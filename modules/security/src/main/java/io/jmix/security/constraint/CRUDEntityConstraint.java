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

import io.jmix.core.security.CurrentAuthentication;
import io.jmix.data.impl.context.CRUDEntityContext;
import io.jmix.security.model.EntityPolicyAction;
import org.springframework.stereotype.Component;

@Component(CRUDEntityConstraint.NAME)
public class CRUDEntityConstraint extends AbstractEntityOperationConstraint<CRUDEntityContext> {
    public static final String NAME = "sec_CRUDEntityConstraint";

    public CRUDEntityConstraint(CurrentAuthentication currentAuthentication) {
        super(currentAuthentication);
    }

    @Override
    public Class<CRUDEntityContext> getContextType() {
        return CRUDEntityContext.class;
    }

    @Override
    public void applyTo(CRUDEntityContext context) {
        if (!isEntityOpPermitted(context.getEntityClass(), EntityPolicyAction.CREATE)) {
            context.setCreateDenied();
        }
        if (!isEntityOpPermitted(context.getEntityClass(), EntityPolicyAction.READ)) {
            context.setReadDenied();
        }
        if (!isEntityOpPermitted(context.getEntityClass(), EntityPolicyAction.UPDATE)) {
            context.setUpdateDenied();
        }
    }
}
