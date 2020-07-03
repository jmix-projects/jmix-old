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

import com.google.common.collect.Sets;
import io.jmix.core.session.HttpSessionRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HttpSessionRegistryImpl implements HttpSessionRegistry {

    protected SessionRegistry delegate;

    private final Set<Object> principals = Sets.newConcurrentHashSet();

    public SessionRegistry getDelegate() {
        return delegate;
    }

    public void setDelegate(SessionRegistry delegate) {
        this.delegate = delegate;
    }

    @EventListener
    public void handleSessionDestroyed(HttpSessionDestroyedEvent event) {
        String s = "";
    }

    @Override
    public List<Object> getAllPrincipals() {
        return new ArrayList<>(principals);
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        return delegate.getAllSessions(principal, includeExpiredSessions);
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        return delegate.getSessionInformation(sessionId);
    }

    @Override
    public void refreshLastRequest(String sessionId) {
        delegate.refreshLastRequest(sessionId);
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        delegate.registerNewSession(sessionId, principal);
        addPrincipal(principal);
    }

    protected void addPrincipal(Object principal) {
        principals.add(principal);
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        SessionInformation sessionInformation = delegate.getSessionInformation(sessionId);
        if (sessionInformation != null) {
            Object principal = sessionInformation.getPrincipal();
            delegate.removeSessionInformation(sessionId);
            if (delegate.getAllSessions(principal, false).isEmpty()) {
                removePrincipal(principal);
            }
        }
    }

    private void removePrincipal(Object principal) {
        principals.remove(principal);
    }

    @Nullable
    @Override
    public SessionInformation currentSessionInfo() {
        String sessionId = currentSessionId();
        return sessionId != null ? delegate.getSessionInformation(sessionId) : null;
    }

    @Nullable
    @Override
    public String currentSessionId() {
        return RequestContextHolder.getRequestAttributes() != null ?
                RequestContextHolder.currentRequestAttributes().getSessionId()
                : null;
    }

    @Override
    public void expire(String sessionId) {
        SessionInformation sessionInformation = delegate.getSessionInformation(sessionId);
        if (sessionInformation != null) {
            sessionInformation.expireNow();
            Object principal = sessionInformation.getPrincipal();
            if (delegate.getAllSessions(principal, false).isEmpty()) {
                removePrincipal(principal);
            }
        }
    }
}
