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
import io.jmix.core.EntityStates;
import io.jmix.core.Metadata;
import io.jmix.core.MetadataTools;
import io.jmix.core.entity.EntityValues;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.data.impl.context.InMemoryReadAttributesContext;
import io.jmix.security.model.RowLevelPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class InMemoryReadAttributesConstraint extends AbstractRowLevelReadConstraint<InMemoryReadAttributesContext> {
    @Autowired
    protected Metadata metadata;
    @Autowired
    protected MetadataTools metadataTools;
    @Autowired
    protected EntityStates entityStates;

    @Autowired
    public InMemoryReadAttributesConstraint(CurrentAuthentication currentAuthentication) {
        super(currentAuthentication);
    }

    @Override
    public Class<InMemoryReadAttributesContext> getContextType() {
        return InMemoryReadAttributesContext.class;
    }

    @Override
    public void applyTo(InMemoryReadAttributesContext context) {
        Set<Entity> visited = new LinkedHashSet<>();
        for (Entity entity : context.getEntities()) {
            traverseEntities(entity, visited, (ownerEntity, propertyEntity, propertyName) -> {
                if (!isPermitted(propertyEntity)) {
                    context.addDeniedEntity(ownerEntity, propertyEntity, propertyName);
                    return false;
                }
                return true;
            });
        }
    }

    protected void traverseEntities(Entity entity, Set<Entity> visited, Visitor visitor) {
        if (visited.contains(entity)) {
            return;
        }

        visited.add(entity);

        for (MetaProperty property : metadata.getClass(entity.getClass()).getProperties()) {
            if (isPersistentEntityProperty(property) && entityStates.isLoaded(entity, property.getName())) {
                Object value = EntityValues.getValue(entity, property.getName());
                if (value instanceof Collection<?>) {
                    //noinspection unchecked
                    for (Entity item : (Collection<Entity>) value) {
                        if (visitor.visit(entity, item, property.getName())) {
                            traverseEntities(item, visited, visitor);
                        }
                    }
                } else if (value instanceof Entity) {
                    Entity valueEntity = (Entity) value;
                    if (visitor.visit(entity, valueEntity, property.getName())) {
                        traverseEntities(valueEntity, visited, visitor);
                    }
                }
            }
        }
    }

    protected boolean isPermitted(Entity entity) {
        MetaClass entityClass = metadata.getClass(entity.getClass());
        boolean permitted = true;
        for (RowLevelPolicy policy : getRowLevelPolicies(entityClass)) {
            if (Objects.equals(policy.getAction(), action) && policy.getPredicate() != null) {
                permitted = permitted && policy.getPredicate().test(entity);
            }
        }
        return permitted;
    }

    protected boolean isPersistentEntityProperty(MetaProperty metaProperty) {
        return metaProperty.getRange().isClass() && metadataTools.isPersistent(metaProperty);
    }

    protected interface Visitor {
        boolean visit(Entity ownerEntity, Entity propertyEntity, String propertyName);
    }
}
