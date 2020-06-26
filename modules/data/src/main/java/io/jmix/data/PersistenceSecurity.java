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

package io.jmix.data;

import io.jmix.core.FetchPlan;
import io.jmix.core.Entity;
import io.jmix.data.impl.JmixQuery;

import java.util.Collection;

/**
 * Applies security on the data access layer.
 */
public interface PersistenceSecurity {

    String NAME = "data_PersistenceSecurity";

    /**
     * Applies in-memory constraints to the entity by filtered data
     * @param entity -
     */
    void applyConstraints(Entity entity);

    /**
     * Applies in-memory constraints to the entity fields by filtered data
     * @param entities - collection of entities
     */
    void applyConstraints(Collection<Entity> entities);

    /**
     * Reads security token and restores security state
     * @param entity - entity to restore security state
     */
    void restoreSecurityState(Entity entity);

    /**
     * Restores filtered data from security token
     * @param entity - entity to restore filtered data
     */
    void restoreFilteredData(Entity entity);

    /**
     * Reads security token and restores security state and filtered data
     * @param entity - entity to restore
     */
    default void restoreSecurityStateAndFilteredData(Entity entity) {
        restoreSecurityState(entity);
        restoreFilteredData(entity);
    }

    /**
     * Validate that security token exists for specific cases.
     * For example, security constraints exists
     * @param entity - entity to check security token
     */
    void assertToken(Entity entity);

    /**
     * Calculate filtered data
     * @param entity for which will calculate filtered data
     */
    void calculateFilteredData(Entity entity);

    /**
     * Calculate filtered data
     * @param entities - collection of entities for which will calculate filtered data
     */
    void calculateFilteredData(Collection<Entity> entities);
}
