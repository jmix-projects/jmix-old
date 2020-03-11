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
package io.jmix.data.entity;

import io.jmix.core.entity.Entity;
import io.jmix.core.entity.annotation.UnavailableInSecurityConstraints;

import javax.persistence.MappedSuperclass;

/**
 * Base class for entities.
 * <br>
 * When choosing a base class for your entity, consider more specific base classes defining the primary key type:
 * <ul>
 * <li>{@link BaseUuidEntity}</li>
 * <li>{@link BaseLongIdEntity}</li>
 * <li>{@link BaseIntegerIdEntity}</li>
 * <li>{@link BaseStringIdEntity}</li>
 * </ul>
 * or most commonly used {@link StandardEntity}.
 */
@MappedSuperclass
@io.jmix.core.metamodel.annotations.MetaClass(name = "sys$BaseGenericIdEntity")
@UnavailableInSecurityConstraints
public abstract class BaseGenericIdEntity<T> implements Entity<T> {

    private static final long serialVersionUID = -8400641366148656528L;

    public abstract void setId(T id);

    public abstract T getId();
}
