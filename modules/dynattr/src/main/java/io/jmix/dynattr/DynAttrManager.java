/*
 * Copyright (c) 2008-2016 Haulmont.
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
 *
 */

package io.jmix.dynattr;

import io.jmix.core.FetchPlan;
import io.jmix.core.JmixEntity;
import io.jmix.core.constraint.AccessConstraint;

import javax.annotation.Nullable;
import java.util.Collection;

public interface DynAttrManager {
    String NAME = "dynattr_DynamicModelManager";

    /**
     * Fetch dynamic attributes from dynamic attributes store for each entity
     */
    void loadValues(Collection<JmixEntity> entities, @Nullable FetchPlan fetchPlan, Collection<AccessConstraint<?>> accessConstraints);

    /**
     * Store dynamic attributes from the entity to store
     */
    void storeValues(Collection<JmixEntity> entities, Collection<AccessConstraint<?>> accessConstraints);
}