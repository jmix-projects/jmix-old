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
import io.jmix.core.entity.EntityAccessor;
import io.jmix.core.entity.KeyValueEntity;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;

public class ClientDataManager implements DataManager {

    @Inject
    protected ServerDataManager service;

    @Inject
    protected Metadata metadata;

    @Inject
    protected EntityStates entityStates;

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
    public EntitySet save(SaveContext context) {
        return EntitySet.of(service.save(context));
    }

    @Override
    public List<KeyValueEntity> loadValues(ValueLoadContext context) {
        return service.loadValues(context);
    }

    @Override
    public EntitySet save(Entity... entities) {
        return save(new SaveContext().saving(entities));
    }

    @Override
    public <E extends Entity> E save(E entity) {
        return save(new SaveContext().saving(entity)).optional(entity).orElseThrow(IllegalStateException::new);
    }

    @Override
    public void remove(Entity... entities) {
        save(new SaveContext().removing(entities));
    }

    @Override
    public <T extends Entity> T create(Class<T> entityClass) {
        return metadata.create(entityClass);
    }

    @Override
    public <T extends Entity<K>, K> T getReference(Class<T> entityClass, K id) {
        T entity = metadata.create(entityClass);
        EntityAccessor.setEntityId(entity, id);
        entityStates.makePatch(entity);
        return entity;
    }
}
