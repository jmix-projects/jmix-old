/*
 * Copyright 2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.core;

import io.jmix.core.annotation.JmixComponent;
import io.jmix.core.annotation.JmixProperty;
import io.jmix.core.compatibility.AppContext;
import io.jmix.core.security.JmixCoreSecurityConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.ClassUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Configuration of the core module.
 *
 * <p>It implements {@link BeanDefinitionRegistryPostProcessor} with {@link PriorityOrdered} in order to be processed
 * before {@code @Conditional} annotations that depend on {@code @JmixProperty} values.
 */
@Configuration
@Import(JmixCoreSecurityConfiguration.class)
@ComponentScan
@JmixComponent(dependsOn = {}, properties = {
        @JmixProperty(name = "jmix.securityImplementation", value = "core"),
        @JmixProperty(name = "jmix.viewsConfig", value = "io/jmix/core/views.xml"),
        @JmixProperty(name = "cuba.confDir", value = "./conf")
})
public class JmixCoreConfiguration implements BeanDefinitionRegistryPostProcessor, PriorityOrdered, EnvironmentAware {

    private Environment environment;

    private JmixComponents jmixComponents;

    private static final Logger log = LoggerFactory.getLogger(JmixCoreConfiguration.class);

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public JmixComponents jmixComponents() {
        return jmixComponents;
    }

    @EventListener
    @Order(Events.HIGHEST_CORE_PRECEDENCE + 10)
    void onApplicationContextRefreshFirst(ContextRefreshedEvent event) {
        AppContext.Internals.setApplicationContext(event.getApplicationContext());
    }

    @EventListener
    @Order(Events.LOWEST_CORE_PRECEDENCE - 10)
    void onApplicationContextRefreshLast(ContextRefreshedEvent event) {
        AppContext.Internals.startContext();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<JmixComponentDescriptor> components = new ArrayList<>();
        List<String> componentIds = new ArrayList<>();

        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (!(beanDefinition instanceof AnnotatedBeanDefinition)) {
                continue;
            }
            if (!((AnnotatedBeanDefinition) beanDefinition).getMetadata().hasAnnotation(JmixComponent.class.getName())
                    || ((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata() != null) {
                continue;
            }
            String beanClassName = beanDefinition.getBeanClassName();

            ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
            if (beanClassLoader == null) {
                throw new RuntimeException("BeanClassLoader is null");
            }
            Class<?> beanClass;
            try {
                beanClass = beanClassLoader.loadClass(beanClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            JmixComponent componentAnnotation = AnnotationUtils.findAnnotation(beanClass, JmixComponent.class);
            if (componentAnnotation == null) {
                continue;
            }
            String compId = getComponentId(componentAnnotation, beanClass);
            if (!componentIds.contains(compId)) {
                componentIds.add(compId);
            }

            JmixComponentDescriptor compDescriptor = components.stream()
                    .filter(descriptor -> descriptor.getId().equals(compId))
                    .findAny()
                    .orElseGet(() -> {
                        JmixComponentDescriptor descriptor = new JmixComponentDescriptor(compId);
                        load(descriptor, componentAnnotation, components);
                        return descriptor;
                    });
            if (!components.contains(compDescriptor))
                components.add(compDescriptor);
        }

        components.sort((c1, c2) -> {
            int res = c1.compareTo(c2);
            if (res != 0)
                return res;
            else
                return componentIds.indexOf(c1.getId()) - componentIds.indexOf(c2.getId());
        });

        log.info("Using Jmix components: {}", components);

        jmixComponents = new JmixComponents(environment, components);

        if (environment instanceof ConfigurableEnvironment) {
            MutablePropertySources sources = ((ConfigurableEnvironment) environment).getPropertySources();
            sources.addLast(new JmixPropertySource(jmixComponents));
        } else {
            throw new IllegalStateException("Not a ConfigurableEnvironment, cannot register JmixComponents property source");
        }

    }

    private String getComponentId(JmixComponent jmixComponent, Class<?> aClass) {
        String compId = jmixComponent.id();
        if ("".equals(compId)) {
            compId = aClass.getPackage().getName();
        }
        return compId;
    }

    private void load(JmixComponentDescriptor component, JmixComponent componentAnnotation,
                      List<JmixComponentDescriptor> components) {
        for (Class<?> depClass : componentAnnotation.dependsOn()) {
            JmixComponent depComponentAnnotation = AnnotationUtils.findAnnotation(depClass, JmixComponent.class);
            if (depComponentAnnotation == null) {
                log.warn("Dependency class {} is not annotated with {}, ignoring it", depClass.getName(), JmixComponent.class.getName());
                continue;
            }
            String depCompId = getComponentId(depComponentAnnotation, depClass);

            JmixComponentDescriptor depComp = components.stream()
                    .filter(descriptor -> descriptor.getId().equals(depCompId))
                    .findAny()
                    .orElseGet(() -> {
                        JmixComponentDescriptor descriptor = new JmixComponentDescriptor(depCompId);
                        load(descriptor, depComponentAnnotation, components);
                        components.add(descriptor);
                        return descriptor;
                    });
            component.addDependency(depComp);
        }

        for (JmixProperty propertyAnn : componentAnnotation.properties()) {
            component.setProperty(propertyAnn.name(), propertyAnn.value(), propertyAnn.append());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1000; // to be before ConfigurationClassPostProcessor
    }

    private static class JmixPropertySource extends EnumerablePropertySource<JmixComponents> {

        public JmixPropertySource(JmixComponents source) {
            super("JmixComponents properties", source);
        }

        @Nonnull
        @Override
        public String[] getPropertyNames() {
            Set<String> propertyNames = new HashSet<>();
            for (JmixComponentDescriptor component : source.getComponents()) {
                propertyNames.addAll(component.getPropertyNames());
            }
            return propertyNames.toArray(new String[0]);
        }

        @Override
        public Object getProperty(String name) {
            return source.getProperty(name);
        }
    }
}