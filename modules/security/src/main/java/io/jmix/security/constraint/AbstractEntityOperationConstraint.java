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

package io.jmix.security.constraint;

import io.jmix.core.constraint.AccessConstraint;
import io.jmix.core.context.AccessContext;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.core.security.EntityAttrAccess;
import io.jmix.core.security.EntityOp;
import io.jmix.security.authentication.SecuredAuthentication;
import io.jmix.security.model.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public abstract class AbstractEntityOperationConstraint<T extends AccessContext> implements AccessConstraint<T> {
    protected CurrentAuthentication currentAuthentication;

    public AbstractEntityOperationConstraint(CurrentAuthentication currentAuthentication) {
        this.currentAuthentication = currentAuthentication;
    }

    protected boolean isEntityOpPermitted(MetaClass metaClass, EntityOp entityOp) {
        //TODO: wildcards
        return getResourcePolicies(metaClass.getName(), ResourcePolicyType.ENTITY).stream()
                .anyMatch(policy -> Objects.equals(policy.getEffect(), ResourcePolicyEffect.ALLOW) &&
                        Objects.equals(policy.getAction(), getEntityAction(entityOp)));
    }

    protected boolean isEntityAttrViewPermitted(MetaPropertyPath metaPropertyPath) {
        //TODO: wildcards
        for (MetaProperty metaProperty : metaPropertyPath.getMetaProperties()) {
            if (!isEntityAttrPermitted(metaProperty.getDomain(), metaProperty.getName(), EntityAttrAccess.VIEW)) {
                return false;
            }
        }
        return true;
    }

    protected boolean isEntityAttrPermitted(MetaClass metaClass, String name, EntityAttrAccess attrAccess) {
        return getResourcePolicies(metaClass.getName() + "." + name, ResourcePolicyType.ENTITY_ATTRIBUTE).stream()
                .anyMatch(policy -> Objects.equals(policy.getEffect(), ResourcePolicyEffect.ALLOW) &&
                        Objects.equals(policy.getAction(), getEntityAttrAction(attrAccess)));
    }

    protected Collection<ResourcePolicy> getResourcePolicies(String resource, String type) {
        if (currentAuthentication.getAuthentication() instanceof SecuredAuthentication) {
            SecuredAuthentication authentication = (SecuredAuthentication) currentAuthentication.getAuthentication();
            return authentication.getResourceByResourceAndType(resource, type);
        }
        return Collections.emptyList();
    }

    protected String getEntityAction(EntityOp entityOp) {
        switch (entityOp) {
            case READ:
                return EntityPolicyAction.READ.getId();
            case CREATE:
                return EntityPolicyAction.CREATE.getId();
            case UPDATE:
                return EntityPolicyAction.UPDATE.getId();
            case DELETE:
                return EntityPolicyAction.DELETE.getId();
        }
        return null;
    }

    protected String getEntityAttrAction(EntityAttrAccess attrAccess) {
        switch (attrAccess) {
            case VIEW:
                return EntityAttributePolicyAction.READ.getId();
            case MODIFY:
                return EntityAttributePolicyAction.UPDATE.getId();
        }
        return null;
    }
}
