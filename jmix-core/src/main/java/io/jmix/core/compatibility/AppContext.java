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
package io.jmix.core.compatibility;

import io.jmix.core.Events;
import io.jmix.core.security.SecurityContext;
import io.jmix.core.event.AppContextInitializedEvent;
import io.jmix.core.event.AppContextStartedEvent;
import io.jmix.core.event.AppContextStoppedEvent;
import io.jmix.core.security.impl.SecurityContextHolder;
import io.jmix.core.security.impl.ThreadLocalSecurityContextHolder;
import io.jmix.core.impl.logging.LogMdc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.Authentication;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * System-level class with static methods providing access to some central application structures:
 * <ul>
 *     <li>Spring's {@link ApplicationContext}</li>
 *     <li>Application properties which were set in {@code app.properties} files</li>
 *     <li>Current thread's {@link SecurityContext}</li>
 * </ul>
 * It also allows to register listeners which are triggered on the application start/stop, and provides the method
 * {@link #isStarted()} to check whether the app is fully initialized at the moment.
 */
public class AppContext {

    private static final Logger log = LoggerFactory.getLogger(AppContext.class);

    private static ApplicationContext context;

    private static SecurityContextHolder securityContextHolder = new ThreadLocalSecurityContextHolder();

    private static AppProperties appProperties;

    private static volatile boolean started;
    private static volatile boolean listenersNotified;

    /**
     * INTERNAL.
     * Used by other framework classes to get access Spring's context.
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * @return all property names defined in the set of {@code app.properties} files
     */
    public static String[] getPropertyNames() {
        if (appProperties == null)
            throw new IllegalStateException("appProperties not initialized");
        return appProperties.getPropertyNames();
    }

    /**
     * Get property value defined in the set of {@code app.properties} files.
     * @param key   property key
     * @return      property value or null if the key is not found
     */
    @Nullable
    public static String getProperty(String key) {
        if (appProperties == null)
            throw new IllegalStateException("appProperties not initialized");
        return appProperties.getProperty(key);
    }

    /**
     * Set property value. The new value will be accessible at the runtime through {@link #getProperty(String)} and
     * {@link #getPropertyNames()}, but will not be saved in any {@code app.properties} file and will be lost
     * after the application restart.
     * @param key       property key
     * @param value     property value. If null, the property will be removed.
     */
    public static void setProperty(String key, @Nullable String value) {
        if (appProperties == null)
            throw new IllegalStateException("appProperties not initialized");
        appProperties.setProperty(key, value);
    }

    /**
     * @return  current thread's {@link SecurityContext} or null if there is no context bound
     */
    @Nullable
    public static SecurityContext getSecurityContext() {
        return securityContextHolder.get();
    }

    /**
     * @return  current thread's {@link SecurityContext}
     * @throws SecurityException if there is no context bound to the current thread
     */
    public static SecurityContext getSecurityContextNN() {
        SecurityContext securityContext = getSecurityContext();
        if (securityContext == null)
            // todo think about using NoUserSessionException or introduce a specific exception
            throw new SecurityException("No security context bound to the current thread");

        return securityContext;
    }

    /**
     * Set current thread's {@link SecurityContext}.
     * @param securityContext security context to be set for the current thread
     */
    public static void setSecurityContext(@Nullable SecurityContext securityContext) {
        log.trace("setSecurityContext {} for thread {}", securityContext, Thread.currentThread());

        securityContextHolder.set(securityContext);
        LogMdc.setup(securityContext);

        Authentication authentication = securityContext != null ? securityContext.getSession().getAuthentication() : null;
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Sets current thread's {@link SecurityContext}, invokes runnable and sets previous security context back.
     *
     * @param securityContext security context to be set for the current thread
     * @param runnable        runnable
     */
    public static void withSecurityContext(SecurityContext securityContext, Runnable runnable) {
        SecurityContext previousSecurityContext = getSecurityContext();
        setSecurityContext(securityContext);
        try {
            runnable.run();
        } finally {
            setSecurityContext(previousSecurityContext);
        }
    }

    /**
     * Sets current thread's {@link SecurityContext}, calls operation and sets previous security context back.
     *
     * @param securityContext security context to be set for the current thread
     * @param operation       operation
     * @return result of operation
     */
    public static <T> T withSecurityContext(SecurityContext securityContext, SecuredOperation<T> operation) {
        SecurityContext previousSecurityContext = getSecurityContext();
        setSecurityContext(securityContext);
        try {
            return operation.call();
        } finally {
            setSecurityContext(previousSecurityContext);
        }
    }

    /**
     * @return true if the application context is initialized
     */
    public static boolean isStarted() {
        return started;
    }

    /**
     * @return true if the application context is initialized and all listeners have been notified
     */
    public static boolean isReady() {
        return started && listenersNotified;
    }

    /**
     * INTERNAL.
     * Contains methods for setting up AppContext internals.
     */
    public static class Internals {

        /**
         * Called by the framework to set Spring's context.
         *
         * @param applicationContext initialized Spring's context
         */
        public static void setApplicationContext(@Nullable ApplicationContext applicationContext) {
            context = applicationContext;
            appProperties = applicationContext != null ?
                    applicationContext.getBean(AppProperties.NAME, AppProperties.class) : null;

            Events events = getApplicationContext().getBean(Events.NAME, Events.class);
            events.publish(new AppContextInitializedEvent(context));
        }

        /**
         * Called by the framework to replace standard thread-local holder.
         *
         * @param holder a holder implementation
         */
        public static void setSecurityContextHolder(SecurityContextHolder holder) {
            AppContext.securityContextHolder = holder;
        }

        /**
         * Called by the framework after the application has been started and fully initialized.
         */
        public static void startContext() {
            started = true;

            Events events = getApplicationContext().getBean(Events.NAME, Events.class);
            events.publish(new AppContextStartedEvent(context));

            listenersNotified = true;
        }

        /**
         * Called by the framework right before the application shutdown.
         */
        public static void stopContext() {
            started = false;

            Events events = getApplicationContext().getBean(Events.NAME, Events.class);
            events.publish(new AppContextStoppedEvent(context));

            if (context instanceof ConfigurableApplicationContext) {
                ((ConfigurableApplicationContext) context).close();
            }
        }

        /**
         * Direct access to the {@link AppProperties} object.
         */
        public static AppProperties getAppProperties() {
            return AppContext.appProperties;
        }
    }

    public interface SecuredOperation<T> {
        T call();
    }
}