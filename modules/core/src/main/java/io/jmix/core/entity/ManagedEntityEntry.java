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

import java.io.Serializable;

public interface ManagedEntityEntry<K> extends Serializable {

    ManagedEntity<K> getSource();

    K getEntityId();

    void setEntityId(K id);

    <T> T getEntityValue(String name);

    default void setEntityValue(String name, Object value) {
        setEntityValue(name, value, true);
    }

    void setEntityValue(String name, Object value, boolean checkEquals);

    boolean isNew();

    boolean isManaged();

    boolean isDetached();

    boolean isRemoved();

    void setNew(boolean _new);

    void setManaged(boolean managed);

    void setDetached(boolean detached);

    void setRemoved(boolean removed);

    SecurityState getSecurityState();

    void setSecurityState(SecurityState securityState);

    /**
     * Add listener to track attributes changes.
     *
     * @param listener listener
     */
    void addPropertyChangeListener(EntityPropertyChangeListener listener);

    /**
     * Remove listener.
     *
     * @param listener listener to remove
     */
    void removePropertyChangeListener(EntityPropertyChangeListener listener);

    /**
     * Remove all {@link EntityPropertyChangeListener}s.
     */
    void removeAllListeners();

    void firePropertyChanged(String propertyName, Object prev, Object curr);

    /**
     * Copies the state.
     */
    void copy(ManagedEntityEntry<?> entry);

    void clear();
}
