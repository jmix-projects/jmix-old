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

package io.jmix.remoting.gateway;

import io.jmix.core.*;
import io.jmix.core.entity.Entity;
import io.jmix.core.entity.KeyValueEntity;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;

public class ClientDataManager extends DataManagerSupport implements DataManager {

    @Inject
    protected ServerDataManager service;

    @Nullable
    @Override
    public <E extends Entity> E load(LoadContext<E> context) {
        return service.load(context);
    }

    @Override
    public <E extends Entity> List<E> loadList(LoadContext<E> context) {
        return service.loadList(context);
    }

    @Override
    public long getCount(LoadContext<? extends Entity> context) {
        return service.getCount(context);
    }

    @Override
    public EntitySet commit(CommitContext context) {
        return EntitySet.of(service.commit(context));
    }

    @Override
    public List<KeyValueEntity> loadValues(ValueLoadContext context) {
        return service.loadValues(context);
    }

    @Override
    public DataManager secure() {
        return this;
    }
}
