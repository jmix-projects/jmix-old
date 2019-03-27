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

import com.google.common.base.Splitter;
import io.jmix.core.annotation.JmixComponent;
import io.jmix.core.annotation.JmixProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Holds the list of {@link JmixComponentDescriptor}s.
 */
@ParametersAreNonnullByDefault
public class JmixComponents implements BeanFactoryPostProcessor, EnvironmentAware {

    public static final Pattern SEPARATOR_PATTERN = Pattern.compile("\\s");

    private final Logger log = LoggerFactory.getLogger(JmixComponents.class);

    private final List<JmixComponentDescriptor> components = new ArrayList<>();

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        List<String> componentIds = new ArrayList<>();

        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            if (!(beanDefinition instanceof AnnotatedBeanDefinition)) {
                continue;
            }
            if (!((AnnotatedBeanDefinition) beanDefinition).getMetadata().hasAnnotation(JmixComponent.class.getName())
                    || ((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata() != null) {
                continue;
            }
            String beanClassName = beanDefinition.getBeanClassName();
            ClassLoader beanClassLoader = beanFactory.getBeanClassLoader();
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
            JmixComponentDescriptor compDescriptor = get(compId);
            if (compDescriptor == null) {
                compDescriptor = new JmixComponentDescriptor(compId);
                load(compDescriptor, componentAnnotation);
            }
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

        if (environment instanceof ConfigurableEnvironment) {
            MutablePropertySources sources = ((ConfigurableEnvironment) environment).getPropertySources();
            sources.addLast(new JmixPropertySource(this));
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

    /**
     * @return the list of components
     */
    public List<JmixComponentDescriptor> getComponents() {
        return Collections.unmodifiableList(components);
    }

    /**
     * Get a component by Id.
     * @return component or null if not found
     */
    @Nullable
    public JmixComponentDescriptor get(String componentId) {
        for (JmixComponentDescriptor component : components) {
            if (component.getId().equals(componentId))
                return component;
        }
        return null;
    }

    private void load(JmixComponentDescriptor component, JmixComponent componentAnnotation) {
        for (Class<?> depClass : componentAnnotation.dependsOn()) {
            JmixComponent depComponentAnnotation = AnnotationUtils.findAnnotation(depClass, JmixComponent.class);
            if (depComponentAnnotation == null) {
                log.warn("Dependency class {} is not annotated with {}, ignoring it", depClass.getName(), JmixComponent.class.getName());
                continue;
            }
            String depCompId = getComponentId(depComponentAnnotation, depClass);
            JmixComponentDescriptor depComp = get(depCompId);
            if (depComp == null) {
                depComp = new JmixComponentDescriptor(depCompId);
                load(depComp, depComponentAnnotation);
                components.add(depComp);
            }
            component.addDependency(depComp);
        }

        for (JmixProperty propertyAnn : componentAnnotation.properties()) {
            component.setProperty(propertyAnn.name(), propertyAnn.value(), propertyAnn.append());
        }
    }

    @Nullable
    public String getProperty(String name) {
        List<String> values = new ArrayList<>();

        List<JmixComponentDescriptor> components = getComponents();
        ListIterator<JmixComponentDescriptor> iterator = components.listIterator(components.size());

        int index;
        while (iterator.hasPrevious()) {
            JmixComponentDescriptor component = iterator.previous();

            String compValue = component.getProperty(name);
            if (StringUtils.isNotEmpty(compValue)) {
                if (component.isAdditiveProperty(name)) {
                    index = 0;
                    for (String valuePart : split(compValue)) {
                        if (!values.contains(valuePart)) {
                            values.add(index, valuePart);
                            index++;
                        }
                    }
                } else {
                    values.add(0, compValue);
                    // we found overwrite, stop iteration
                    break;
                }
            }
        }

        return values.isEmpty() ? null : String.join(" ", values);
    }

    private Iterable<String> split(String compValue) {
        return Splitter.on(SEPARATOR_PATTERN).omitEmptyStrings().split(compValue);
    }

    private static class JmixPropertySource extends EnumerablePropertySource<JmixComponents> {

        public JmixPropertySource(JmixComponents source) {
            super("JmixComponents properties", source);
        }

        @Nonnull
        @Override
        public String[] getPropertyNames() {
            Set<String> propertyNames = new HashSet<>();
            for (JmixComponentDescriptor component : source.components) {
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