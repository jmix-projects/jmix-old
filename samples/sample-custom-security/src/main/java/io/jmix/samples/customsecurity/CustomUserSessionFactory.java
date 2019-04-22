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

package io.jmix.samples.customsecurity;

import io.jmix.core.security.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component(UserSessionFactory.NAME)
public class CustomUserSessionFactory implements UserSessionFactory {

    private final UserSession SYSTEM_SESSION;

    public CustomUserSessionFactory() {
        CustomUser user = new CustomUser("system", "", "System");
        SystemAuthenticationToken authentication = new SystemAuthenticationToken(user, Collections.emptyList());
        SYSTEM_SESSION = new BuiltInSystemUserSession(authentication);
    }

    @Override
    public UserSession create(Authentication authentication) {
        return new CustomUserSession(authentication);
    }

    @Override
    public UserSession getSystemSession() {
        return SYSTEM_SESSION;
    }

    private static class BuiltInSystemUserSession extends UserSession implements SystemUserSession {

        private static final long serialVersionUID = 7080514121793124904L;

        public BuiltInSystemUserSession(SystemAuthenticationToken authentication) {
            super(authentication);
            clientDetails = ClientDetails.builder().info("System authentication").build();
        }
    }

}
