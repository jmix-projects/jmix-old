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
import io.jmix.core.security.Authenticator;
import io.jmix.core.security.SecurityContext;
import io.jmix.core.security.UserSession;
import io.jmix.core.security.UserSessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class RemotingAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RemotingAuthenticationFilter.class);

    protected UserSessions userSessions;

    protected Authenticator authenticator;

    protected ClientTokenSupport clientTokenSupport;

    public RemotingAuthenticationFilter(UserSessions userSessions, Authenticator authenticator,
                                        ClientTokenSupport clientTokenSupport) {
        this.userSessions = userSessions;
        this.authenticator = authenticator;
        this.clientTokenSupport = clientTokenSupport;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            throw new BadCredentialsException("Authorization header is null");
        }
        authenticate(authHeader);

        filterChain.doFilter(request, response);
    }

    private void authenticate(String authHeaderStr) {
        AuthHeader header = AuthHeader.decode(authHeaderStr);
        if (header.getClientToken() != null) {
            if (!clientTokenSupport.matches(header.getClientToken())) {
                throw new BadCredentialsException("Client token doesn't match");
            }
            authenticator.begin(header.getUser());

        } else {
            UUID sessionId;
            try {
                sessionId = UUID.fromString(header.getSessionId());
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid user session format: " + header.getSessionId());
            }
            UserSession userSession = userSessions.getAndRefresh(sessionId);
            if (userSession == null) {
                throw new SessionAuthenticationException("User session " + sessionId + " does not exist");
            }
            AppContext.setSecurityContext(new SecurityContext(userSession));
        }

    }
}
