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

package com.haulmont.cuba.core.global.impl;

import io.jmix.core.DevelopmentException;
import io.jmix.core.InstanceNameProvider;
import io.jmix.core.impl.InstanceNameProviderImpl;
import com.haulmont.chile.core.annotations.NamePattern;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * {@link InstanceNameProvider} implementation for CUBA compatible module to support {@link NamePattern} annotation
 */
public class CubaInstanceNameProviderImpl extends InstanceNameProviderImpl {

    private static final Pattern INSTANCE_NAME_SPLIT_PATTERN = Pattern.compile("[,;]");

    @Nullable
    @Override
    public NamePatternRec parseNamePattern(MetaClass metaClass) {
        NamePatternRec namePattern = super.parseNamePattern(metaClass);
        if (namePattern != null) {
            return namePattern;
        }
        Map attributes = (Map) metaClass.getAnnotations().get(NamePattern.class.getName());
        if (attributes == null)
            return null;
        String pattern = (String) attributes.get("value");
        if (StringUtils.isBlank(pattern))
            return null;

        int pos = pattern.indexOf("|");
        if (pos < 0)
            throw new DevelopmentException("Invalid name pattern: " + pattern);

        String format = StringUtils.substring(pattern, 0, pos);
        String trimmedFormat = format.trim();
        String methodName = trimmedFormat.startsWith("#") ? trimmedFormat.substring(1) : null;
        String fieldsStr = StringUtils.substring(pattern, pos + 1);
        String[] fields = INSTANCE_NAME_SPLIT_PATTERN.split(fieldsStr);
        return new NamePatternRec(format, methodName, fields);
    }

    /**
     * Return a collection of properties included into entity's name pattern (see {@link NamePattern}).
     *
     * @param metaClass   entity metaclass
     * @param useOriginal if true, and if the given metaclass doesn't define a {@link NamePattern} and if it is an
     *                    extended entity, this method tries to find a name pattern in an original entity
     * @return collection of the name pattern properties
     */
    @Nonnull
    public Collection<MetaProperty> getNamePatternProperties(MetaClass metaClass, boolean useOriginal) {
        Collection<MetaProperty> properties = new ArrayList<>();
        String pattern = (String) metadataTools.getMetaAnnotationAttributes(metaClass.getAnnotations(), NamePattern.class).get("value");
        if (pattern == null && useOriginal) {
            MetaClass original = extendedEntities.getOriginalMetaClass(metaClass);
            if (original != null) {
                pattern = (String) metadataTools.getMetaAnnotationAttributes(original.getAnnotations(), NamePattern.class).get("value");
            }
        }
        if (!StringUtils.isBlank(pattern)) {
            String value = StringUtils.substringAfter(pattern, "|");
            String[] fields = StringUtils.splitPreserveAllTokens(value, ",");
            for (String field : fields) {
                String fieldName = StringUtils.trim(field);

                MetaProperty property = metaClass.findProperty(fieldName);
                if (property != null) {
                    properties.add(metaClass.findProperty(fieldName));
                } else {
                    throw new DevelopmentException(
                            String.format("Property '%s' is not found in %s", field, metaClass.toString()),
                            "NamePattern", pattern);
                }
            }
        }
        return properties;
    }
}
