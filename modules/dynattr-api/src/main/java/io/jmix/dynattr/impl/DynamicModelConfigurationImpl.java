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

package io.jmix.dynattr.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.jmix.core.*;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.data.PersistenceHints;
import io.jmix.data.StoreAwareLocator;
import io.jmix.dynattr.AttributeDefinition;
import io.jmix.dynattr.DynamicModelConfiguration;
import io.jmix.dynattr.impl.model.Category;
import io.jmix.dynattr.impl.model.CategoryAttribute;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component(DynamicModelConfiguration.NAME)
public class DynamicModelConfigurationImpl implements DynamicModelConfiguration {

    @Inject
    protected StoreAwareLocator storeAwareLocator;
    @Inject
    protected ExtendedEntities extendedEntities;
    @Inject
    protected Metadata metadata;

    protected volatile Cache cache;

    protected String dynamicAttributesStore = Stores.MAIN;

    private static final Logger log = LoggerFactory.getLogger(DynamicModelConfigurationImpl.class);

    @Override
    public boolean hasAttributesForClass(MetaClass metaClass) {
        Collection<AttributeDefinition> attributes = getAttributesByClass(metaClass);
        return !attributes.isEmpty();
    }

    @Override
    public Collection<AttributeDefinition> getAttributesByClass(MetaClass metaClass) {
        return getCache().getAttributesForMetaClass(metaClass);
    }

    @Override
    @Nullable
    public AttributeDefinition findAttributeByCode(MetaClass metaClass, String code) {
        return getCache().getAttributeForMetaClass(metaClass, code);
    }

    @Override
    public void reload() {
        cache = doLoadCache();
    }

    protected Cache getCache() {
        if (cache == null) {
            Cache newCache = doLoadCache();
            if (cache == null) {
                cache = newCache;
            }
        }
        return cache;
    }

    protected Cache doLoadCache() {
        return storeAwareLocator.getTransactionTemplate(dynamicAttributesStore)
                .execute(transactionStatus -> {
                    EntityManager entityManager = storeAwareLocator.getEntityManager(dynamicAttributesStore);

                    FetchPlan fetchPlan = FetchPlanBuilder.of(Category.class)
                            .addFetchPlan(FetchPlan.LOCAL)
                            .add("categoryAttrs", builder -> {
                                builder.addFetchPlan(FetchPlan.LOCAL);
                                builder.add("category", FetchPlan.LOCAL);
                                builder.add("defaultEntity", FetchPlan.LOCAL);
                            })
                            .build();

                    List<Category> resultList = entityManager.createQuery("select c from sys$Category c", Category.class)
                            .setHint(PersistenceHints.FETCH_PLAN, fetchPlan)
                            .getResultList();

                    Multimap<String, Category> categoriesCache = HashMultimap.create();
                    Map<String, Map<String, AttributeDefinition>> attributesCache = new LinkedHashMap<>();

                    for (Category category : resultList) {
                        MetaClass metaClass = metadata.findClass(category.getEntityType());
                        if (metaClass != null) {
                            metaClass = extendedEntities.getOriginalOrThisMetaClass(metaClass);
                            categoriesCache.put(metaClass.getName(), category);
                            Map<String, AttributeDefinition> attributes = attributesCache.computeIfAbsent(metaClass.getName(),
                                    k -> new LinkedHashMap<>());
                            for (CategoryAttribute attribute : category.getCategoryAttrs()) {
                                attributes.put(attribute.getCode(), attribute);
                            }
                        } else {
                            log.warn("Could not resolve meta class name {} for the category {}.",
                                    category.getEntityType(), category.getName());
                        }
                    }

                    return new Cache(categoriesCache, attributesCache);
                });
    }


    protected class Cache {
        protected final Multimap<String, Category> categories;
        protected final Map<String, Map<String, AttributeDefinition>> attributes;

        public Cache(Multimap<String, Category> categories, Map<String, Map<String, AttributeDefinition>> attributes) {
            this.categories = categories;
            this.attributes = attributes;
        }

        public Collection<AttributeDefinition> getAttributesForMetaClass(MetaClass metaClass) {
            Collection<Category> entityCategories = categories.get(extendedEntities.getOriginalOrThisMetaClass(metaClass).getName());
            return entityCategories.stream()
                    .flatMap(ca -> ca.getCategoryAttrs().stream())
                    .filter(a -> StringUtils.isNotBlank(a.getCode()))
                    .collect(Collectors.toList());
        }

        @Nullable
        public AttributeDefinition getAttributeForMetaClass(MetaClass metaClass, String code) {
            MetaClass targetMetaClass = extendedEntities.getOriginalOrThisMetaClass(metaClass);
            Map<String, AttributeDefinition> targetAttributes = attributes.get(targetMetaClass.getName());
            if (targetAttributes != null) {
                return targetAttributes.get(code);
            }

            return null;
        }
    }
}
