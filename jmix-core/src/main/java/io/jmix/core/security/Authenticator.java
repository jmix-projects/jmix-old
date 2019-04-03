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

package io.jmix.core.security;

import com.google.common.base.Strings;
import io.jmix.core.Events;
import io.jmix.core.compatibility.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean that provides authentication to an arbitrary code on the Middleware.
 * <br>
 * Authentication is required if the code doesn't belong to a normal user request handling, which is the case for
 * invocation by schedulers or JMX tools, other than Web Client's JMX-console.
 * <br>
 * Example usage:
 * <pre>
 *     authenticator.begin();
 *     try {
 *         // valid current thread's user session presents here
 *     } finally {
 *         authenticator.end();
 *     }
 * </pre>
 */
@Component(Authenticator.NAME)
public class Authenticator {

    public static final String NAME = "jmix_Authenticator";

    private static final Logger log = LoggerFactory.getLogger(Authenticator.class);

    private static final SecurityContext NULL_CONTEXT = new SecurityContext(new NullSession());

    @Inject
    protected AuthenticationManager authenticationManager;

    @Inject
    protected UserSessionFactory userSessionFactory;

    protected ThreadLocal<Deque<SecurityContext>> threadLocalStack = new ThreadLocal<>();

    protected Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    @EventListener
    @Order(Events.HIGHEST_CORE_PRECEDENCE + 5)
    protected void beginServerSessionOnStartup(ContextRefreshedEvent event) {
        begin();
    }

    @EventListener
    @Order(Events.LOWEST_CORE_PRECEDENCE - 5)
    protected void endServerSessionOnStartup(ContextRefreshedEvent event) {
        end();
    }

    /**
     * Begins an authenticated code block.
     * <br>
     * Saves the current thread session on a stack to get it back on {@link #end()}.
     * Subsequent {@link #end()} method must be called in "finally" section.
     *
     * @param login user login. If null, the 'server' system session is started
     * @return new or cached instance of system user session
     */
    public UserSession begin(@Nullable String login) {
        UserSession session;

        if (!Strings.isNullOrEmpty(login)) {
            log.trace("Authenticating as {}", login);
            session = sessions.get(login);
            if (session == null) {
                // saved session doesn't exist
                synchronized (this) {
                    // double check to prevent the same log in by subsequent threads
                    session = sessions.get(login);
                    if (session == null) {
                        try {
                            Authentication authToken = new SystemAuthenticationToken(login);
                            Authentication authentication = authenticationManager.authenticate(authToken);
                            session = userSessionFactory.create(authentication);
                            session.setClientDetails(ClientDetails.builder().info("System authentication").build());
                        } catch (LoginException e) {
                            throw new RuntimeException("Unable to perform system login", e);
                        }
                        sessions.put(login, session);
                    }
                }
            }
        } else {
            log.trace("Authenticating as server");
            session = userSessionFactory.getServerSession();
        }

        pushSecurityContext(AppContext.getSecurityContext());

        AppContext.setSecurityContext(new SecurityContext(session));

        return session;
    }

    /**
     * Authenticate with the 'server' system session.
     * <br>
     * Same as {@link #begin(String)} with null parameter.
     */
    public UserSession begin() {
        return begin(null);
    }

    /**
     * End of an authenticated code block.
     * <br>
     * The previous session (or null) is set to security context.
     * Must be called in "finally" section of a try/finally block.
     */
    public void end() {
        log.trace("Set previous SecurityContext");
        SecurityContext previous = popSecurityContext();
        AppContext.setSecurityContext(previous);
    }

    /**
     * Execute code on behalf of the specified user.
     *
     * @param login     user login. If null, the 'server' system session is used.
     * @param operation code to execute
     * @return result of the execution
     */
    public <T> T withUser(@Nullable String login, AuthenticatedOperation<T> operation) {
        SecurityContext previousSecurityContext = AppContext.getSecurityContext();
        AppContext.setSecurityContext(null);
        try {
            begin(login);
            return operation.call();
        } finally {
            AppContext.setSecurityContext(previousSecurityContext);
        }
    }

    /**
     * Execute code as the 'server' system user.
     *
     * @param operation code to execute
     * @return result of the execution
     */
    public <T> T withServer(AuthenticatedOperation<T> operation) {
        SecurityContext previousSecurityContext = AppContext.getSecurityContext();
        AppContext.setSecurityContext(null);
        try {
            begin(null);
            return operation.call();
        } finally {
            AppContext.setSecurityContext(previousSecurityContext);
        }
    }

    private void pushSecurityContext(SecurityContext securityContext) {
        Deque<SecurityContext> stack = threadLocalStack.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            threadLocalStack.set(stack);
        } else {
            if (stack.size() > 10) {
                log.warn("Stack is too big: {}. Check correctness of begin/end invocations.", stack.size());
            }
        }
        if (securityContext == null) {
            securityContext = NULL_CONTEXT;
        }
        stack.push(securityContext);
    }

    private SecurityContext popSecurityContext() {
        Deque<SecurityContext> stack = threadLocalStack.get();
        if (stack != null) {
            SecurityContext securityContext = stack.poll();
            if (securityContext != null) {
                if (securityContext == NULL_CONTEXT) {
                    return null;
                } else {
                    return securityContext;
                }
            } else {
                log.warn("Stack is empty. Check correctness of begin/end invocations.");
            }
        } else {
            log.warn("Stack does not exist. Check correctness of begin/end invocations.");
        }
        return null;
    }

    public interface AuthenticatedOperation<T> {
        T call();
    }

    private static class NullSession extends UserSession {
        public NullSession() {
            id = new UUID(0L, 0L);
        }
    }
}