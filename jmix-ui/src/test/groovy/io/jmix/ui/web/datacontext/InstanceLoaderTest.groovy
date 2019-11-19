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

package io.jmix.ui.web.datacontext

import io.jmix.core.app.DataService
import io.jmix.core.DataManager
import io.jmix.core.Metadata
import io.jmix.ui.model.DataComponents
import io.jmix.ui.model.InstanceContainer
import io.jmix.ui.model.InstanceLoader
import com.haulmont.cuba.web.container.CubaTestContainer
import com.haulmont.cuba.web.testmodel.datacontext.Foo
import com.haulmont.cuba.web.testsupport.TestContainer
import com.haulmont.cuba.web.testsupport.proxy.TestServiceProxy
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

import java.util.function.Consumer

@SuppressWarnings("GroovyAssignabilityCheck")
class InstanceLoaderTest extends Specification {

    @Shared @ClassRule
    public TestContainer cont = CubaTestContainer.Common.INSTANCE

    private Metadata metadata
    private DataManager dataManager
    private DataComponents factory

    void setup() {
        metadata = cont.getBean(Metadata)
        dataManager = cont.getBean(DataManager)
        factory = cont.getBean(DataComponents)
    }

    void cleanup() {
        TestServiceProxy.clear()
    }

    def "successful load"() {
        InstanceLoader<Foo> loader = factory.createInstanceLoader()
        InstanceContainer<Foo> container = factory.createInstanceContainer(Foo)

        Consumer preLoadListener = Mock()
        loader.addPreLoadListener(preLoadListener)

        Consumer postLoadListener = Mock()
        loader.addPostLoadListener(postLoadListener)

        Foo foo = new Foo()

        TestServiceProxy.mock(DataService, Mock(DataService) {
            load(_) >> com.haulmont.cuba.client.testsupport.TestSupport.reserialize(foo)
        })

        when:

        loader.setContainer(container)
        loader.setEntityId(foo.id)
        loader.load()

        then:

        container.getItem() == foo

        1 * preLoadListener.accept(_)
        1 * postLoadListener.accept(_)
    }

    def "prevent load by PreLoadEvent"() {
        InstanceLoader<Foo> loader = factory.createInstanceLoader()
        InstanceContainer<Foo> container = factory.createInstanceContainer(Foo)

        Consumer preLoadListener = { InstanceLoader.PreLoadEvent e -> e.preventLoad() }
        loader.addPreLoadListener(preLoadListener)

        Consumer postLoadListener = Mock()
        loader.addPostLoadListener(postLoadListener)

        Foo foo = new Foo()

        when:

        loader.setContainer(container)
        loader.setEntityId(foo.id)
        loader.load()

        then:

        container.getItemOrNull() == null

        0 * postLoadListener.accept(_)
    }
}
