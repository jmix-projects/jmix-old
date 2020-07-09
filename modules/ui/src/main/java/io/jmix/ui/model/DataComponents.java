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

package io.jmix.ui.model;

import io.jmix.core.AccessManager;
import io.jmix.core.Entity;
import io.jmix.core.Metadata;
import io.jmix.core.entity.EntityValues;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.ui.context.UiReadEntityAttributeContext;
import io.jmix.ui.context.UiReadEntityContext;
import io.jmix.ui.model.impl.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Factory bean for data API components.
 */
@Component("jmix_DataComponents")
public class DataComponents implements ApplicationContextAware {

    @Autowired
    protected Metadata metadata;

    @Autowired
    protected SorterFactory sorterFactory;

    @Autowired
    protected AccessManager accessManager;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Creates {@code DataContext}.
     */
    public DataContext createDataContext() {
        return new DataContextImpl(applicationContext);
    }

    /**
     * Creates {@code InstanceContainer}.
     */
    public <E extends Entity> InstanceContainer<E> createInstanceContainer(Class<E> entityClass) {
        return new InstanceContainerImpl<>(applicationContext, metadata.getClass(entityClass));
    }

    /**
     * Creates {@code InstancePropertyContainer}.
     */
    @SuppressWarnings("unchecked")
    public <E extends Entity> InstancePropertyContainer<E> createInstanceContainer(Class<E> entityClass,
                                                                                   InstanceContainer<? extends Entity> masterContainer,
                                                                                   String property) {
        InstancePropertyContainerImpl<E> container = new InstancePropertyContainerImpl<>(
                applicationContext, metadata.getClass(entityClass), masterContainer, property);

        UiReadEntityContext readEntityContext = accessManager.applyRegisteredConstraints(
                new UiReadEntityContext(masterContainer.getEntityMetaClass()));

        UiReadEntityAttributeContext readAttributeContext = accessManager.applyRegisteredConstraints(
                new UiReadEntityAttributeContext(masterContainer.getEntityMetaClass(), property));

        if (readAttributeContext.isPermitted()
                && readEntityContext.isPermitted()) {
            masterContainer.addItemChangeListener(e -> {
                Entity item = masterContainer.getItemOrNull();
                container.setItem(item != null ? EntityValues.getValue(item, property) : null);
            });

            masterContainer.addItemPropertyChangeListener(e -> {
                if (e.getProperty().equals(property)) {
                    container.setItem((E) e.getValue());
                }
            });
        }

        return container;
    }

    /**
     * Creates {@code CollectionContainer}.
     */
    public <E extends Entity> CollectionContainer<E> createCollectionContainer(Class<E> entityClass) {
        CollectionContainerImpl<E> container = new CollectionContainerImpl<>(
                applicationContext, metadata.getClass(entityClass));
        container.setSorter(sorterFactory.createCollectionContainerSorter(container, null));
        return container;
    }

    /**
     * Creates {@code CollectionPropertyContainer}.
     */
    @SuppressWarnings("unchecked")
    public <E extends Entity> CollectionPropertyContainer<E> createCollectionContainer(Class<E> entityClass,
                                                                                       InstanceContainer<? extends Entity> masterContainer,
                                                                                       String property) {
        CollectionPropertyContainerImpl<E> container = new CollectionPropertyContainerImpl<>(
                applicationContext, metadata.getClass(entityClass), masterContainer, property);
        container.setSorter(sorterFactory.createCollectionPropertyContainerSorter(container));

        UiReadEntityContext readEntityContext = accessManager.applyRegisteredConstraints(
                new UiReadEntityContext(masterContainer.getEntityMetaClass()));

        UiReadEntityAttributeContext readAttributeContext = accessManager.applyRegisteredConstraints(
                new UiReadEntityAttributeContext(masterContainer.getEntityMetaClass(), property));

        if (readAttributeContext.isPermitted()
                && readEntityContext.isPermitted()) {
            masterContainer.addItemChangeListener(e -> {
                Entity item = masterContainer.getItemOrNull();
                container.setItems(item != null ? EntityValues.getValue(item, property) : null);
            });

            masterContainer.addItemPropertyChangeListener(e -> {
                if (e.getProperty().equals(property)) {
                    container.setDisconnectedItems((Collection<E>) e.getValue());
                }
            });
        }

        return container;
    }

    /**
     * Creates {@code KeyValueContainer}.
     */
    public KeyValueContainer createKeyValueContainer() {
        return new KeyValueContainerImpl(applicationContext);
    }

    /**
     * Creates {@code KeyValueContainer} for the given MetaClass.
     */
    public KeyValueContainer createKeyValueContainer(MetaClass metaClass) {
        return new KeyValueContainerImpl(applicationContext, metaClass);
    }

    /**
     * Creates {@code KeyValueCollectionContainer}.
     */
    public KeyValueCollectionContainer createKeyValueCollectionContainer() {
        KeyValueCollectionContainerImpl container = new KeyValueCollectionContainerImpl(applicationContext);
        container.setSorter(sorterFactory.createCollectionContainerSorter(container, null));
        return container;
    }

    /**
     * Creates {@code InstanceLoader}.
     */
    public <E extends Entity> InstanceLoader<E> createInstanceLoader() {
        return new InstanceLoaderImpl<>(applicationContext);
    }

    /**
     * Creates {@code CollectionLoader}.
     */
    public <E extends Entity> CollectionLoader<E> createCollectionLoader() {
        return new CollectionLoaderImpl<>(applicationContext);
    }

    /**
     * Creates {@code KeyValueCollectionLoader}.
     */
    public KeyValueCollectionLoader createKeyValueCollectionLoader() {
        return new KeyValueCollectionLoaderImpl(applicationContext);
    }

    /**
     * Creates {@code KeyValueInstanceLoader}.
     */
    public KeyValueInstanceLoader createKeyValueInstanceLoader() {
        return new KeyValueInstanceLoaderImpl(applicationContext);
    }
}
