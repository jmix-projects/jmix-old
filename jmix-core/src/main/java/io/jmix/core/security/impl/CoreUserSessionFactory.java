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

package io.jmix.core.security.impl;

import io.jmix.core.security.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(UserSessionFactory.NAME)
@ConditionalOnSecurityImplementation("core")
public class CoreUserSessionFactory implements UserSessionFactory {

    private final UserSession SERVER_SESSION;

    public CoreUserSessionFactory() {
        CoreUser user = new CoreUser("server", "", "Server");
        SystemAuthenticationToken authentication = new SystemAuthenticationToken(user);
        SERVER_SESSION = new UserSession(authentication) {
            { id = new UUID(1L, 1L); }
        };
        SERVER_SESSION.setClientDetails(ClientDetails.builder().info("System authentication").build());
    }

    @Override
    public UserSession create(Authentication authentication) {
        return new UserSession(authentication);
    }

    @Override
    public UserSession getServerSession() {
        return SERVER_SESSION;
    }
}
