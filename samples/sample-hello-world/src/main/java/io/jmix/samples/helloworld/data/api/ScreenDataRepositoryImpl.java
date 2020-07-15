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

package io.jmix.samples.helloworld.data.api;

import io.jmix.core.DataManager;
import io.jmix.core.JmixEntity;
import io.jmix.core.LoadContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.List;

public class ScreenDataRepositoryImpl<T extends JmixEntity> implements ScreenDataRepository<T> {

    @Autowired
    private DataManager dataManager;

    @Override
    public List<T> loadEntitiesList(LoadContext<T> loadContext) {
        return dataManager.loadList(loadContext);
    }

    @Override
    @Nullable
    public T loadEntity(LoadContext<T> context) {
        return dataManager.load(context);
    }
}
