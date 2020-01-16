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

import io.jmix.core.security.*;
import io.jmix.remoting.annotation.Remote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component(ServerUserSessionManager.NAME)
@Profile("remoting")
@Remote
public class ServerUserSessionManagerImpl implements ServerUserSessionManager {

    private static final Logger log = LoggerFactory.getLogger(ServerUserSessionManagerImpl.class);

    @Inject
    protected UserSessionManager userSessionManager;

    @Inject
    protected AuthenticationManager authenticationManager;

    @Inject
    protected UserSessionFactory userSessionFactory;

    @Inject
    protected Authenticator authenticator;

    @Override
    public UserSession createSession(Authentication authToken) {
        if (authToken instanceof SystemAuthenticationToken) {
            Object clientToken = authToken.getCredentials();
            if (!"todo clientToken".equals(clientToken)) {
                throw new BadCredentialsException("Invalid client token");
            }

            UserSession session = authenticator.begin(authToken.getName());
            authenticator.end();

            log.info("Created system session: {}", session);
            return session;
        }

        return userSessionManager.createSession(authToken);
    }
}
