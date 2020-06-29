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
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.data.impl.context.InMemoryReadEntityContext;
import io.jmix.security.model.RowLevelPolicy;
import io.jmix.security.model.RowLevelPolicyAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(InMemoryReadEntityConstraint.NAME)
public class InMemoryReadEntityConstraint extends AbstractInMemoryRowLevelConstraint<InMemoryReadEntityContext> {
    public static final String NAME = "sec_InMemoryReadEntityConstraint";

    @Autowired
    public InMemoryReadEntityConstraint(CurrentAuthentication currentAuthentication,
                                        Metadata metadata) {
        super(currentAuthentication, metadata);
    }

    @Override
    public Class<InMemoryReadEntityContext> getContextType() {
        return InMemoryReadEntityContext.class;
    }

    @Override
    public void applyTo(InMemoryReadEntityContext context) {
        for (Entity entity : context.getEntities()) {
            if (!isPermitted(entity)) {
                context.addDeniedEntity(entity);
            }
        }
    }
}
