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

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Base class for persistent embeddable entities.
 */
@MappedSuperclass
@io.jmix.core.metamodel.annotations.MetaClass(name = "sys$EmbeddableEntity")
public abstract class EmbeddableEntity implements Entity<Object>, ManagedEntity<Object> {

    private static final long serialVersionUID = 266201862280559076L;

    @Transient
    protected ManagedEntityEntry<Object> __entityEntry;

    protected static class EmbeddableEntityEntry extends BaseManagedEntityEntry<Object> {
        public EmbeddableEntityEntry(EmbeddableEntity entity) {
            super(entity);
        }

        @Override
        public void setEntityId(Object id) {
        }

        @Override
        public Object getEntityId() {
            return source;
        }
    }

    @Override
    public ManagedEntityEntry<Object> __getEntityEntry() {
        if (__entityEntry == null) {
            __entityEntry = new EmbeddableEntityEntry(this);
        }
        return __entityEntry;
    }
}
