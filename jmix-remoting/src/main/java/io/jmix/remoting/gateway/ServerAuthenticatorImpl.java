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

package io.jmix.remoting.gateway;

import io.jmix.core.security.ClientDetails;
import io.jmix.core.security.SystemAuthenticationToken;
import io.jmix.core.security.UserSession;
import io.jmix.core.security.UserSessionFactory;
import io.jmix.core.security.impl.SystemSessions;
import io.jmix.remoting.annotation.Remote;
import io.jmix.remoting.impl.ClientTokenSupport;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component(ServerAuthenticator.NAME)
@Profile("remoting")
@Remote
public class ServerAuthenticatorImpl implements ServerAuthenticator {

    @Inject
    protected AuthenticationManager authenticationManager;

    @Inject
    protected UserSessionFactory userSessionFactory;

    @Inject
    protected SystemSessions systemSessions;

    @Inject
    protected ClientTokenSupport clientTokenSupport;

    @Override
    public UserSession authenticateByClientToken(String login, String clientToken) {
        if (!clientTokenSupport.matches(clientToken)) {
            throw new BadCredentialsException("Client token doesn't match");
        }

        UserSession session = systemSessions.get(login);
        if (session != null) {
            return session;
        }

        Authentication authToken = new SystemAuthenticationToken(login);
        Authentication authentication = authenticationManager.authenticate(authToken);
        session = userSessionFactory.create(authentication);
        session.setClientDetails(ClientDetails.builder().info("System authentication").build());

        systemSessions.put(login, session);
        return session;
    }
}
