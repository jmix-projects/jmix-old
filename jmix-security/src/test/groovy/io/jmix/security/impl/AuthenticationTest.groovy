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

package io.jmix.security.impl

import io.jmix.core.DataManager
import io.jmix.core.JmixCoreConfiguration
import io.jmix.core.compatibility.AppContext
import io.jmix.core.security.UserSessionManager
import io.jmix.data.JmixDataConfiguration
import io.jmix.security.JmixSecurityConfiguration
import io.jmix.security.entity.User
import io.jmix.security.test.JmixSecurityTestConfiguration
import io.jmix.security.test.TestSupport
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.inject.Inject

@ContextConfiguration(classes = [JmixCoreConfiguration, JmixDataConfiguration, JmixSecurityConfiguration, JmixSecurityTestConfiguration])
class AuthenticationTest extends Specification {

    @Inject
    DataManager dataManager

    @Inject
    TestSupport testSupport

    @Inject
    UserSessionManager userSessionManager

    def "create and remove session"() {

        def user = new User(login: 'user1', password: '{noop}123')
        dataManager.commit(user)

        when:

        def token = new UsernamePasswordAuthenticationToken('user1', '123')
        def session = userSessionManager.createSession(token)

        then:

        AppContext.getSecurityContext().session == session
        session.user == user

        when:

        userSessionManager.removeSession()

        then:

        AppContext.getSecurityContext() == null

        cleanup:

        testSupport.deleteRecord(user)
    }

}
