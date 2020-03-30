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
import io.jmix.core.impl.method.ArgumentResolverComposite;
import io.jmix.core.impl.method.MethodArgumentsProvider;
import io.jmix.core.metamodel.annotations.InstanceName;
import io.jmix.core.metamodel.datatypes.DatatypeRegistry;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.security.UserSessionSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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

    protected ArgumentResolverComposite resolvers;

    protected MethodArgumentsProvider methodArgumentsProvider;

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

    public ArgumentResolverComposite getResolvers() {
        return resolvers;
    }

    public void setResolvers(ArgumentResolverComposite resolvers) {
        this.resolvers = resolvers;
        this.methodArgumentsProvider = new MethodArgumentsProvider(resolvers);
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
        Collection<MetaProperty> properties = getInstanceNameProperties(metaClass);
        if (properties.isEmpty() && useOriginal) {
            MetaClass original = extendedEntities.getOriginalMetaClass(metaClass);
            if (original != null) {
                properties = getInstanceNameProperties(original);
            }
        }
        return properties;
    }

    private Collection<MetaProperty> getInstanceNameProperties(MetaClass metaClass) {
        final Collection<MetaProperty> properties = new HashSet<>();
        MetaProperty nameProperty = metaClass.getProperties().stream()
                .filter(p -> p.getAnnotatedElement().getAnnotation(InstanceName.class) != null)
                .findFirst().orElse(null);
        if (nameProperty != null) {
            properties.add(nameProperty);
            String relatedPropertiesStr = nameProperty.getAnnotatedElement().getAnnotation(InstanceName.class).value();
            if (StringUtils.isNotBlank(relatedPropertiesStr)) {
                Arrays.stream(relatedPropertiesStr.split(","))
                        .map(metaClass::getProperty)
                        .forEach(properties::add);
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
