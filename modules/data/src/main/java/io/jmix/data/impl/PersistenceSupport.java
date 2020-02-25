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

package io.jmix.data.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import io.jmix.core.AppBeans;
import io.jmix.core.Metadata;
import io.jmix.core.Stores;
import io.jmix.core.commons.util.StackTrace;
import io.jmix.core.entity.*;
import io.jmix.data.EntityChangeType;
import io.jmix.data.StoreAwareLocator;
import io.jmix.data.event.EntityChangedEvent;
import io.jmix.data.impl.entitycache.QueryCacheManager;
import io.jmix.data.listener.AfterCompleteTransactionListener;
import io.jmix.data.listener.BeforeCommitTransactionListener;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Component(PersistenceSupport.NAME)
public class PersistenceSupport implements ApplicationContextAware {

    public static final String NAME = "jmix_PersistenceImplSupport";

    public static final String RESOURCE_HOLDER_KEY = ContainerResourceHolder.class.getName();

    public static final String RUNNER_RESOURCE_HOLDER = RunnerResourceHolder.class.getName();

    public static final String PROP_NAME = "jmix.storeName";

    @Inject
    protected StoreAwareLocator storeAwareLocator;

    @Inject
    protected Metadata metadata;

    @Inject
    protected EntityListenerManager entityListenerManager;

    @Inject
    protected QueryCacheManager queryCacheManager;

    @Inject
    protected OrmCacheSupport ormCacheSupport;

    @Inject
    protected EntityChangedEventManager entityChangedEventManager;

    @Inject
    protected EntityChangingEventManager entityChangingEventManager;

    protected List<BeforeCommitTransactionListener> beforeCommitTxListeners;

    protected List<AfterCompleteTransactionListener> afterCompleteTxListeners;

    private static final Logger log = LoggerFactory.getLogger(PersistenceSupport.class.getName());

    private Logger implicitFlushLog = LoggerFactory.getLogger("com.haulmont.cuba.IMPLICIT_FLUSH");

    protected static Set<Entity> createEntitySet() {
        return Sets.newIdentityHashSet();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, BeforeCommitTransactionListener> beforeCommitMap = applicationContext.getBeansOfType(BeforeCommitTransactionListener.class);
        beforeCommitTxListeners = new ArrayList<>(beforeCommitMap.values());
        beforeCommitTxListeners.sort(new OrderComparator());

        Map<String, AfterCompleteTransactionListener> afterCompleteMap = applicationContext.getBeansOfType(AfterCompleteTransactionListener.class);
        afterCompleteTxListeners = new ArrayList<>(afterCompleteMap.values());
        afterCompleteTxListeners.sort(new OrderComparator());
    }

    /**
     * INTERNAL.
     * Register synchronizations with a just started transaction.
     */
    public void registerSynchronizations(String store) {
        log.trace("registerSynchronizations for store '{}'", store);
        getInstanceContainerResourceHolder(store);
        getRunnerResourceHolder(store);
    }

    /**
     * INTERNAL
     */
    public void addBeforeCommitAction(String storeName, Runnable action) {
        RunnerResourceHolder runner = getRunnerResourceHolder(storeName);
        runner.add(action);
    }

    private RunnerResourceHolder getRunnerResourceHolder(String storeName) {
        RunnerResourceHolder runner = (RunnerResourceHolder) TransactionSynchronizationManager.getResource(RUNNER_RESOURCE_HOLDER);
        if (runner == null) {
            runner = new RunnerResourceHolder(storeName);
            TransactionSynchronizationManager.bindResource(RUNNER_RESOURCE_HOLDER, runner);
        } else if (!storeName.equals(runner.getStoreName())) {
            throw new IllegalStateException("Cannot handle entity from " + storeName
                    + " datastore because active transaction is for " + runner.getStoreName());
        }
        if (TransactionSynchronizationManager.isSynchronizationActive() && !runner.isSynchronizedWithTransaction()) {
            runner.setSynchronizedWithTransaction(true);
            TransactionSynchronizationManager.registerSynchronization(new RunnerSynchronization(runner));
        }
        return runner;
    }

    public void registerInstance(Entity entity, javax.persistence.EntityManager entityManager) {
        if (!TransactionSynchronizationManager.isActualTransactionActive())
            throw new RuntimeException("No transaction");

        UnitOfWork unitOfWork = entityManager.unwrap(UnitOfWork.class);
        getInstanceContainerResourceHolder(getStorageName(unitOfWork)).registerInstanceForUnitOfWork(entity, unitOfWork);

        if (entity instanceof ManagedEntity) {
            ((ManagedEntity) entity).getEntityEntry().setDetached(false);
        }
    }

    public void registerInstance(Entity entity, AbstractSession session) {
        // Can be called outside of a transaction when fetching lazy attributes
        if (!TransactionSynchronizationManager.isActualTransactionActive())
            return;

        if (!(session instanceof UnitOfWork))
            throw new RuntimeException("Session is not a UnitOfWork: " + session);

        getInstanceContainerResourceHolder(getStorageName(session)).registerInstanceForUnitOfWork(entity, (UnitOfWork) session);
    }

    public Collection<Entity> getSavedInstances(String storeName) {
        if (!TransactionSynchronizationManager.isActualTransactionActive())
            throw new RuntimeException("No transaction");

        return getInstanceContainerResourceHolder(storeName).getSavedInstances();
    }

    public String getStorageName(Session session) {
        String storeName = (String) session.getProperty(PROP_NAME);
        return Strings.isNullOrEmpty(storeName) ? Stores.MAIN : storeName;
    }

    public ContainerResourceHolder getInstanceContainerResourceHolder(String storeName) {
        ContainerResourceHolder holder =
                (ContainerResourceHolder) TransactionSynchronizationManager.getResource(RESOURCE_HOLDER_KEY);
        if (holder == null) {
            holder = new ContainerResourceHolder(storeName);
            TransactionSynchronizationManager.bindResource(RESOURCE_HOLDER_KEY, holder);
        } else if (!storeName.equals(holder.getStoreName())) {
            throw new IllegalStateException("Cannot handle entity from " + storeName
                    + " datastore because active transaction is for " + holder.getStoreName());
        }

        if (TransactionSynchronizationManager.isSynchronizationActive() && !holder.isSynchronizedWithTransaction()) {
            holder.setSynchronizedWithTransaction(true);
            TransactionSynchronizationManager.registerSynchronization(
                    new ContainerResourceSynchronization(holder, RESOURCE_HOLDER_KEY));
        }
        return holder;
    }

    public void processFlush(javax.persistence.EntityManager entityManager, boolean warnAboutImplicitFlush) {
        UnitOfWork unitOfWork = entityManager.unwrap(UnitOfWork.class);
        String storeName = getStorageName(unitOfWork);
        traverseEntities(getInstanceContainerResourceHolder(storeName), new OnSaveEntityVisitor(storeName), warnAboutImplicitFlush);
    }

    protected void fireBeforeDetachEntityListener(ManagedEntity<?> entity, String storeName) {
        if (!(entity.getEntityEntry().isDetached())) {
            JmixEntityFetchGroup.setAccessLocalUnfetched(false);
            try {
                entityListenerManager.fireListener(entity, EntityListenerType.BEFORE_DETACH, storeName);
            } finally {
                JmixEntityFetchGroup.setAccessLocalUnfetched(true);
            }
        }
    }

    protected static boolean isDeleted(ManagedEntity<?> entity, AttributeChangeListener changeListener) {
        if ((entity instanceof SoftDelete)) {
            ObjectChangeSet changeSet = changeListener.getObjectChangeSet();
            return changeSet != null
                    && changeSet.getAttributesToChanges().containsKey("deleteTs")
                    && ((SoftDelete) entity).isDeleted();

        } else {
            return entity.getEntityEntry().isRemoved();
        }
    }

    protected void traverseEntities(ContainerResourceHolder container, EntityVisitor visitor, boolean warnAboutImplicitFlush) {
        beforeStore(container, visitor, container.getAllInstances(), createEntitySet(), warnAboutImplicitFlush);
    }

    protected void beforeStore(ContainerResourceHolder container, EntityVisitor visitor,
                               Collection<Entity> instances, Set<Entity> processed, boolean warnAboutImplicitFlush) {
        boolean possiblyChanged = false;
        Set<Entity> withoutPossibleChanges = createEntitySet();
        for (Entity entity : instances) {
            processed.add(entity);

            if (!(entity instanceof ChangeTracker))
                continue;

            boolean result = visitor.visit(entity);
            if (!result) {
                withoutPossibleChanges.add(entity);
            }
            possiblyChanged = result || possiblyChanged;
        }
        if (!possiblyChanged)
            return;

        if (warnAboutImplicitFlush) {
            if (implicitFlushLog.isTraceEnabled()) {
                implicitFlushLog.trace("Implicit flush due to query execution, see stack trace for the cause:\n"
                        + StackTrace.asString());
            } else {
                implicitFlushLog.debug("Implicit flush due to query execution");
            }
        }

        Collection<Entity> afterProcessing = container.getAllInstances();
        if (afterProcessing.size() > processed.size()) {
            afterProcessing.removeAll(processed);
            beforeStore(container, visitor, afterProcessing, processed, false);
        }

        if (!withoutPossibleChanges.isEmpty()) {
            afterProcessing = withoutPossibleChanges.stream()
                    .filter(instance -> {
                        ChangeTracker changeTracker = (ChangeTracker) instance;
                        AttributeChangeListener changeListener =
                                (AttributeChangeListener) changeTracker._persistence_getPropertyChangeListener();
                        return changeListener != null
                                && changeListener.hasChanges();
                    })
                    .collect(Collectors.toList());
            if (!afterProcessing.isEmpty()) {
                beforeStore(container, visitor, afterProcessing, processed, false);
            }
        }
    }

    public void detach(javax.persistence.EntityManager entityManager, Entity entity) {
        UnitOfWork unitOfWork = entityManager.unwrap(UnitOfWork.class);
        String storeName = getStorageName(unitOfWork);

        if (entity instanceof ManagedEntity) {
            fireBeforeDetachEntityListener((ManagedEntity) entity, storeName);

            ContainerResourceHolder container = getInstanceContainerResourceHolder(storeName);
            container.unregisterInstance(entity, unitOfWork);
            if (((ManagedEntity) entity).getEntityEntry().isNew()) {
                container.getNewDetachedInstances().add(entity);
            }
        }

        makeDetached(entity);
    }

    protected void makeDetached(Object instance) {
        if (instance instanceof ManagedEntity) {
            ((ManagedEntity) instance).getEntityEntry().setNew(false);
            ((ManagedEntity) instance).getEntityEntry().setManaged(false);
            ((ManagedEntity) instance).getEntityEntry().setDetached(true);
        }
        if (instance instanceof FetchGroupTracker) {
            ((FetchGroupTracker) instance)._persistence_setSession(null);
        }
        if (instance instanceof ChangeTracker) {
            ((ChangeTracker) instance)._persistence_setPropertyChangeListener(null);
        }
    }

    public interface EntityVisitor {
        boolean visit(Entity<?> entity);
    }

    public static class ContainerResourceHolder extends ResourceHolderSupport {

        protected Map<UnitOfWork, Set<Entity>> unitOfWorkMap = new HashMap<>();

        protected Set<Entity> savedInstances = createEntitySet();

        protected Set<Entity> newDetachedInstances = createEntitySet();

        protected String storeName;

        public ContainerResourceHolder(String storeName) {
            this.storeName = storeName;
        }

        public String getStoreName() {
            return storeName;
        }

        protected void registerInstanceForUnitOfWork(Entity instance, UnitOfWork unitOfWork) {
            if (log.isTraceEnabled())
                log.trace("ContainerResourceHolder.registerInstanceForUnitOfWork: instance = " +
                        instance + ", UnitOfWork = " + unitOfWork);

            if (instance instanceof ManagedEntity) {
                ((ManagedEntity) instance).getEntityEntry().setManaged(true);
            }

            Set<Entity> instances = unitOfWorkMap.get(unitOfWork);
            if (instances == null) {
                instances = createEntitySet();
                unitOfWorkMap.put(unitOfWork, instances);
            }
            instances.add(instance);
        }

        protected void unregisterInstance(Entity instance, UnitOfWork unitOfWork) {
            Set<Entity> instances = unitOfWorkMap.get(unitOfWork);
            if (instances != null) {
                instances.remove(instance);
            }
        }

        protected Collection<Entity> getInstances(UnitOfWork unitOfWork) {
            HashSet<Entity> set = new HashSet<>();
            Set<Entity> entities = unitOfWorkMap.get(unitOfWork);
            if (entities != null)
                set.addAll(entities);
            return set;
        }

        protected Collection<Entity> getAllInstances() {
            Set<Entity> set = createEntitySet();
            for (Set<Entity> instances : unitOfWorkMap.values()) {
                set.addAll(instances);
            }
            return set;
        }

        protected Collection<Entity> getSavedInstances() {
            return savedInstances;
        }

        public Set<Entity> getNewDetachedInstances() {
            return newDetachedInstances;
        }

        @Override
        public String toString() {
            return "ContainerResourceHolder@" + Integer.toHexString(hashCode()) + "{" +
                    "storeName='" + storeName + '\'' +
                    '}';
        }
    }

    protected class ContainerResourceSynchronization
            extends ResourceHolderSynchronization<ContainerResourceHolder, String> implements Ordered {

        protected final ContainerResourceHolder container;

        public ContainerResourceSynchronization(ContainerResourceHolder resourceHolder, String resourceKey) {
            super(resourceHolder, resourceKey);
            this.container = resourceHolder;
        }

        @Override
        protected void cleanupResource(ContainerResourceHolder resourceHolder, String resourceKey, boolean committed) {
            resourceHolder.unitOfWorkMap.clear();
            resourceHolder.savedInstances.clear();
        }

        @Override
        public void beforeCommit(boolean readOnly) {
            if (log.isTraceEnabled())
                log.trace("ContainerResourceSynchronization.beforeCommit: instances=" + container.getAllInstances() + ", readOnly=" + readOnly);

            if (!readOnly) {
                traverseEntities(container, new OnSaveEntityVisitor(container.getStoreName()), false);
                // todo entity log
//                entityLog.flush();
            }

            Collection<Entity> instances = container.getAllInstances();
            Set<String> typeNames = new HashSet<>();
            for (Object instance : instances) {
                if (instance instanceof Entity) {
                    Entity entity = (Entity) instance;

                    if (readOnly) {
                        AttributeChangeListener changeListener =
                                (AttributeChangeListener) ((ChangeTracker) entity)._persistence_getPropertyChangeListener();
                        if (changeListener != null && changeListener.hasChanges())
                            throw new IllegalStateException("Changed instance " + entity + " in read-only transaction");
                    }

                    // if cache is enabled, the entity can have EntityFetchGroup instead of JmixEntityFetchGroup
                    if (instance instanceof FetchGroupTracker) {
                        FetchGroupTracker fetchGroupTracker = (FetchGroupTracker) entity;
                        FetchGroup fetchGroup = fetchGroupTracker._persistence_getFetchGroup();
                        if (fetchGroup != null && !(fetchGroup instanceof JmixEntityFetchGroup))
                            fetchGroupTracker._persistence_setFetchGroup(new JmixEntityFetchGroup(fetchGroup));
                    }

                    if (entity instanceof ManagedEntity) {
                        if (((ManagedEntity) entity).getEntityEntry().isNew()) {
                            typeNames.add(metadata.getClass(entity).getName());
                        }
                        fireBeforeDetachEntityListener((ManagedEntity) entity, container.getStoreName());
                    }
                }
            }

            if (!readOnly) {
                Collection<Entity> allInstances = container.getAllInstances();
                for (BeforeCommitTransactionListener transactionListener : beforeCommitTxListeners) {
                    transactionListener.beforeCommit(container.getStoreName(), allInstances);
                }
                queryCacheManager.invalidate(typeNames, true);
                List<EntityChangedEvent> collectedEvents = entityChangedEventManager.collect(container.getAllInstances());
                detachAll();
                publishEntityChangedEvents(collectedEvents);
            } else {
                detachAll();
            }
        }

        @Override
        public void afterCompletion(int status) {
            try {
                Collection<Entity> instances = container.getAllInstances();
                if (log.isTraceEnabled())
                    log.trace("ContainerResourceSynchronization.afterCompletion: instances = " + instances);
                for (Object instance : instances) {
                    if (instance instanceof ManagedEntity) {
                        if (status == TransactionSynchronization.STATUS_COMMITTED) {
                            if (((ManagedEntity) instance).getEntityEntry().isNew()) {
                                // new instances become not new and detached only if the transaction was committed
                                ((ManagedEntity) instance).getEntityEntry().setNew(false);
                            }
                        } else { // commit failed or the transaction was rolled back
                            makeDetached(instance);
                            for (Entity entity : container.getNewDetachedInstances()) {
                                ((ManagedEntity) entity).getEntityEntry().setNew(true);
                                ((ManagedEntity) entity).getEntityEntry().setDetached(false);
                            }
                        }
                    }
                }
                for (AfterCompleteTransactionListener listener : afterCompleteTxListeners) {
                    listener.afterComplete(status == TransactionSynchronization.STATUS_COMMITTED, instances);
                }
            } finally {
                super.afterCompletion(status);
            }
        }

        private void detachAll() {
            Collection<Entity> instances = container.getAllInstances();
            for (Object instance : instances) {
                if (instance instanceof ManagedEntity &&
                        ((ManagedEntity) instance).getEntityEntry().isNew()) {
                    container.getNewDetachedInstances().add((Entity) instance);
                }
            }

            javax.persistence.EntityManager jmixEm = storeAwareLocator.getEntityManager(container.getStoreName());
            JpaEntityManager jpaEm = jmixEm.unwrap(JpaEntityManager.class);
            jpaEm.flush();
            jpaEm.clear();

            for (Object instance : instances) {
                makeDetached(instance);
            }
        }

        private void publishEntityChangedEvents(List<EntityChangedEvent> collectedEvents) {
            if (collectedEvents.isEmpty())
                return;

            List<TransactionSynchronization> synchronizationsBefore = new ArrayList<>(
                    TransactionSynchronizationManager.getSynchronizations());

            entityChangedEventManager.publish(collectedEvents);

            List<TransactionSynchronization> synchronizations = new ArrayList<>(
                    TransactionSynchronizationManager.getSynchronizations());

            if (synchronizations.size() > synchronizationsBefore.size()) {
                synchronizations.removeAll(synchronizationsBefore);
                for (TransactionSynchronization synchronization : synchronizations) {
                    synchronization.beforeCommit(false);
                }
            }
        }

        @Override
        public int getOrder() {
            return 100;
        }
    }

    protected class OnSaveEntityVisitor implements EntityVisitor {

        private String storeName;

        public OnSaveEntityVisitor(String storeName) {
            this.storeName = storeName;
        }

        @Override
        public boolean visit(Entity entity) {
            if (((ManagedEntity) entity).getEntityEntry().isNew()
                    && !getSavedInstances(storeName).contains(entity)) {
                entityListenerManager.fireListener(entity, EntityListenerType.BEFORE_INSERT, storeName);

                entityChangingEventManager.publishEvent(entity, EntityChangeType.CREATE, null);

                // todo entity log
//                entityLog.registerCreate(entity, true);

                // todo fts
//                enqueueForFts(entity, FtsChangeType.INSERT);

                ormCacheSupport.evictMasterEntity(entity, null);
                return true;
            }

            AttributeChangeListener changeListener =
                    (AttributeChangeListener) ((ChangeTracker) entity)._persistence_getPropertyChangeListener();
            if (changeListener == null)
                return false;

            if (isDeleted((ManagedEntity) entity, changeListener)) {
                entityListenerManager.fireListener(entity, EntityListenerType.BEFORE_DELETE, storeName);

                entityChangingEventManager.publishEvent(entity, EntityChangeType.DELETE, null);

                // todo entity log
//                entityLog.registerDelete(entity, true);

                if ((entity instanceof SoftDelete))
                    processDeletePolicy(entity);

                // todo fts
//                enqueueForFts(entity, FtsChangeType.DELETE);

                ormCacheSupport.evictMasterEntity(entity, null);
                return true;

            } else if (changeListener.hasChanges()) {

                EntityAttributeChanges changes = new EntityAttributeChanges();
                // add changes before listener
                changes.addChanges(entity);

                entityListenerManager.fireListener(entity, EntityListenerType.BEFORE_UPDATE, storeName);

                // add changes after listener
                changes.addChanges(entity);

                if (((ManagedEntity) entity).getEntityEntry().isNew()) {

                    entityChangingEventManager.publishEvent(entity, EntityChangeType.CREATE, null);
                    // todo entity log
//                    // it can happen if flush was performed, so the entity is still New but was saved
//                    entityLog.registerCreate(entity, true);

                    // todo fts
//                    enqueueForFts(entity, FtsChangeType.INSERT);
                } else {

                    entityChangingEventManager.publishEvent(entity, EntityChangeType.UPDATE, changes);
                    // todo entity log
//                    entityLog.registerModify(entity, true, changes);

                    // todo fts
//                    enqueueForFts(entity, FtsChangeType.UPDATE);
                }

                ormCacheSupport.evictMasterEntity(entity, changes);
                return true;
            }

            return false;
        }

        // todo fts
//        protected void enqueueForFts(Entity entity, FtsChangeType changeType) {
//            if (!FtsConfigHelper.getEnabled())
//                return;
//            try {
//                if (ftsSender == null) {
//                    if (AppBeans.containsBean(FtsSender.NAME)) {
//                        ftsSender = AppBeans.get(FtsSender.NAME);
//                    } else {
//                        log.error("Error enqueueing changes for FTS: " + FtsSender.NAME + " bean not found");
//                    }
//                }
//                if (ftsSender != null)
//                    ftsSender.enqueue(entity, changeType);
//            } catch (Exception e) {
//                log.error("Error enqueueing changes for FTS", e);
//            }
//        }

        protected void processDeletePolicy(Entity entity) {
            DeletePolicyProcessor processor = AppBeans.get(DeletePolicyProcessor.NAME); // prototype
            processor.setEntity(entity);
            processor.process();
        }
    }

    private static class RunnerResourceHolder extends ResourceHolderSupport {

        private List<Runnable> list = new ArrayList<>();
        private String storeName;

        public RunnerResourceHolder(String storeName) {
            this.storeName = storeName;
        }

        public String getStoreName() {
            return storeName;
        }

        private void add(Runnable action) {
            list.add(action);
        }

        private void run() {
            for (Runnable runnable : list) {
                runnable.run();
            }
        }
    }

    private static class RunnerSynchronization extends ResourceHolderSynchronization<RunnerResourceHolder, String> {

        private RunnerResourceHolder runner;

        public RunnerSynchronization(RunnerResourceHolder runner) {
            super(runner, RUNNER_RESOURCE_HOLDER);
            this.runner = runner;
        }

        @Override
        public void beforeCommit(boolean readOnly) {
            runner.run();
        }
    }
}
