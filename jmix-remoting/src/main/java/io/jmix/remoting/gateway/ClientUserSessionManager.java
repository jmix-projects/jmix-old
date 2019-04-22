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

import io.jmix.core.compatibility.AppContext;
import io.jmix.core.security.SecurityContext;
import io.jmix.core.security.SystemAuthenticationToken;
import io.jmix.core.security.UserSession;
import io.jmix.core.security.UserSessionManager;
import org.springframework.security.core.Authentication;

import javax.inject.Inject;

public class ClientUserSessionManager implements UserSessionManager {

    @Inject
    protected ServerUserSessionManager service;

    @Override
    public UserSession createSession(Authentication authToken) {
        if (authToken instanceof SystemAuthenticationToken) {
            ((SystemAuthenticationToken) authToken).setCredentials("todo clientPassword");
        }
        UserSession userSession = service.createSession(authToken);
        AppContext.setSecurityContext(new SecurityContext(userSession));
        return userSession;
    }

    @Override
    public void removeSession() {
        AppContext.setSecurityContext(null);
        // todo logout
    }
}
