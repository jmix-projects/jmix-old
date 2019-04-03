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

package io.jmix.samples.customsecurity

import io.jmix.core.security.Security
import io.jmix.core.security.UserSession
import io.jmix.core.security.UserSessionManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import spock.lang.Specification

import javax.inject.Inject

@SpringBootTest
class CustomSecurityImplTest extends Specification {

    @Inject
    Security security

    @Inject
    UserDetailsService userDetailsService

    @Inject
    UserSessionManager userSessionManager

    def "custom implementations are in use"() {
        expect:

        security instanceof CustomSecurityImpl
        userDetailsService.loadUserByUsername('admin') instanceof CustomUser
    }

    def "authentication yields custom session and user"() {

        when:

        def session = userSessionManager.createSession(new UsernamePasswordAuthenticationToken('admin', 'admin123'))

        then:

        session instanceof CustomUserSession
        session.user instanceof CustomUser
        session.authentication.principal.is(session.user)

        cleanup:

        userSessionManager.removeSession()
    }
}
