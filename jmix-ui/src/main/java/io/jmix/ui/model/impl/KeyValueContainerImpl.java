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

package io.jmix.ui.model.impl;

import io.jmix.core.metamodel.datatypes.Datatype;
import io.jmix.core.impl.keyvalue.KeyValueMetaClass;
import io.jmix.core.impl.keyvalue.KeyValueMetaProperty;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.ui.model.KeyValueContainer;
import org.springframework.context.ApplicationContext;

import javax.annotation.Nullable;

public class KeyValueContainerImpl extends InstanceContainerImpl<KeyValueEntity> implements KeyValueContainer {

    private String idName;

    public KeyValueContainerImpl(ApplicationContext applicationContext) {
        super(applicationContext, new KeyValueMetaClass());
    }

    @Override
    public KeyValueMetaClass getEntityMetaClass() {
        return (KeyValueMetaClass) super.getEntityMetaClass();
    }

    @Override
    public KeyValueContainer setIdName(String name) {
        idName = name;
        return this;
    }

    @Override
    public String getIdName() {
        return idName;
    }

    @Override
    public KeyValueContainer addProperty(String name) {
        getEntityMetaClass().addProperty(new KeyValueMetaProperty(getEntityMetaClass(), name, String.class));
        return this;
    }

    @Override
    public KeyValueContainer addProperty(String name, Class aClass) {
        getEntityMetaClass().addProperty(new KeyValueMetaProperty(getEntityMetaClass(), name, aClass));
        return this;
    }

    @Override
    public KeyValueContainer addProperty(String name, Datatype datatype) {
        getEntityMetaClass().addProperty(new KeyValueMetaProperty(getEntityMetaClass(), name, datatype));
        return this;
    }

    @Override
    public void setItem(@Nullable KeyValueEntity item) {
        if (item != null) {
            updateEntityMetadata(item);
        }
        super.setItem(item);
    }

    protected void updateEntityMetadata(KeyValueEntity entity) {
        entity.setInstanceMetaClass(entityMetaClass);
        if (idName != null)
            entity.setIdName(idName);
    }
}
