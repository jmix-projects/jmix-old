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

package io.jmix.backend

import com.sample.app.TestAppConfiguration
import com.sample.app.entity.TestAppEntity
import io.jmix.backend.test.JmixBackendTestConfiguration
import io.jmix.core.DataManager
import io.jmix.core.EntityStates
import io.jmix.core.JmixCoreConfiguration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.inject.Inject

@ContextConfiguration(classes = [JmixCoreConfiguration, JmixBackendConfiguration, JmixBackendTestConfiguration, TestAppConfiguration])
class DataManagerTest extends Specification {

    @Inject
    DataManager dataManager

    @Inject
    EntityStates entityStates

    def "create commit load"() {
        when:

        def entity = dataManager.create(TestAppEntity)
        entity.name = 'e1'

        then:

        entityStates.isNew(entity)

        when:

        def entity1 = dataManager.commit(entity)

        then:

        entity1.version == 1
        !entityStates.isNew(entity1)
        entityStates.isDetached(entity1)

        when:

        def entity2 = dataManager.load(TestAppEntity).id(entity.id).one()

        then:

        !entityStates.isNew(entity2)
        entityStates.isDetached(entity2)

    }
}
