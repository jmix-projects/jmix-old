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

package io.jmix.core.entity;

import io.jmix.core.metamodel.model.EntityPropertyPath;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.Collection;

import static io.jmix.core.metamodel.model.utils.EntityPaths.formatValuePath;
import static io.jmix.core.metamodel.model.utils.EntityPaths.parseValuePath;

public class EntityAccessor {

    @SuppressWarnings("unchecked")
    public static <K> K getEntityId(Entity<?> entity) {
        return (K)((ManagedEntity<?>) entity).__getEntityEntry().getEntityId();
    }

    public static <K> void setEntityId(Entity<K> entity, K key) {
        ((ManagedEntity<K>) entity).__getEntityEntry().setEntityId(key);
    }

    /**
     * Set an attribute value.
     * <br>
     * An implementor should first read a current value of the attribute, and then call an appropriate setter
     * method only if the new value differs. This ensures triggering of {@link EntityPropertyChangeListener}s only if the attribute
     * was actually changed.
     *
     * @param name  attribute name according to JavaBeans notation
     * @param value attribute value
     */
    public static void setEntityValue(Entity<?> entity, String name, Object value) {
        ((ManagedEntity<?>) entity).__getEntityEntry().setEntityValue(name, value, true);
    }

    /**
     * Set an attribute value.
     * <br>
     * An implementor should first read a current value of the attribute, and then call an appropriate setter
     * method only if the new value differs. This ensures triggering of {@link EntityPropertyChangeListener}s only if the attribute
     * was actually changed.
     *
     * @param name        attribute name according to JavaBeans notation
     * @param value       attribute value
     * @param checkEquals check equals for previous and new value.
     *                    If flag is true and objects equals, then setter will not be invoked
     */
    public static void setEntityValue(Entity<?> entity, String name, Object value, boolean checkEquals) {
        ((ManagedEntity<?>) entity).__getEntityEntry().setEntityValue(name, value, checkEquals);
    }

    /**
     * Get an attribute value.
     *
     * @param name attribute name according to JavaBeans notation
     * @return attribute value
     */
    @Nullable
    public static <T> T getEntityValue(Entity<?> entity, String name) {
        return ((ManagedEntity<?>) entity).__getEntityEntry().getEntityValue(name);
    }

    /**
     * Get an attribute value. Locates the attribute by the given path in object graph starting from this instance.
     * <br>
     * The path must consist of attribute names according to JavaBeans notation, separated by dots, e.g.
     * {@code car.driver.name}.
     *
     * @param propertyPath path to an attribute
     * @return attribute value. If any traversing attribute value is null or is not an {@link Entity}, this method
     * stops here and returns this value.
     */
    @Nullable
    public static <T> T getEntityValueEx(Entity<?> entity, String propertyPath) {
        return getEntityValueEx(entity, parseValuePath(propertyPath));
    }

    /**
     * Get an attribute value. Locates the attribute by the given path in object graph starting from this instance.
     * <br>
     * The path must consist of attribute names according to JavaBeans notation, separated by dots, e.g.
     * {@code car.driver.name}.
     *
     * @param propertyPath path to an attribute
     * @return attribute value. If any traversing attribute value is null or is not an {@link Entity}, this method
     * stops here and returns this value.
     */
    @Nullable
    public static <T> T getEntityValueEx(Entity<?> entity, EntityPropertyPath propertyPath) {
        if (propertyPath.isDirectProperty()) {
            return getEntityValue(entity, propertyPath.getFirstPropertyName());
        } else {
            return getEntityValueEx(entity, propertyPath.getPropertyNames());
        }
    }

    /**
     * Set an attribute value. Locates the attribute by the given path in object graph starting from this instance.
     * <br> The path must consist of attribute names according to JavaBeans notation, separated by dots, e.g.
     * {@code car.driver.name}.
     * <br> In the example above this method first gets value of {@code car.driver} attribute, and if it is not
     * null and is an {@link Entity}, sets value of {@code name} attribute in it.
     * <br> An implementor should first read a current value of the attribute, and then call an appropriate setter
     * method only if the new value differs. This ensures triggering of {@link EntityPropertyChangeListener}s only if the attribute
     * was actually changed.
     *
     * @param propertyPath path to an attribute
     * @param value        attribute value
     */
    public static void setEntityValueEx(Entity<?> entity, String propertyPath, Object value) {
        setEntityValueEx(entity, parseValuePath(propertyPath), value);
    }

    /**
     * Set an attribute value. Locates the attribute by the given path in object graph starting from this instance.
     * <br> The path must consist of attribute names according to JavaBeans notation, separated by dots, e.g.
     * {@code car.driver.name}.
     * <br> In the example above this method first gets value of {@code car.driver} attribute, and if it is not
     * null and is an {@link Entity}, sets value of {@code name} attribute in it.
     * <br> An implementor should first read a current value of the attribute, and then call an appropriate setter
     * method only if the new value differs. This ensures triggering of {@link EntityPropertyChangeListener}s only if the attribute
     * was actually changed.
     *
     * @param propertyPath path to an attribute
     * @param value        attribute value
     */
    public static void setEntityValueEx(Entity<?> entity, EntityPropertyPath propertyPath, Object value) {
        if (propertyPath.isDirectProperty()) {
            setEntityValue(entity, propertyPath.getFirstPropertyName(), value);
        } else {
            String[] properties = propertyPath.getPropertyNames();
            setEntityValueEx(entity, properties, value);
        }
    }

    /**
     * Set value of an attribute according to the rules described in {@link EntityAccessor#setEntityValueEx(Entity, String, Object)}.
     *
     * @param entity     instance
     * @param properties path to the attribute
     * @param value      attribute value
     */
    public static void setEntityValueEx(Entity<?> entity, String[] properties, Object value) {
        if (properties.length > 1) {

            if (properties.length == 2) {
                entity = getEntityValue(entity, properties[0]);
            } else {
                String[] subarray = ArrayUtils.subarray(properties, 0, properties.length - 1);
                String path = formatValuePath(subarray);
                entity = getEntityValueEx(entity, path);
            }

            if (entity != null) {
                setEntityValue(entity, properties[properties.length - 1], value);
            }
        } else {
            setEntityValue(entity, properties[0], value);
        }
    }

    /**
     * Get value of an attribute according to the rules described in {@link EntityAccessor#getEntityValueEx(Entity, String)}.
     *
     * @param entity     entity
     * @param properties path to the attribute
     * @return attribute value
     */
    @SuppressWarnings("unchecked")
    public static <T> T getEntityValueEx(Entity<?> entity, String[] properties) {
        if (properties == null) {
            return null;
        }

        Object currentValue = null;
        Entity<?> currentEntity = entity;
        for (String property : properties) {
            if (currentEntity == null) {
                break;
            }

            currentValue = getEntityValue(entity, property);

            if (currentValue == null) {
                break;
            }


            currentEntity = currentValue instanceof Entity ? (Entity<?>) currentValue : null;
        }

        return (T) currentValue;
    }

    /**
     * Used by {@link } to check whether a property value has been changed.
     *
     * @param a an object
     * @param b an object
     * @return true if {@code a} equals to {@code b}, but in case of {@code a} is {@link } or {@code Collection} returns
     * true only if {@code a} is the same instance as {@code b}
     */
    public static boolean propertyValueEquals(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a instanceof Entity || a instanceof Collection) {
            return false;
        }
        return a != null && a.equals(b);
    }
}
