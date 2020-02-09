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

import io.jmix.core.JmixCoreConfiguration;
import io.jmix.data.JmixDataConfiguration;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import test_support.JmixDataTestConfiguration;
import test_support.entity.sales.Customer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {JmixCoreConfiguration.class, JmixDataConfiguration.class, JmixDataTestConfiguration.class})
public class EntityManagerTest {

    @PersistenceContext
    EntityManager entityManager;

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @After
    public void tearDown() throws Exception {
        jdbcTemplate.update("delete from SALES_CUSTOMER");
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testContainerEm() {
        Customer customer = new Customer();
        customer.setName("c1");

        entityManager.persist(customer);
        entityManager.flush();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from SALES_CUSTOMER");
        assertEquals(1, rows.size());
    }

    @Ignore
    @Test
    public void testApplicationEm() {
        Customer customer = new Customer();
        customer.setName("c1");

        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(customer);
        em.getTransaction().commit();
        em.close();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from SALES_CUSTOMER");
        assertEquals(1, rows.size());
    }
}
