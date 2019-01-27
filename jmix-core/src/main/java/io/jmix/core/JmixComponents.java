package io.jmix.core;

import com.google.common.base.Splitter;
import io.jmix.core.annotation.JmixComponent;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Holds the list of {@link JmixComponentDescriptor}s.
 */
public class JmixComponents implements BeanFactoryPostProcessor {

    public static final Pattern SEPARATOR_PATTERN = Pattern.compile("\\s");

    private final Logger log = LoggerFactory.getLogger(JmixComponents.class);

    private final List<JmixComponentDescriptor> components = new ArrayList<>();

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
            JmixComponent jmixComponent = AnnotationUtils.findAnnotation(beanClass, JmixComponent.class);
            if (jmixComponent == null) {
                continue;
            }
            String compId = getComponentId(jmixComponent, beanClass);
            if (!componentIds.contains(compId)) {
                componentIds.add(compId);
            }
            JmixComponentDescriptor compDescriptor = get(compId);
            if (compDescriptor == null) {
                compDescriptor = new JmixComponentDescriptor(compId);
                load(compDescriptor, jmixComponent.dependsOn());
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

        Set<String> propertyNames = new HashSet<>();
        for (JmixComponentDescriptor component : components) {
            propertyNames.addAll(component.getPropertyNames());
        }
        for (String name : propertyNames) {
            System.setProperty(name, getProperty(name));
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

    private void load(JmixComponentDescriptor component, Class[] dependsOn) {
        try {
            for (Class<?> depClass : dependsOn) {
                JmixComponent jmixComponent = AnnotationUtils.findAnnotation(depClass, JmixComponent.class);
                if (jmixComponent == null) {
                    log.warn("Dependency class {} is not annotated with @JmixComponent, ignoring it", depClass.getName());
                    continue;
                }
                String depCompId = getComponentId(jmixComponent, depClass);
                JmixComponentDescriptor depComp = get(depCompId);
                if (depComp == null) {
                    depComp = new JmixComponentDescriptor(depCompId);
                    load(depComp, jmixComponent.dependsOn());
                    components.add(depComp);
                }
                component.addDependency(depComp);
            }

            Element propertiesEl = getDescriptorDoc(component).getRootElement().element("properties");
            for (Element propertyEl : propertiesEl.elements("property")) {
                String name = propertyEl.attributeValue("name");
                String value = propertyEl.attributeValue("value");
                boolean overwrite = Boolean.parseBoolean(propertyEl.attributeValue("overwrite"));
                component.setProperty(name, value, !overwrite);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading app component '" + component + "'", e);
        }
    }

    private Document getDescriptorDoc(JmixComponentDescriptor component) {
        String descriptorPath = component.getDescriptorPath();
        try (InputStream descrStream = getClass().getClassLoader().getResourceAsStream(descriptorPath)) {
            if (descrStream == null)
                throw new RuntimeException("Jmix component descriptor was not found in '" + descriptorPath + "'");

            SAXReader reader = new SAXReader();
            return reader.read(new InputStreamReader(descrStream, StandardCharsets.UTF_8));
        } catch (IOException | DocumentException e) {
            throw new RuntimeException("Error reading Jmix component descriptor '" + descriptorPath + "'", e);
        }
    }

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

        return String.join(" ", values);
    }

    private Iterable<String> split(String compValue) {
        return Splitter.on(SEPARATOR_PATTERN).omitEmptyStrings().split(compValue);
    }
}