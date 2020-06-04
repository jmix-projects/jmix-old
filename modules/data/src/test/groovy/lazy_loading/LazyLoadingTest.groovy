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
import org.eclipse.persistence.config.CascadePolicy
import org.eclipse.persistence.config.HintValues
import org.eclipse.persistence.config.QueryHints
import org.eclipse.persistence.queries.FetchGroup
import org.eclipse.persistence.queries.LoadGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import test_support.DataSpec
import test_support.entity.lazyloading.ELTMainEntity
import test_support.entity.lazyloading.FPMainEntity
import test_support.entity.lazyloading.FPManyEntity
import test_support.entity.lazyloading.ManyToManyEntity
import test_support.entity.lazyloading.ManyToManyTwoEntity

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
        ELTMainEntity eltMainEntity = new ELTMainEntity()
        eltMainEntity.setName("Name")
        dataManager.save(eltMainEntity)

        FPManyEntity fpManyEntity = new FPManyEntity()
        fpManyEntity.setName("Name many")
        fpManyEntity.setSecondName("Second name many")
        fpManyEntity.setMainEntity(fpMainEntity)
        fpManyEntity.setEltMainEntity(eltMainEntity)
        dataManager.save(fpManyEntity)
        UUID fpId = fpManyEntity.getId()

        when:

        LoadContext<FPManyEntity> loadContext = new LoadContext<>(FPManyEntity.class)
        loadContext.setId(fpId)

        loadContext.setFetchPlan(fetchPlanRepository.getFetchPlan(FPManyEntity.class, "FPManyEntity"))
        fpManyEntity = dataManager.loadList(loadContext).iterator().next()
        System.out.println(fpManyEntity.getMainEntity().getRefField())

        then:

        fpManyEntity.getName() == "Name many"
        fpManyEntity.getMainEntity() == fpMainEntity
        fpManyEntity.getEltMainEntity() == eltMainEntity
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
            query.setHint(QueryHints.REFRESH, HintValues.TRUE);
            query.setHint(QueryHints.REFRESH_CASCADE, CascadePolicy.CascadeByMapping);
            query.setHint(QueryHints.FETCH_GROUP, fetchGroup)
            return query.getSingleResult()
        })

        then:

        fpManyEntity.getName() == "Name many"
        fpManyEntity.getMainEntity() == fpMainEntity
    }


    def "Raw EntityManager and FetchGroup OneToMany"() {
        setup:

        FPMainEntity fpMainEntity = new FPMainEntity()
        fpMainEntity.setName("Name")
        dataManager.save(fpMainEntity)

        ELTMainEntity eltMainEntity = new ELTMainEntity()
        eltMainEntity.setName("Name")
        dataManager.save(eltMainEntity)
        UUID fpId = eltMainEntity.getId()


        FPManyEntity fpManyEntity = new FPManyEntity()
        fpManyEntity.setName("Name many")
        fpManyEntity.setSecondName("Second name many")
        fpManyEntity.setMainEntity(fpMainEntity)
        fpManyEntity.setEltMainEntity(eltMainEntity)
        dataManager.save(fpManyEntity)

        fpManyEntity = new FPManyEntity()
        fpManyEntity.setName("Name 2 many")
        fpManyEntity.setSecondName("Second 2 name many")
        fpManyEntity.setMainEntity(fpMainEntity)
        fpManyEntity.setEltMainEntity(eltMainEntity)
        dataManager.save(fpManyEntity)

        when:

        eltMainEntity = transaction.execute({ status ->
            EntityManager emDelegate = entityManager.getDelegate()
            def query = emDelegate.createQuery("select e from elt_MainEntity e where e.id = :id")
            query.setParameter("id", fpId)
            FetchGroup fetchGroup = new FetchGroup()
            fetchGroup.addAttribute("name")
            query.setHint(QueryHints.FETCH_GROUP, fetchGroup)
            return query.getSingleResult()
        })

        then:

        eltMainEntity.getName() == "Name"
    }

    def "Raw EntityManager and LoadGroup OneToMany"() {
        setup:

        FPMainEntity fpMainEntity = new FPMainEntity()
        fpMainEntity.setName("Name")
        dataManager.save(fpMainEntity)

        ELTMainEntity eltMainEntity = new ELTMainEntity()
        eltMainEntity.setName("Name")
        dataManager.save(eltMainEntity)
        UUID fpId = eltMainEntity.getId()


        FPManyEntity fpManyEntity = new FPManyEntity()
        fpManyEntity.setName("Name many")
        fpManyEntity.setSecondName("Second name many")
        fpManyEntity.setMainEntity(fpMainEntity)
        fpManyEntity.setEltMainEntity(eltMainEntity)
        dataManager.save(fpManyEntity)

        fpManyEntity = new FPManyEntity()
        fpManyEntity.setName("Name 2 many")
        fpManyEntity.setSecondName("Second 2 name many")
        fpManyEntity.setMainEntity(fpMainEntity)
        fpManyEntity.setEltMainEntity(eltMainEntity)
        dataManager.save(fpManyEntity)

        when:

        eltMainEntity = transaction.execute({ status ->
            EntityManager emDelegate = entityManager.getDelegate()
            def query = emDelegate.createQuery("select e from elt_MainEntity e where e.id = :id")
            query.setParameter("id", fpId)
            LoadGroup loadGroup = new LoadGroup()
            loadGroup.addAttribute("name")
            query.setHint(QueryHints.LOAD_GROUP, loadGroup)
            return query.getSingleResult()
        })
        eltMainEntity.getFpManyEntities()

        then:

        eltMainEntity.getName() == "Name"
        eltMainEntity.getRefField() != null
    }

    def "ManyToMany"() {
        setup:

        UUID twoId = prepareManyToMany()

        LoadContext<ManyToManyTwoEntity> loadContext = new LoadContext<>(ManyToManyTwoEntity.class)
        loadContext.setId(twoId)

        loadContext.setFetchPlan(fetchPlanRepository.getFetchPlan(ManyToManyTwoEntity.class, "ManyToManyTwoEntity"))

        when:

        ManyToManyTwoEntity result = transaction.execute({ status ->
            EntityManager emDelegate = entityManager.getDelegate()
            def query = emDelegate.createQuery("select e from test_ManyToManyTwoEntity e where e.id = :id")
            query.setParameter("id", twoId)
            return query.getSingleResult()
        })

        then:

        result.getManyToManyEntities() != null
    }

    UUID prepareManyToMany() {
        ManyToManyEntity manyToManyEntity1 = new ManyToManyEntity()
        manyToManyEntity1.setName("Name 1")
        manyToManyEntity1.setSecondName("Second name 1")
        dataManager.save(manyToManyEntity1)

        ManyToManyEntity manyToManyEntity2 = new ManyToManyEntity()
        manyToManyEntity2.setName("Name 2")
        manyToManyEntity2.setSecondName("Second name 2")
        dataManager.save(manyToManyEntity2)

        ManyToManyEntity manyToManyEntity4 = new ManyToManyEntity()
        manyToManyEntity4.setName("Name 4")
        manyToManyEntity4.setSecondName("Second name 4")
        dataManager.save(manyToManyEntity4)

        ManyToManyEntity manyToManyEntity3 = new ManyToManyEntity()
        manyToManyEntity3.setName("Name 3")
        manyToManyEntity3.setSecondName("Second name 3")
        dataManager.save(manyToManyEntity3)

        ManyToManyEntity manyToManyEntity5 = new ManyToManyEntity()
        manyToManyEntity5.setName("Name 5")
        manyToManyEntity5.setSecondName("Second name 5")
        dataManager.save(manyToManyEntity5)

        List<ManyToManyEntity> manyToManyEntities = new ArrayList<>()
        manyToManyEntities.add(manyToManyEntity1)
        manyToManyEntities.add(manyToManyEntity2)
        manyToManyEntities.add(manyToManyEntity3)
        manyToManyEntities.add(manyToManyEntity4)
        manyToManyEntities.add(manyToManyEntity5)
        UUID oneId = manyToManyEntity1.getId()


        ManyToManyTwoEntity manyToManyTwoEntity1 = new ManyToManyTwoEntity()
        manyToManyTwoEntity1.setName("Name 1")
        manyToManyTwoEntity1.setSecondName("Second name 1")
        manyToManyTwoEntity1.setManyToManyEntities(manyToManyEntities)
        dataManager.save(manyToManyTwoEntity1)
        UUID twoId = manyToManyTwoEntity1.getId()

        ManyToManyTwoEntity manyToManyTwoEntity2 = new ManyToManyTwoEntity()
        manyToManyTwoEntity2.setName("Name 2")
        manyToManyTwoEntity2.setSecondName("Second name 2")
        manyToManyTwoEntity2.setManyToManyEntities(manyToManyEntities)
        dataManager.save(manyToManyTwoEntity2)

        return twoId
    }
}
