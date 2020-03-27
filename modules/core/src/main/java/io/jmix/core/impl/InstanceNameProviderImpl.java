/*
 * Copyright 2020 Haulmont.
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

package io.jmix.core.impl;

import io.jmix.core.*;
import io.jmix.core.entity.EntityValues;
import io.jmix.core.event.AppContextInitializedEvent;
import io.jmix.core.impl.method.ContextArgumentResolverComposite;
import io.jmix.core.impl.method.MethodArgumentsProvider;
import io.jmix.core.metamodel.annotations.InstanceName;
import io.jmix.core.metamodel.datatypes.DatatypeRegistry;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.security.UserSessionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.el.MethodNotFoundException;
import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.jmix.core.commons.util.Preconditions.checkNotNullArgument;

@Component(InstanceNameProvider.NAME)
public class InstanceNameProviderImpl implements InstanceNameProvider {

    @Inject
    protected Metadata metadata;

    @Inject
    protected ExtendedEntities extendedEntities;

    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    protected DatatypeRegistry datatypeRegistry;

    @Inject
    protected Messages messages;

    @Inject
    protected MetadataTools metadataTools;

    @Inject
    protected BeanLocator beanLocator;

    private ContextArgumentResolverComposite resolvers;

    private MethodArgumentsProvider methodArgumentsProvider;

    private final Logger logger = LoggerFactory.getLogger(MetadataTools.class);

    public static class NamePatternRec {
        /**
         * Name pattern string format
         */
        public final String format;
        /**
         * Formatting method name or null
         */
        public final String methodName;
        /**
         * Array of property names
         */
        public final String[] fields;

        public NamePatternRec(String format, @Nullable String methodName, String[] fields) {
            this.fields = fields;
            this.format = format;
            this.methodName = methodName;
        }
    }

    @EventListener
    void onInitialized(AppContextInitializedEvent event) {
        if (resolvers == null) {
            resolvers = new ContextArgumentResolverComposite(beanLocator);
        }
        methodArgumentsProvider = new MethodArgumentsProvider(resolvers);
    }

    public ContextArgumentResolverComposite getResolvers() {
        return resolvers;
    }

    public void setResolvers(ContextArgumentResolverComposite resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public String getInstanceName(Entity instance) {
        checkNotNullArgument(instance, "instance is null");

        MetaClass metaClass = metadata.getClass(instance.getClass());

        NamePatternRec rec = parseNamePattern(metaClass);
        if (rec == null) {
            return instance.toString();
        }

        if (rec.methodName != null) {
            try {
                Method method = Stream.of(metaClass.getJavaClass().getDeclaredMethods())
                        .filter(m -> m.getName().equals(rec.methodName))
                        .findFirst().orElseThrow(NoSuchMethodException::new);
                Object result = method.invoke(instance, methodArgumentsProvider.getMethodArgumentValues(method));
                return (String) result;
            } catch (Exception e) {
                throw new RuntimeException("Error getting instance name", e);
            }
        }

        Object[] values = new Object[rec.fields.length];
        for (int i = 0; i < rec.fields.length; i++) {
            String fieldName = rec.fields[i];
            MetaProperty property = metaClass.getProperty(fieldName);

            Object value = EntityValues.getValue(instance, fieldName);
            values[i] = metadataTools.format(value, property);
        }

        return String.format(rec.format, values);
    }

    @Override
    public Collection<MetaProperty> getNamePatternProperties(MetaClass metaClass, boolean useOriginal) {
        Collection<MetaProperty> properties;
        properties = metaClass.getProperties().stream()
                .filter(p -> p.getAnnotations().get(InstanceName.class.getName()) != null)
                .collect(Collectors.toList());
        if (properties.isEmpty() && useOriginal) {
            MetaClass original = extendedEntities.getOriginalMetaClass(metaClass);
            if (original != null) {
                properties = original.getProperties().stream()
                        .filter(p -> p.getAnnotations().get(InstanceName.class.getName()) != null)
                        .collect(Collectors.toList());
            }
        }
        return properties;
    }

    @Nullable
    public NamePatternRec parseNamePattern(MetaClass metaClass) {
        MetaProperty nameProperty = metaClass.getProperties().stream()
                .filter(p -> p.getAnnotatedElement().getAnnotation(InstanceName.class) != null)
                .findFirst().orElse(null);

        if (nameProperty != null) {
            return new NamePatternRec("%s", null, new String[]{nameProperty.getName()});
        }

        Method nameMethod = Stream.of(metaClass.getJavaClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(InstanceName.class))
                .findFirst().orElse(null);

        if (nameMethod != null) {
            return new NamePatternRec("%s", nameMethod.getName(), new String[]{});
        }
        return null;
    }


}
