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

package io.jmix.data.impl.context;

import io.jmix.core.Entity;
import io.jmix.core.context.AccessContext;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.security.EntityOp;

import java.util.function.Predicate;

public class InMemoryCRUDEntityContext implements AccessContext {
    protected final MetaClass entityClass;
    protected final EntityOp entityOp;
    protected Predicate<Entity> predicate;

    public InMemoryCRUDEntityContext(MetaClass entityClass, EntityOp entityOp) {
        this.entityClass = entityClass;
        this.entityOp = entityOp;
    }

    public MetaClass getEntityClass() {
        return entityClass;
    }

    public EntityOp getEntityOp() {
        return entityOp;
    }

    public void addPredicate(Predicate<Entity> predicate) {
        if (this.predicate == null) {
            this.predicate = predicate;
        } else {
            this.predicate = this.predicate.and(predicate);
        }
    }

    public Predicate<Entity> getPredicate() {
        return predicate;
    }

    public boolean isPermitted(Entity entity) {
        return predicate == null || predicate.test(entity);
    }
}
