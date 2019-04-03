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

package io.jmix.samples.customsecurity;

import io.jmix.core.security.ConstraintOperationType;
import io.jmix.core.security.EntityAttrAccess;
import io.jmix.core.security.EntityOp;
import io.jmix.core.security.Security;
import io.jmix.core.entity.Entity;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import org.springframework.stereotype.Component;

@Component
public class CustomSecurityImpl implements Security {
    @Override
    public boolean isScreenPermitted(String windowAlias) {
        return false;
    }

    @Override
    public boolean isEntityOpPermitted(MetaClass metaClass, EntityOp entityOp) {
        return false;
    }

    @Override
    public boolean isEntityOpPermitted(Class<?> entityClass, EntityOp entityOp) {
        return false;
    }

    @Override
    public boolean isEntityAttrPermitted(MetaClass metaClass, String property, EntityAttrAccess access) {
        return false;
    }

    @Override
    public boolean isEntityAttrPermitted(Class<?> entityClass, String property, EntityAttrAccess access) {
        return false;
    }

    @Override
    public boolean isEntityAttrUpdatePermitted(MetaClass metaClass, String propertyPath) {
        return false;
    }

    @Override
    public boolean isEntityAttrUpdatePermitted(MetaPropertyPath metaPropertyPath) {
        return false;
    }

    @Override
    public boolean isEntityAttrReadPermitted(MetaPropertyPath metaPropertyPath) {
        return false;
    }

    @Override
    public boolean isEntityAttrReadPermitted(MetaClass metaClass, String propertyPath) {
        return false;
    }

    @Override
    public boolean isSpecificPermitted(String name) {
        return false;
    }

    @Override
    public void checkSpecificPermission(String name) {

    }

    @Override
    public boolean isPermitted(Entity entity, ConstraintOperationType operationType) {
        return false;
    }

    @Override
    public boolean isPermitted(Entity entity, String customCode) {
        return false;
    }

    @Override
    public boolean hasConstraints() {
        return false;
    }

    @Override
    public boolean hasConstraints(MetaClass metaClass) {
        return false;
    }

    @Override
    public boolean hasInMemoryConstraints(MetaClass metaClass, ConstraintOperationType... operationTypes) {
        return false;
    }

    @Override
    public Object evaluateConstraintScript(Entity entity, String groovyScript) {
        return null;
    }
}
