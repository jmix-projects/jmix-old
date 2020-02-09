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

package entity_manager;

import io.jmix.core.DataManager;
import io.jmix.core.JmixCoreConfiguration;
import io.jmix.data.JmixDataConfiguration;
import io.jmix.data.impl.JmixQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import test_support.JmixDataTestConfiguration;
import test_support.entity.sales.Customer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {JmixCoreConfiguration.class, JmixDataConfiguration.class, JmixDataTestConfiguration.class})
public class QueryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    DataManager dataManager;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Test
    @Transactional
    public void testUnwrap() {
        // given:
        TypedQuery<Customer> query = entityManager.createQuery("select e from sales$Customer e where e.name = ?1", Customer.class);

        // when:
        JmixQuery jmixQuery = query.unwrap(JmixQuery.class);

        // then:
        assertNotNull(jmixQuery);
    }

    @Test
    public void testResultList() {
        // given:
        Customer customer = dataManager.create(Customer.class);
        customer.setName("c1");
        dataManager.commit(customer);

        // when:
        List<Customer> customerList = transactionTemplate.execute(status -> {
            TypedQuery<Customer> query = entityManager.createQuery("select e from sales$Customer e where e.name = ?1", Customer.class);
            return query.setParameter(1, "c1").getResultList();
        });

        // then:
        assertEquals(1, customerList.size());
    }
}
