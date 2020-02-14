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
 */

package com.haulmont.cuba.core.listener;

import com.haulmont.cuba.core.model.common.User;
import io.jmix.core.AppBeans;
import io.jmix.core.FetchPlan;
import io.jmix.data.EntityManager;
import io.jmix.data.Persistence;
import io.jmix.data.Transaction;
import io.jmix.data.TypedQuery;
import io.jmix.data.listener.AfterUpdateEntityListener;
import io.jmix.data.listener.BeforeUpdateEntityListener;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


public class TestCascadingEntityListener implements
        BeforeUpdateEntityListener<User>,
        AfterUpdateEntityListener<User> {

    public static final List<String> events = new ArrayList<>();

    public static boolean withView;
    public static boolean withNewTx;

    @Override
    public void onBeforeUpdate(User entity, EntityManager entityManager) {
        events.add("onBeforeUpdate: " + entity.getId());

        Transaction.Runnable code = (em) -> {
            TypedQuery<User> query = entityManager.createQuery("select u from test$User u where u.login = 'admin'", User.class);
            if (withView) {
                query.setViewName(FetchPlan.MINIMAL);
            }
            User admin = query.getSingleResult();
            System.out.println(admin.getLogin());
        };

        if (withNewTx) {
            AppBeans.get(Persistence.class).runInTransaction(code);
        } else {
            code.run(entityManager);
        }
    }

    @Override
    public void onAfterUpdate(User entity, Connection connection) {
        events.add("onAfterUpdate: " + entity.getId());
    }
}
