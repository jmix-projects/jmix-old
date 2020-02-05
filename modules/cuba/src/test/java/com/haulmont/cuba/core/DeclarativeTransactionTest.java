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

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.model.common.Group;
import com.haulmont.cuba.core.model.common.User;
import com.haulmont.cuba.core.testsupport.CoreTest;
import com.haulmont.cuba.core.testsupport.TestSupport;
import com.haulmont.cuba.core.testsupport.TestingTransactionsOnMethodService;
import com.haulmont.cuba.core.testsupport.TestingTransactionsOnTypeService;
import io.jmix.data.EntityManager;
import io.jmix.data.Persistence;
import io.jmix.data.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNull;

@CoreTest
public class DeclarativeTransactionTest {
    @Inject
    protected TestingTransactionsOnMethodService testingTransactionsOnMethodService;
    @Inject
    protected TestingTransactionsOnTypeService testingTransactionsOnTypeService;
    @Inject
    protected Persistence persistence;
    @Inject
    protected Metadata metadata;

    protected User user;
    protected Group group;

    @BeforeEach
    public void setUp() {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            group = metadata.create(Group.class);
            group.setName("Group");
            em.persist(group);

            user = metadata.create(User.class);
            user.setName("testUser");
            user.setLogin("admin");
            user.setPassword("000");
            user.setGroup(group);
            em.persist(user);

            tx.commit();
        }
    }

    @AfterEach
    public void cleanup() {
        TestSupport.deleteRecord(user);
        TestSupport.deleteRecord(group);
    }

    @Test
    public void test_Method_TxAnnotation() {
        testingTransactionsOnMethodService.declarativeTransaction(user.getId());

        assertNull(persistence.getEntityManagerContext().getAttribute("test"));
    }

    @Test
    public void test_Class_TxAnnotation() {
        testingTransactionsOnTypeService.declarativeTransaction_withoutMethodTxAnnotation(user.getId());

        assertNull(persistence.getEntityManagerContext().getAttribute("test1"));
    }

    @Test
    public void test_MethodAndClass_TxAnnotation() {
        testingTransactionsOnTypeService.declarativeTransaction_withMethodTxAnnotation(user.getId());

        assertNull(persistence.getEntityManagerContext().getAttribute("test2"));
    }
}
