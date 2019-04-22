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

package io.jmix.remoting.impl;

import io.jmix.core.compatibility.AppContext;
import io.jmix.core.security.SecurityContext;
import io.jmix.core.security.SystemAuthenticationToken;
import io.jmix.core.security.SystemUserSession;
import io.jmix.core.security.UserSession;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.net.HttpURLConnection;

@Component(ClientRequestExecutor.NAME)
public class ClientRequestExecutor extends SimpleHttpInvokerRequestExecutor {

    public static final String NAME = "jmix_ClientRequestExecutor";

    @Inject
    protected ClientTokenSupport clientTokenSupport;

    @Override
    protected void prepareConnection(HttpURLConnection connection, int contentLength) throws IOException {
        super.prepareConnection(connection, contentLength);
        SecurityContext securityContext = AppContext.getSecurityContext();
        if (securityContext != null) {
            String authValue;
            UserSession userSession = securityContext.getSession();
            if (userSession.getAuthentication() instanceof SystemAuthenticationToken) {
                String clientToken = clientTokenSupport.current();
                if (clientToken == null) {
                    throw new IllegalStateException("Client token is not set");
                }
                if (userSession instanceof SystemUserSession) {
                    authValue = AuthHeader.encode(clientToken, null);
                } else {
                    authValue = AuthHeader.encode(clientToken, userSession.getUser().getLoginLowerCase());
                }
            } else {
                authValue = AuthHeader.encode(userSession.getId());
            }
            connection.setRequestProperty("Authorization", authValue);
        }
    }
}
