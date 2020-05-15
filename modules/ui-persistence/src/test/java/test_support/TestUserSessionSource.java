/*
 * Copyright 2020 Haulmont.
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

package test_support;

import io.jmix.core.entity.User;
import io.jmix.core.security.NoUserSessionException;
import io.jmix.core.security.UserSession;
import io.jmix.core.security.impl.CoreUser;
import io.jmix.core.security.impl.UserSessionSourceImpl;

public class TestUserSessionSource extends UserSessionSourceImpl {

    protected UserSession userSession;

    @Override
    public synchronized UserSession getUserSession() throws NoUserSessionException {
        if (userSession == null) {
            userSession = createTestSession();
        }
        return userSession;
    }

    protected UserSession createTestSession() {
        return new UserSession() {
            @Override
            public User getUser() {
                CoreUser user = new CoreUser("test_admin", "test_admin", "Test Administrator");
                return user;
            }
        };
    }
}