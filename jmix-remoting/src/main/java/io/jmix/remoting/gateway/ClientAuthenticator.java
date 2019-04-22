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

import com.google.common.base.Strings;
import io.jmix.core.compatibility.AppContext;
import io.jmix.core.security.*;
import io.jmix.core.security.impl.SystemSessions;
import io.jmix.remoting.impl.ClientTokenSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class ClientAuthenticator extends AuthenticatorSupport implements Authenticator {

    private static final Logger log = LoggerFactory.getLogger(ClientAuthenticator.class);

    @Inject
    protected ServerAuthenticator serverAuthenticator;

    @Inject
    protected UserSessionFactory userSessionFactory;

    @Inject
    protected ClientTokenSupport clientTokenSupport;

    @Inject
    public ClientAuthenticator(SystemSessions sessions) {
        super(sessions);
    }

    @Override
    public UserSession begin(@Nullable String login) {
        UserSession userSession;

        if (!Strings.isNullOrEmpty(login)) {
            log.trace("Authenticating as {}", login);

            userSession = getFromCacheOrCreate(login, () -> {
                String clientToken = clientTokenSupport.current();
                if (clientToken != null) {
                    return serverAuthenticator.authenticateByClientToken(login, clientToken);
                } else {
                    throw new IllegalStateException("Property jmix.clientToken is not set");
                }
            });

        } else {
            log.trace("Authenticating as system");
            userSession = userSessionFactory.getSystemSession();
        }

        pushSecurityContext(AppContext.getSecurityContext());

        AppContext.setSecurityContext(new SecurityContext(userSession));

        return userSession;
    }

    @Override
    public UserSession begin() {
        return begin(null);
    }

    @Override
    public void end() {
        log.trace("Set previous SecurityContext");
        SecurityContext previous = popSecurityContext();
        AppContext.setSecurityContext(previous);
    }

    @Override
    public <T> T withUser(@Nullable String login, AuthenticatedOperation<T> operation) {
        begin(login);
        try {
            return operation.call();
        } finally {
            end();
        }
    }

    @Override
    public <T> T withSystem(AuthenticatedOperation<T> operation) {
        begin();
        try {
            return operation.call();
        } finally {
            end();
        }
    }
}
