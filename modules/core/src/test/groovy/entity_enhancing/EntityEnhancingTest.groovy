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

package entity_enhancing

import io.jmix.core.JmixCoreConfiguration
import io.jmix.core.entity.EntityPropertyChangeListener
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import spock.lang.Specification
import test_support.AppContextTestExecutionListener
import test_support.addon1.TestAddon1Configuration
import test_support.addon1.entity.TestAddon1Entity
import test_support.app.TestAppConfiguration
import test_support.app.entity.Pet

@ContextConfiguration(classes = [TestAppConfiguration, TestAddon1Configuration, JmixCoreConfiguration])
@TestExecutionListeners(value = AppContextTestExecutionListener,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
class EntityEnhancingTest extends Specification {

    def "JPA entity is enhanced"() {

        def pet = new Pet()
        EntityPropertyChangeListener listener = Mock()
        pet.addPropertyChangeListener(listener)

        when:
        pet.setName('Misty')

        then:
        1 * listener.propertyChanged(_)
    }

    def "non-JPA entity is enhanced"() {

        def entity = new TestAddon1Entity()
        EntityPropertyChangeListener listener = Mock()
        entity.addPropertyChangeListener(listener)

        when:
        entity.setName('Misty')

        then:
        1 * listener.propertyChanged(_)
    }
}
