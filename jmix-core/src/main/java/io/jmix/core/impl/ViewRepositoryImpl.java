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

package io.jmix.core.impl;

import io.jmix.core.View;
import io.jmix.core.ViewRepository;
import io.jmix.core.entity.Entity;
import io.jmix.core.metamodel.model.MetaClass;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Collection;

// todo impl
@Component(ViewRepository.NAME)
public class ViewRepositoryImpl implements ViewRepository {
    @Override
    public View getView(Class<? extends Entity> entityClass, String name) {
        return null;
    }

    @Override
    public View getView(MetaClass metaClass, String name) {
        return null;
    }

    @Nullable
    @Override
    public View findView(MetaClass metaClass, String name) {
        return null;
    }

    @Override
    public Collection<String> getViewNames(MetaClass metaClass) {
        return null;
    }

    @Override
    public Collection<String> getViewNames(Class<? extends Entity> entityClass) {
        return null;
    }
}
