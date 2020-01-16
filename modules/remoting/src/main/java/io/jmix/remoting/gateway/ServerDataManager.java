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
import io.jmix.core.LoadContext;
import io.jmix.core.ValueLoadContext;
import io.jmix.core.entity.Entity;
import io.jmix.core.entity.KeyValueEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public interface ServerDataManager {

    String NAME = "jmix_DataManagerService";

    Set<Entity> commit(CommitContext context);

    @Nullable
    <E extends Entity> E load(LoadContext<E> context);

    <E extends Entity> List<E> loadList(LoadContext<E> context);

    long getCount(LoadContext<? extends Entity> context);

    List<KeyValueEntity> loadValues(ValueLoadContext context);
}
