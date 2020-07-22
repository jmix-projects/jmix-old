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

import io.jmix.core.context.AccessContext;
import io.jmix.core.metamodel.model.MetaClass;

public class CRUDEntityContext implements AccessContext {
    public static final String NAME = "data_ReadEntityContext";

    protected final MetaClass entityClass;
    protected boolean readPermitted = true;

    public CRUDEntityContext(MetaClass entityClass) {
        this.entityClass = entityClass;
    }

    public MetaClass getEntityClass() {
        return entityClass;
    }

    public boolean isCreatePermitted() {
        return false;
    }

    public void setCreateDenied() {
    }

    public boolean isReadPermitted() {
        return readPermitted;
    }

    public void setReadDenied() {
        this.readPermitted = false;
    }

    public boolean isUpdatePermitted() {
        return false;
    }

    public void setUpdateDenied() {
    }

    public boolean isDeletePermitted() {
        return false;
    }
}
