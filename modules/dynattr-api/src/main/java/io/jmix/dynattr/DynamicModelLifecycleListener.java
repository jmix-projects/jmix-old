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

package io.jmix.dynattr;

import io.jmix.core.Entity;
import io.jmix.core.LoadContext;
import io.jmix.data.impl.OrmLifecycleListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;

@Component(DynamicModelLifecycleListener.NAME)
public class DynamicModelLifecycleListener implements OrmLifecycleListener {
    public static final String NAME = "jmix_DynamicModelLifecycleListener";

    @Inject
    protected DynamicModelManager dynamicModelManager;

    @Override
    public void onLoad(Collection<Entity> entities, LoadContext loadContext) {
        if (loadContext.isLoadDynamicAttributes()) {
            dynamicModelManager.loadValues(entities, loadContext.getFetchPlan());
        }
    }

    @Override
    public void onSave(Collection<Entity> entities) {
        dynamicModelManager.storeValues(entities);
    }
}
