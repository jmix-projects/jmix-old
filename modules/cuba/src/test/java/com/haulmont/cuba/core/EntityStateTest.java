/*
 * Copyright (c) 2008-2016 Haulmont.
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
 *
 */

package com.haulmont.cuba.core;

import com.haulmont.cuba.core.model.common.Group;
import com.haulmont.cuba.core.model.common.User;
import com.haulmont.cuba.core.testsupport.CoreTest;
import com.haulmont.cuba.core.testsupport.TestSupport;
import io.jmix.core.entity.ManagedEntityEntry;
import io.jmix.data.EntityManager;
import io.jmix.data.Persistence;
import io.jmix.data.Query;
import io.jmix.data.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

import static com.haulmont.cuba.core.testsupport.TestSupport.reserialize;
import static org.junit.jupiter.api.Assertions.*;

@CoreTest
public class EntityStateTest {
    @Inject
    private Persistence persistence;

    private UUID userId;
    private Group group;

    @AfterEach
    public void tearDown() throws Exception {
        if (userId != null)
            TestSupport.deleteRecord("TEST_USER", userId);
        if (group != null) {
            TestSupport.deleteRecord(group);
        }
    }

    @Test
    public void testTransactions() throws Exception {
        User user;
        Group localGroup;

        // create and persist

        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();

            user = new User();
            assertTrue(user.getEntityEntry().isNew());

            assertFalse(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());

            group = new Group();
            group.setName("group");
            em.persist(group);

            userId = user.getId();
            user.setName("testUser");
            user.setLogin("testLogin");
            user.setGroup(group);
            em.persist(user);

            assertTrue(user.getEntityEntry().isNew());

            assertTrue(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());

            tx.commit();
        } finally {
            tx.end();
        }

        assertFalse(user.getEntityEntry().isNew());

        assertFalse(user.getEntityEntry().isManaged());
        assertTrue(user.getEntityEntry().isDetached());

        // load from DB

        tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            // find
            user = em.find(User.class, userId);
            assertNotNull(user);

            assertFalse(user.getEntityEntry().isNew());

            assertTrue(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());

            localGroup = user.getGroup();
            assertNotNull(localGroup);

            assertFalse(user.getEntityEntry().isNew());

            assertTrue(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());

            tx.commit();
        } finally {
            tx.end();
        }

        tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            // query
            Query query = em.createQuery("select u from test$User u where u.id = ?1").setParameter(1, userId);
            user = (User) query.getFirstResult();
            assertNotNull(user);

            assertFalse(user.getEntityEntry().isNew());

            assertTrue(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());

            localGroup = user.getGroup();
            assertNotNull(localGroup);

            assertFalse(localGroup.getEntityEntry().isNew());

            assertTrue(localGroup.getEntityEntry().isManaged());
            assertFalse(localGroup.getEntityEntry().isDetached());

            tx.commit();
        } finally {
            tx.end();
        }

        assertFalse(user.getEntityEntry().isNew());

        assertFalse(user.getEntityEntry().isManaged());
        assertTrue(user.getEntityEntry().isDetached());

        assertFalse(localGroup.getEntityEntry().isNew());

        assertFalse(user.getEntityEntry().isManaged());
        assertTrue(user.getEntityEntry().isDetached());

        user.setName("changed name");

        // merge changed

        tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            user = em.merge(user);

            assertFalse(user.getEntityEntry().isNew());

            assertTrue(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());

            tx.commit();
        } finally {
            tx.end();
        }

        assertFalse(user.getEntityEntry().isNew());

        assertFalse(user.getEntityEntry().isManaged());
        assertTrue(user.getEntityEntry().isDetached());
    }

    @Test
    public void testSerialization() throws Exception {
        User user;
        Group localGroup;

        // serialize new
        user = new User();
        assertTrue(user.getEntityEntry().isNew());

        assertFalse(user.getEntityEntry().isManaged());
        assertFalse(user.getEntityEntry().isDetached());

        user = reserialize(user);

        assertTrue(user.getEntityEntry().isNew());

        assertFalse(user.getEntityEntry().isManaged());
        assertFalse(user.getEntityEntry().isDetached());

        // serialize managed

        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();

            group = new Group();
            group.setName("group");
            em.persist(group);

            user = new User();
            userId = user.getId();
            user.setName("testUser");
            user.setLogin("testLogin");

            user.setGroup(group);
            em.persist(user);

            tx.commit();
        } finally {
            tx.end();
        }

        tx = persistence.createTransaction();

        try {
            EntityManager em = persistence.getEntityManager();
            user = em.find(User.class, userId);
            assertNotNull(user);

            assertFalse(user.getEntityEntry().isNew());

            assertTrue(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());

            localGroup = user.getGroup();
            assertNotNull(localGroup);

            assertFalse(localGroup.getEntityEntry().isNew());

            assertTrue(localGroup.getEntityEntry().isManaged());
            assertFalse(localGroup.getEntityEntry().isDetached());

            user = reserialize(user);

            assertFalse(user.getEntityEntry().isNew());

            assertFalse(user.getEntityEntry().isManaged());
            assertTrue(user.getEntityEntry().isDetached());

            tx.commit();
        } finally {
            tx.end();
        }

        user.setName("changed name");

        // merge changed and serialize

        tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            user = em.merge(user);

            assertFalse(user.getEntityEntry().isNew());

            assertTrue(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());

            tx.commit();
        } finally {
            tx.end();
        }

        assertFalse(user.getEntityEntry().isNew());

        assertFalse(user.getEntityEntry().isManaged());
        assertTrue(user.getEntityEntry().isDetached());

        user = reserialize(user);

        assertFalse(user.getEntityEntry().isNew());

        assertFalse(user.getEntityEntry().isManaged());
        assertTrue(user.getEntityEntry().isDetached());
    }

    @Test
    public void testTransactionRollback_new() throws Exception {
        User user = null;

        // create and persist

        Transaction tx = persistence.createTransaction();
        try {
            user = new User();
            assertTrue(user.getEntityEntry().isNew());

            assertFalse(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());

            userId = user.getId();

            persistence.getEntityManager().persist(user);

            assertTrue(user.getEntityEntry().isNew());

            assertTrue(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());

            tx.commit();

            fail(); // due to absence login
        } catch (Exception e) {
            // ok
        } finally {
            tx.end();
        }

        assertTrue(user.getEntityEntry().isNew());

        assertFalse(user.getEntityEntry().isManaged());
        assertFalse(user.getEntityEntry().isDetached());
    }

    @Test
    public void testTransactionRollback_loaded() {
        User user;

        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();

            user = new User();
            assertTrue(user.getEntityEntry().isNew());

            assertFalse(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());

            group = new Group();
            group.setName("group");
            em.persist(group);

            userId = user.getId();
            user.setName("testUser");
            user.setLogin("testLogin");
            user.setGroup(group);
            em.persist(user);

            tx.commit();
        } finally {
            tx.end();
        }


        tx = persistence.createTransaction();
        try {
            user = persistence.getEntityManager().find(User.class, userId);

            assertFalse(user.getEntityEntry().isNew());

            assertTrue(user.getEntityEntry().isManaged());
            assertFalse(user.getEntityEntry().isDetached());
        } finally {
            tx.end();
        }

        assertFalse(user.getEntityEntry().isNew());

        assertFalse(user.getEntityEntry().isManaged());
        assertTrue(user.getEntityEntry().isDetached());
    }
}
