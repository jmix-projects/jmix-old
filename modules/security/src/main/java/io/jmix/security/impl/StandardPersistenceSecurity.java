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

package io.jmix.security.impl;

import com.google.common.collect.Multimap;
import io.jmix.core.*;
import io.jmix.core.entity.EntityValues;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.security.ConstraintOperationType;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.core.security.Security;
import io.jmix.data.RowLevelSecurityException;
import io.jmix.data.StoreAwareLocator;
import io.jmix.security.SecurityTokenException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.function.BiPredicate;

import static java.lang.String.format;

@Component(PersistenceSecurity.NAME)
public class StandardPersistenceSecurity implements PersistenceSecurity {

    private static final Logger log = LoggerFactory.getLogger(StandardPersistenceSecurity.class);

    @Autowired
    protected SecurityTokenManager securityTokenManager;

    @Autowired
    protected StoreAwareLocator storeAwareLocator;

    @Autowired
    protected ReferenceToEntitySupport referenceToEntitySupport;

    @Autowired
    protected EntityStates entityStates;

    @Autowired
    protected Security security;

    @Autowired
    protected Metadata metadata;

    @Autowired
    protected CurrentAuthentication currentAuthentication;

    @Autowired
    protected MetadataTools metadataTools;

    @Override
    public void assertToken(Entity entity) {
        EntityEntry entityEntry = entity.__getEntityEntry();
        if (entityEntry.getSecurityState().getSecurityToken() == null) {
            assertSecurityConstraints(entity, (e, metaProperty) -> entityStates.isDetached(entity)
                    && !entityStates.isLoaded(entity, metaProperty.getName()));
        }
    }

    protected void assertSecurityConstraints(Entity entity, BiPredicate<Entity, MetaProperty> predicate) {
        MetaClass metaClass = metadata.getClass(entity.getClass());
        for (MetaProperty metaProperty : metaClass.getProperties()) {
            if (metaProperty.getRange().isClass() && metadataTools.isPersistent(metaProperty)) {
                if (predicate.test(entity, metaProperty)) {
                    continue;
                }
                if (security.hasInMemoryConstraints(metaProperty.getRange().asClass(), ConstraintOperationType.READ,
                        ConstraintOperationType.ALL)) {
                    throw new RowLevelSecurityException(format("Could not read security token from entity %s, " +
                                    "even though there are active READ/ALL constraints for the property: %s", entity,
                            metaProperty.getName()),
                            metaClass.getName());
                }
            }
        }
    }


//        } catch (JpqlSyntaxException e) {
//            log.error("Syntax errors found in constraint's JPQL expressions. Entity [{}]. Constraint ID [{}].",
//                    entityName, constraint.getId(), e);
//
//            throw new RowLevelSecurityException(
//                    "Syntax errors found in constraint's JPQL expressions. Please see the logs.", entityName);
//        } catch (Exception e) {
//            log.error("An error occurred when applying security constraint. Entity [{}]. Constraint ID [{}].",
//                    entityName, constraint.getId(), e);
//
//            throw new RowLevelSecurityException(
//                    "An error occurred when applying security constraint. Please see the logs.", entityName);
//        }


    @SuppressWarnings("unchecked")
    protected void applyConstraints(Entity entity, Set<EntityId> handled) {
        MetaClass metaClass = metadata.getClass(entity);
        EntityId entityId = new EntityId(referenceToEntitySupport.getReferenceId(entity), metaClass.getName());
        if (handled.contains(entityId)) {
            return;
        }
        handled.add(entityId);
        if (!entity.__getEntityEntry().isEmbeddable()) {
            EntityEntry entityEntry = entity.__getEntityEntry();
            Multimap<String, Object> filteredData = entityEntry.getSecurityState().getErasedData();
            for (MetaProperty property : metaClass.getProperties()) {
                if (metadataTools.isPersistent(property) && entityStates.isLoaded(entity, property.getName())) {
                    Object value = EntityValues.getValue(entity, property.getName());
                    if (value instanceof Collection) {
                        Collection entities = (Collection) value;
                        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext(); ) {
                            Entity item = iterator.next();
                            if (filteredData != null && filteredData.containsEntry(property.getName(),
                                    referenceToEntitySupport.getReferenceId(item))) {
                                iterator.remove();
                            } else {
                                applyConstraints(item, handled);
                            }
                        }
                    } else if (value instanceof Entity) {
                        if (filteredData != null && filteredData.containsEntry(property.getName(),
                                referenceToEntitySupport.getReferenceId((Entity) value))) {
                            EntityValues.setValue((Entity) value, property.getName(), null);
                        } else {
                            applyConstraints((Entity) value, handled);
                        }
                    }
                }
            }
        }
    }
}
