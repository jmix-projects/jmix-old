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

package io.jmix.ui.persistence.impl;

import io.jmix.core.ClientType;
import io.jmix.core.Metadata;
import io.jmix.core.commons.xmlparsing.Dom4jTools;
import io.jmix.core.entity.User;
import io.jmix.core.security.Security;
import io.jmix.core.security.UserSessionSource;
import io.jmix.ui.persistence.UserSettingsManager;
import io.jmix.ui.persistence.entity.UserSetting;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

@Component(UserSettingsManager.NAME)
public class UserSettingsManagerImpl implements UserSettingsManager {

    // TODO get User
    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Security security;

    @Inject
    protected Dom4jTools dom4JTools;

    @PersistenceContext
    protected EntityManager entityManager;

    protected TransactionTemplate transaction;

    @Inject
    protected void setTransactionManager(PlatformTransactionManager transactionManager) {
        transaction = new TransactionTemplate(transactionManager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
    public String loadSetting(String name) {
        return loadSetting(null, name);
    }

    @Override
    public String loadSetting(ClientType clientType, String name) {
        String value = transaction.execute(status -> {
            UserSetting us = findUserSettings(clientType, name);
            return us == null ? null : us.getValue();
        });

        return value;
    }

    @Override
    public void saveSetting(String name, String value) {
        saveSetting(null, name, value);
    }

    @Override
    public void saveSetting(ClientType clientType, String name, String value) {
        transaction.executeWithoutResult(status -> {
            UserSetting us = findUserSettings(clientType, name);
            if (us == null) {
                us = metadata.create(UserSetting.class);
                us.setUserLogin(userSessionSource.getUserSession().getUser().getLogin());
                us.setName(name);
                us.setClientType(clientType.getName());
                us.setValue(value);

                entityManager.persist(us);
            } else {
                us.setValue(value);
            }
        });
    }

    @Override
    public void deleteSettings(ClientType clientType, String name) {
        transaction.executeWithoutResult(status -> {
            UserSetting us = findUserSettings(clientType, name);
            if (us != null) {
                entityManager.remove(us);
            }
        });
    }


    @Override
    public void copySettings(User fromUser, User toUser) {
            /*MetaClass metaClass = metadata.getClassNN(UserSetting.class);

            if (!security.isEntityOpPermitted(metaClass, EntityOp.CREATE)) {
                throw new AccessDeniedException(PermissionType.ENTITY_OP, metaClass.getName());
            }

            Map<UUID, Presentation> presentationsMap = copyPresentations(fromUser, toUser);
            copyUserFolders(fromUser, toUser, presentationsMap);
            Map<UUID, FilterEntity> filtersMap = copyFilters(fromUser, toUser);

            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();

                Query deleteSettingsQuery = em.createQuery("delete from sec$UserSetting s where s.user.id = ?1");
                deleteSettingsQuery.setParameter(1, toUser.getId());
                deleteSettingsQuery.executeUpdate();
                tx.commitRetaining();
                em = persistence.getEntityManager();

                TypedQuery<UserSetting> q = em.createQuery("select s from sec$UserSetting s where s.user.id = ?1", UserSetting.class);
                q.setParameter(1, fromUser.getId());
                List<UserSetting> fromUserSettings = q.getResultList();

                for (UserSetting currSetting : fromUserSettings) {
                    UserSetting newSetting = metadata.create(UserSetting.class);
                    newSetting.setUser(toUser);
                    newSetting.setClientType(currSetting.getClientType());
                    newSetting.setName(currSetting.getName());

                    try {
                        Document doc = dom4JTools.readDocument(currSetting.getValue());

                        List<Element> components = doc.getRootElement().element("components").elements("component");
                        for (Element component : components) {
                            Attribute presentationAttr = component.attribute("presentation");
                            if (presentationAttr != null) {
                                UUID presentationId = UuidProvider.fromString(presentationAttr.getValue());
                                Presentation newPresentation = presentationsMap.get(presentationId);
                                if (newPresentation != null) {
                                    presentationAttr.setValue(newPresentation.getId().toString());
                                }
                            }
                            Element defaultFilterEl = component.element("defaultFilter");
                            if (defaultFilterEl != null) {
                                Attribute idAttr = defaultFilterEl.attribute("id");
                                if (idAttr != null) {
                                    UUID filterId = UuidProvider.fromString(idAttr.getValue());
                                    FilterEntity newFilter = filtersMap.get(filterId);
                                    if (newFilter != null) {
                                        idAttr.setValue(newFilter.getId().toString());
                                    }
                                }
                            }
                        }

                        newSetting.setValue(dom4JTools.writeDocument(doc, true));
                    } catch (Exception e) {
                        newSetting.setValue(currSetting.getValue());
                    }
                    em.persist(newSetting);
                }

                tx.commit();
            }*/
    }

    @Override
    public void deleteScreenSettings(ClientType clientType, Set<String> screens) {
        transaction.executeWithoutResult(status -> {
            TypedQuery<UserSetting> selectQuery = entityManager.createQuery(
                    "select e from sec$UserSetting e where e.user.id = ?1 and e.clientType=?2",
                    UserSetting.class);
            selectQuery.setParameter(1, userSessionSource.getUserSession().getUser().getId());
            selectQuery.setParameter(2, clientType.getName());
            List<UserSetting> userSettings = selectQuery.getResultList();
            for (UserSetting userSetting : userSettings) {
                if (screens.contains(userSetting.getName())) {
                    entityManager.remove(userSetting);
                }
            }
        });
    }

    @Nullable
    protected UserSetting findUserSettings(ClientType clientType, String name) {
        TypedQuery<UserSetting> q = entityManager.createQuery(
                "select s from ui_UserSetting s where s.userLogin = ?1 and s.name =?2 and s.clientType = ?3",
                UserSetting.class);
        q.setParameter(1, userSessionSource.getUserSession().getUser().getLogin());
        q.setParameter(2, name);
        q.setParameter(3, clientType == null ? null : clientType.getName());

        List result = q.getResultList();
        if (result.isEmpty()) {
            return null;
        }

        return (UserSetting) result.get(0);
    }

    /*protected Map<UUID, Presentation> copyPresentations(User fromUser, User toUser) {
        Map<UUID, Presentation> presentationMap = new HashMap<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            // delete existing
            Query delete = em.createQuery("delete from sec$Presentation p where p.user.id = ?1");
            delete.setParameter(1, toUser.getId());
            delete.executeUpdate();

            // copy settings
            TypedQuery<Presentation> selectQuery = em.createQuery(
                    "select p from sec$Presentation p where p.user.id = ?1", Presentation.class);
            selectQuery.setParameter(1, fromUser.getId());
            List<Presentation> presentations = selectQuery.getResultList();

            for (Presentation presentation : presentations) {
                Presentation newPresentation = metadata.create(Presentation.class);
                newPresentation.setUser(toUser);
                newPresentation.setComponentId(presentation.getComponentId());
                newPresentation.setAutoSave(presentation.getAutoSave());
                newPresentation.setName(presentation.getName());
                newPresentation.setXml(presentation.getXml());
                presentationMap.put(presentation.getId(), newPresentation);
                em.persist(newPresentation);
            }
            tx.commit();
            return presentationMap;
        }
    }

    protected void copyUserFolders(User fromUser, User toUser, Map<UUID, Presentation> presentationsMap) {
        try (Transaction tx = persistence.createTransaction()) {
            MetaClass effectiveMetaClass = metadata.getExtendedEntities().getEffectiveMetaClass(SearchFolder.class);
            EntityManager em = persistence.getEntityManager();
            try {
                em.setSoftDeletion(false);
                Query deleteSettingsQuery = em.createQuery(
                        String.format("delete from %s s where s.user.id = ?1", effectiveMetaClass.getName())
                );

                deleteSettingsQuery.setParameter(1, toUser.getId());
                deleteSettingsQuery.executeUpdate();
            } finally {
                em.setSoftDeletion(true);
            }
            TypedQuery<SearchFolder> q = em.createQuery(
                    String.format("select s from %s s where s.user.id = ?1", effectiveMetaClass.getName()),
                    SearchFolder.class);
            q.setParameter(1, fromUser.getId());

            List<SearchFolder> fromUserFolders = q.getResultList();
            Map<SearchFolder, SearchFolder> copiedFolders = new HashMap<>();
            for (SearchFolder searchFolder : fromUserFolders) {
                copyFolder(searchFolder, toUser, copiedFolders, presentationsMap);
            }
            tx.commit();
        }
    }*/

  /*  protected SearchFolder copyFolder(SearchFolder searchFolder,
                                      User toUser,
                                      Map<SearchFolder, SearchFolder> copiedFolders,
                                      Map<UUID, Presentation> presentationsMap) {
        SearchFolder newFolder;
        if (searchFolder.getUser() == null)
            return searchFolder;
        newFolder = copiedFolders.get(searchFolder);
        if (newFolder != null)
            return null;
        newFolder = metadata.create(SearchFolder.class);
        newFolder.setUser(toUser);
        newFolder.setApplyDefault(searchFolder.getApplyDefault());
        newFolder.setFilterComponentId(searchFolder.getFilterComponentId());
        newFolder.setFilterXml(searchFolder.getFilterXml());
        newFolder.setItemStyle(searchFolder.getItemStyle());
        newFolder.setName(searchFolder.getName());
        newFolder.setTabName(searchFolder.getTabName());
        newFolder.setSortOrder(searchFolder.getSortOrder());
        newFolder.setIsSet(searchFolder.getIsSet());
        newFolder.setEntityType(searchFolder.getEntityType());
        SearchFolder copiedFolder = copiedFolders.get(searchFolder.getParent());
        if (searchFolder.getParent() != null) {
            if (copiedFolder != null) {
                newFolder.setParent(copiedFolder);
            } else {
                SearchFolder newParent = getParent((SearchFolder) searchFolder.getParent(), toUser, copiedFolders, presentationsMap);
                newFolder.setParent(newParent);
            }
        }
        if (searchFolder.getPresentation() != null) {
            if (searchFolder.getPresentation().getUser() == null) {
                newFolder.setPresentation(searchFolder.getPresentation());
            } else {
                Presentation newPresentation = presentationsMap.get(searchFolder.getPresentation().getId());
                newFolder.setPresentation(newPresentation);
            }
        }
        copiedFolders.put(searchFolder, newFolder);
        EntityManager em = persistence.getEntityManager();
        em.persist(newFolder);
        return newFolder;
    }

    protected SearchFolder getParent(SearchFolder parentFolder, User toUser, Map<SearchFolder, SearchFolder> copiedFolders, Map<UUID, Presentation> presentationMap) {
        if (parentFolder == null) {
            return null;
        }
        if (parentFolder.getUser() == null) {
            return parentFolder;
        }
        return copyFolder(parentFolder, toUser, copiedFolders, presentationMap);
    }

    protected Map<UUID, FilterEntity> copyFilters(User fromUser, User toUser) {
        Map<UUID, FilterEntity> filtersMap = new HashMap<>();

        try (Transaction tx = persistence.createTransaction()) {
            MetaClass effectiveMetaClass = metadata.getExtendedEntities().getEffectiveMetaClass(FilterEntity.class);

            EntityManager em = persistence.getEntityManager();
            try {
                em.setSoftDeletion(false);
                Query deleteFiltersQuery = em.createQuery(
                        String.format("delete from %s f where f.user.id = ?1", effectiveMetaClass.getName())
                );
                deleteFiltersQuery.setParameter(1, toUser.getId());
                deleteFiltersQuery.executeUpdate();
            } finally {
                em.setSoftDeletion(true);
            }

            TypedQuery<FilterEntity> q = em.createQuery(
                    String.format("select f from %s f where f.user.id = ?1", effectiveMetaClass.getName()),
                    FilterEntity.class);
            q.setParameter(1, fromUser.getId());
            List<FilterEntity> fromUserFilters = q.getResultList();

            for (FilterEntity filter : fromUserFilters) {
                FilterEntity newFilter = metadata.create(FilterEntity.class);
                newFilter.setUser(toUser);
                newFilter.setCode(filter.getCode());
                newFilter.setName(filter.getName());
                newFilter.setComponentId(filter.getComponentId());
                newFilter.setXml(filter.getXml());
                filtersMap.put(filter.getId(), newFilter);
                em.persist(newFilter);
            }

            tx.commit();
            return filtersMap;
        }
    }*/
}
