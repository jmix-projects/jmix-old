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
import java.beans.PropertyChangeListener;

import static io.jmix.core.metamodel.model.utils.EntityPaths.formatValuePath;
import static io.jmix.core.metamodel.model.utils.EntityPaths.parseValuePath;

public class EntityAccessor {

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


    }

    /**
     * Get an attribute value.
     *
     * @param name attribute name according to JavaBeans notation
     * @return attribute value
     */
    @Nullable
    public static <T> T getEntityValue(Entity<?> entity, String name) {
        return null;

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
     * Add listener to track attributes changes.
     *
     * @param listener listener
     */
    public static void addPropertyChangeListener(Entity<?> entity, EntityPropertyChangeListener listener) {

    }

    /**
     * Remove listener.
     *
     * @param listener listener to remove
     */
    public static void removePropertyChangeListener(Entity<?> entity, EntityPropertyChangeListener listener) {

    }

    /**
     * Remove all {@link PropertyChangeListener}s.
     */
    public static void removeAllListeners(Entity<?> entity) {

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
}
