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

import io.jmix.core.CommitContext;
import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.core.ValueLoadContext;
import io.jmix.core.entity.Entity;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.remoting.annotation.Remote;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@Component(ServerDataManager.NAME)
@Profile("remoting")
@Remote
public class ServerDataManagerImpl implements ServerDataManager {

    @Inject
    protected DataManager dataManager;

    @Override
    public Set<Entity> commit(CommitContext context) {
        context.setAuthorizationRequired(true);
        return dataManager.commit(context);
    }

    @Override
    @Nullable
    public <E extends Entity> E load(LoadContext<E> context) {
        context.setAuthorizationRequired(true);
        return dataManager.load(context);
    }

    @Override
    public <E extends Entity> List<E> loadList(LoadContext<E> context) {
        context.setAuthorizationRequired(true);
        return dataManager.loadList(context);
    }

    @Override
    public long getCount(LoadContext<? extends Entity> context) {
        context.setAuthorizationRequired(true);
        return dataManager.getCount(context);
    }

    @Override
    public List<KeyValueEntity> loadValues(ValueLoadContext context) {
        context.setAuthorizationRequired(true);
        return dataManager.loadValues(context);
    }
}