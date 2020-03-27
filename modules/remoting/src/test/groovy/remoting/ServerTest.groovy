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

package remoting

import io.jmix.core.JmixCoreConfiguration
import io.jmix.data.JmixDataConfiguration
import io.jmix.remoting.JmixRemotingConfiguration
import io.jmix.remoting.impl.ServerEndpointExporter
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import test_support.JmixRemotingTestConfiguration
import test_support.TestService
import test_support.TestServiceImpl

import javax.inject.Inject

@ContextConfiguration(classes = [JmixCoreConfiguration, JmixDataConfiguration, JmixRemotingConfiguration,
        JmixRemotingTestConfiguration])
@ActiveProfiles(["remoting","server"])
class ServerTest extends Specification {

    @Inject
    ApplicationContext applicationContext

    def "context has correct beans"() {

        def testService = applicationContext.getBean(TestService.class)

        expect:

        // direct implementation
        testService instanceof TestServiceImpl

        // export
        applicationContext.getBean('/remoting/' + TestService.NAME) instanceof ServerEndpointExporter
    }
}
