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

package lazy_loading

import io.jmix.core.DataManager
import io.jmix.core.FetchPlanRepository
import io.jmix.core.LoadContext
import org.eclipse.persistence.config.QueryHints
import org.eclipse.persistence.queries.FetchGroup
import org.eclipse.persistence.queries.LoadGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import test_support.DataSpec
import test_support.entity.lazyloading.FPMainEntity
import test_support.entity.lazyloading.FPManyEntity

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

class LazyLoadingTest extends DataSpec {

    @Autowired
    DataManager dataManager
    @Autowired
    FetchPlanRepository fetchPlanRepository

    TransactionTemplate transaction
    @PersistenceContext
    EntityManager entityManager

    @Autowired
    def setTransactionManager(PlatformTransactionManager transactionManager) {
        transaction = new TransactionTemplate(transactionManager)
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
    }

    def "DataManager and FetchPlan"() {
        setup:

        FPMainEntity fpMainEntity = new FPMainEntity()
        fpMainEntity.setName("Name")
        dataManager.save(fpMainEntity)

        FPManyEntity fpManyEntity = new FPManyEntity()
        fpManyEntity.setName("Name many")
        fpManyEntity.setSecondName("Second name many")
        fpManyEntity.setMainEntity(fpMainEntity)
        dataManager.save(fpManyEntity)
        UUID fpId = fpManyEntity.getId()

        when:

        LoadContext<FPManyEntity> loadContext = new LoadContext<>(FPManyEntity.class)
        loadContext.setId(fpId)

        loadContext.setFetchPlan(fetchPlanRepository.getFetchPlan(FPManyEntity.class, "FPManyEntity"))
        fpManyEntity = dataManager.load(loadContext)

        then:

        fpManyEntity.getName() == "Name many"
        fpManyEntity.getSecondName() == "Second name many"
        fpManyEntity.getMainEntity() == fpMainEntity
    }

    def "Raw EntityManager and LoadGroup"() {
        setup:

        FPMainEntity fpMainEntity = new FPMainEntity()
        fpMainEntity.setName("Name")
        dataManager.save(fpMainEntity)

        FPManyEntity fpManyEntity = new FPManyEntity()
        fpManyEntity.setName("Name many")
        fpManyEntity.setSecondName("Second name many")
        fpManyEntity.setMainEntity(fpMainEntity)
        dataManager.save(fpManyEntity)
        UUID fpId = fpManyEntity.getId()

        when:

        fpManyEntity = transaction.execute({ status ->
            EntityManager emDelegate = entityManager.getDelegate()
            def query = emDelegate.createQuery("select e from fp_ManyEntity e where e.id = :id")
            query.setParameter("id", fpId)
            LoadGroup loadGroup = new LoadGroup()
            loadGroup.addAttribute("name")
            loadGroup.addAttribute("mainEntity")
            query.setHint(QueryHints.LOAD_GROUP, loadGroup)
            return query.getSingleResult()
        })

        then:

        fpManyEntity.getName() == "Name many"
        fpManyEntity.getSecondName() == "Second name many"
        fpManyEntity.getMainEntity() == fpMainEntity
    }

    def "Raw EntityManager and FetchGroup"() {
        setup:

        FPMainEntity fpMainEntity = new FPMainEntity()
        fpMainEntity.setName("Name")
        dataManager.save(fpMainEntity)

        FPManyEntity fpManyEntity = new FPManyEntity()
        fpManyEntity.setName("Name many")
        fpManyEntity.setSecondName("Second name many")
        fpManyEntity.setMainEntity(fpMainEntity)
        dataManager.save(fpManyEntity)
        UUID fpId = fpManyEntity.getId()

        when:

        fpManyEntity = transaction.execute({ status ->
            EntityManager emDelegate = entityManager.getDelegate()
            def query = emDelegate.createQuery("select e from fp_ManyEntity e where e.id = :id")
            query.setParameter("id", fpId)
            FetchGroup fetchGroup = new FetchGroup()
            fetchGroup.addAttribute("name")
            fetchGroup.addAttribute("mainEntity")
            query.setHint(QueryHints.FETCH_GROUP, fetchGroup)
            return query.getSingleResult()
        })

        then:

        fpManyEntity.getName() == "Name many"
        fpManyEntity.getSecondName() == null
        fpManyEntity.getMainEntity() == fpMainEntity
    }
}
