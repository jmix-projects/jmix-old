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

import io.jmix.core.Entity;
import io.jmix.core.Metadata;
import io.jmix.core.constraint.RowLevelConstraint;
import io.jmix.core.context.AccessContext;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.security.authentication.SecuredAuthentication;
import io.jmix.security.model.RowLevelPolicy;
import io.jmix.security.model.RowLevelPolicyAction;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public abstract class AbstractInMemoryRowLevelConstraint<T extends AccessContext> extends AbstractRowLevelConstraint<T> {
    protected Metadata metadata;

    public AbstractInMemoryRowLevelConstraint(CurrentAuthentication currentAuthentication,
                                              Metadata metadata) {
        super(currentAuthentication);
        this.metadata = metadata;
    }

    protected boolean isPermitted(Entity entity) {
        MetaClass entityClass = metadata.getClass(entity.getClass());
        boolean permitted = true;
        for (RowLevelPolicy policy : getRowLevelPolicies(entityClass)) {
            if (policy.getAction() == RowLevelPolicyAction.READ && policy.getPredicate() != null) {
                permitted = permitted && policy.getPredicate().test(entity);
            }
        }
        return permitted;
    }
}
