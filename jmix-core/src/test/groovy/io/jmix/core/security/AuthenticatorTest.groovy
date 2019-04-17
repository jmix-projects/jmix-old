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

package io.jmix.core.security

import io.jmix.core.JmixCoreConfiguration
import io.jmix.core.compatibility.AppContext
import io.jmix.core.security.Authenticator
import io.jmix.core.security.UserSession
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

import javax.inject.Inject

@ContextConfiguration(classes = [JmixCoreConfiguration])
@TestPropertySource(properties = ["jmix.securityImplementation = core"])
class AuthenticatorTest extends Specification {

    @Inject
    Authenticator authenticator

    def "authenticate as system"() {
        when:

        authenticator.begin()

        then:

        UserSession session = AppContext.getSecurityContextNN().getSession()
        session.user.loginLowerCase == 'server'
        session.system

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
        authentication.principal == session.user

        when:

        authenticator.end()

        then:

        AppContext.getSecurityContext() == null
        SecurityContextHolder.getContext().getAuthentication() == null
    }

    def "authenticate as admin"() {
        when:

        authenticator.begin('admin')

        then:

        UserSession session = AppContext.getSecurityContextNN().getSession()
        session.user.loginLowerCase == 'admin'
        session.system

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
        authentication.principal == session.user

        when:

        authenticator.end()

        then:

        AppContext.getSecurityContext() == null
        SecurityContextHolder.getContext().getAuthentication() == null
    }

    def "nested authentication"() {

        when: "outer auth"

        authenticator.begin()

        then:

        UserSession outerSession = AppContext.getSecurityContextNN().getSession()
        outerSession.user.loginLowerCase == 'server'

        Authentication outerAuth = SecurityContextHolder.getContext().getAuthentication()
        outerAuth.principal == outerSession.user

        when: "inner auth"

        authenticator.begin('admin')

        then:

        UserSession innerSession = AppContext.getSecurityContextNN().getSession()
        innerSession.user.loginLowerCase == 'admin'
        innerSession.system

        Authentication innerAuth = SecurityContextHolder.getContext().getAuthentication()
        innerAuth.principal == innerSession.user

        when: "end inner"

        authenticator.end()

        then:

        UserSession outerSession1 = AppContext.getSecurityContextNN().getSession()
        outerSession1.user.loginLowerCase == 'server'

        Authentication outerAuth1 = SecurityContextHolder.getContext().getAuthentication()
        outerAuth1.principal == outerSession1.user

        when: "end outer"

        authenticator.end()

        then:

        AppContext.getSecurityContext() == null
        SecurityContextHolder.getContext().getAuthentication() == null
    }
}
