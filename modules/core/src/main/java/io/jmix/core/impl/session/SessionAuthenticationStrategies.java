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

package io.jmix.core.impl.session;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

public class SessionAuthenticationStrategies implements SessionAuthenticationStrategy {

    private CompositeSessionAuthenticationStrategy compositeStrategy;

    public SessionAuthenticationStrategies(SessionRegistry sessionRegistry, int maximumUserSessions) {
        compositeStrategy = new CompositeSessionAuthenticationStrategy(strategies(sessionRegistry, maximumUserSessions));
    }

    protected List<SessionAuthenticationStrategy> strategies(SessionRegistry sessionRegistry, int maximumUserSessions) {
        SessionFixationProtectionStrategy sessionFixationProtectionStrategy
                = new SessionFixationProtectionStrategy();
        sessionFixationProtectionStrategy.setMigrateSessionAttributes(true);

        RegisterSessionAuthenticationStrategy registerSessionAuthenticationStrategy
                = new RegisterSessionAuthenticationStrategy(sessionRegistry);
        ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlStrategy
                = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        concurrentSessionControlStrategy.setMaximumSessions(maximumUserSessions);

        List<SessionAuthenticationStrategy> strategies = new LinkedList<>();

        strategies.add(sessionFixationProtectionStrategy);
        strategies.add(registerSessionAuthenticationStrategy);
        strategies.add(concurrentSessionControlStrategy);
        strategies.add(new SessionDataAuthenticationStrategy());
        return strategies;
    }

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException {
        compositeStrategy.onAuthentication(authentication, request, response);
    }
}
