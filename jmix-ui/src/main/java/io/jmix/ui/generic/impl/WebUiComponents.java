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

package io.jmix.ui.generic.impl;

import com.google.common.reflect.TypeToken;
import io.jmix.core.BeanLocator;
import io.jmix.core.DevelopmentException;
import io.jmix.core.metamodel.datatypes.DatatypeRegistry;
import io.jmix.ui.components.Component;
import io.jmix.ui.components.Fragment;
import io.jmix.ui.components.HasDatatype;
import io.jmix.ui.components.RootWindow;
import io.jmix.ui.components.impl.WebFragment;
import io.jmix.ui.components.impl.WebRootWindow;
import io.jmix.ui.generic.UiComponents;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Component(UiComponents.NAME)
public class WebUiComponents implements UiComponents {

    @Inject
    protected ApplicationContext applicationContext;
    @Inject
    protected DatatypeRegistry datatypeRegistry;
    /*@Inject
    protected CompositeDescriptorLoader compositeDescriptorLoader;*/ // todo composite
    @Inject
    protected BeanLocator beanLocator;

    protected Map<String, Class<? extends Component>> classes = new ConcurrentHashMap<>();
    protected Map<Class, String> names = new ConcurrentHashMap<>();

    {
        classes.put(RootWindow.NAME, WebRootWindow.class);
        classes.put(Fragment.NAME, WebFragment.class);

        // todo components
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Component> T create(String name) {
        Class<? extends Component> componentClass = classes.get(name);
        if (componentClass == null) {
            throw new IllegalStateException(String.format("Can't find component class for '%s'", name));
        }

        Constructor<? extends Component> constructor;
        try {
            constructor = componentClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format("Unable to get constructor for '%s' component", name), e);
        }

        try {
            Component instance = constructor.newInstance();
            autowireContext(instance);
            initCompositeComponent(instance, componentClass);
            return (T) instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(String.format("Error creating the '%s' component instance", name), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Component> T create(Class<T> type) {
        String name = names.get(type);
        if (name == null) {
            java.lang.reflect.Field nameField;
            try {
                nameField = type.getField("NAME");
                name = (String) nameField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException ignore) {
            }
            if (name == null)
                throw new DevelopmentException(String.format("Class '%s' doesn't have NAME field", type.getName()));
            else
                names.put(type, name);
        }
        return (T) create(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Component> T create(TypeToken<T> type) {
        T t = (T) create((Class) type.getRawType());
        if (t instanceof HasDatatype) {
            Type[] actualTypeArguments = ((ParameterizedType) type.getType()).getActualTypeArguments();
            if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof Class) {
                Class actualTypeArgument = (Class) actualTypeArguments[0];

                ((HasDatatype) t).setDatatype(datatypeRegistry.get(actualTypeArgument));
            }
        }
        return t;
    }

    protected void autowireContext(Component instance) {
        AutowireCapableBeanFactory autowireBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        autowireBeanFactory.autowireBean(instance);

        if (instance instanceof ApplicationContextAware) {
            ((ApplicationContextAware) instance).setApplicationContext(applicationContext);
        }

        if (instance instanceof InitializingBean) {
            try {
                ((InitializingBean) instance).afterPropertiesSet();
            } catch (Exception e) {
                throw new RuntimeException(
                        "Unable to initialize UI Component - calling afterPropertiesSet for " +
                                instance.getClass(), e);
            }
        }
    }

    protected void initCompositeComponent(Component instance, Class<? extends Component> componentClass) {
        // todo composite
        /*if (!(instance instanceof CompositeComponent)) {
            return;
        }

        CompositeComponent compositeComponent = (CompositeComponent) instance;

        CompositeDescriptor descriptor = componentClass.getAnnotation(CompositeDescriptor.class);
        if (descriptor != null) {
            String descriptorPath = descriptor.value();
            if (!descriptorPath.startsWith("/")) {
                String packageName = getPackage(componentClass);
                if (StringUtils.isNotEmpty(packageName)) {
                    String relativePath = packageName.replace('.', '/');
                    descriptorPath = "/" + relativePath + "/" + descriptorPath;
                }
            }
            Component root = processCompositeDescriptor(componentClass, descriptorPath);
            CompositeComponentUtils.setRoot(compositeComponent, root);
        }

        CompositeComponent.CreateEvent event = new CompositeComponent.CreateEvent(compositeComponent);
        CompositeComponentUtils.fireEvent(compositeComponent, CompositeComponent.CreateEvent.class, event);*/
    }

    protected String getPackage(Class<? extends Component> componentClass) {
        Package javaPackage = componentClass.getPackage();
        return javaPackage != null ? javaPackage.getName() : "";
    }

    // todo composite
    /*protected Component processCompositeDescriptor(Class<? extends Component> componentClass, String descriptorPath) {
        CompositeComponentLoaderContext context = new CompositeComponentLoaderContext();
        context.setComponentClass(componentClass);
        context.setDescriptorPath(descriptorPath);
        context.setMessagesPack(getMessagePack(descriptorPath));

        Element element = compositeDescriptorLoader.load(descriptorPath);

        CompositeComponentLayoutLoader layoutLoader =
                beanLocator.getPrototype(CompositeComponentLayoutLoader.NAME, context);

        return layoutLoader.createComponent(element);
    }*/

    protected String getMessagePack(String descriptorPath) {
        if (descriptorPath.contains("/")) {
            descriptorPath = StringUtils.substring(descriptorPath, 0, descriptorPath.lastIndexOf("/"));
        }

        String messagesPack = descriptorPath.replace("/", ".");
        int start = messagesPack.startsWith(".") ? 1 : 0;
        messagesPack = messagesPack.substring(start);
        return messagesPack;
    }

    public void register(String name, Class<? extends Component> componentClass) {
        classes.put(name, componentClass);
    }
}