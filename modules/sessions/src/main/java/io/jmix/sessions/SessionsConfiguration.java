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

package io.jmix.sessions;

import io.jmix.core.CoreConfiguration;
import io.jmix.core.Events;
import io.jmix.core.annotation.JmixModule;
import io.jmix.sessions.validators.VaadinSessionAttributesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.SessionRepositoryFilter;

@Configuration
@ComponentScan
@JmixModule(dependsOn = CoreConfiguration.class)
public class SessionsConfiguration<S extends Session> {

    protected SessionRepositoryWrapper<S> sessionRepositoryWrapper;

    @Autowired
    protected SessionRegistry sessionRegistry;

    @Autowired
    protected Events events;

    @Autowired
    public void setSessionRepository(FindByIndexNameSessionRepository<S> sessionRepository) {
        sessionRepositoryWrapper = new SessionRepositoryWrapper<>(sessionRepository,sessionRegistry,events);
        sessionRepositoryWrapper.addAttributePersistenceValidators(new VaadinSessionAttributesValidator());
    }

    @Bean
    public SessionRepositoryFilter<SessionRepositoryWrapper<S>.SessionWrapper> springSessionRepositoryFilter() {
        SessionRepositoryFilter<SessionRepositoryWrapper<S>.SessionWrapper> sessionRepositoryFilter = new SessionRepositoryFilter<>(sessionRepositoryWrapper);
        sessionRepositoryFilter.setHttpSessionIdResolver(new CookieHttpSessionIdResolver());
        return sessionRepositoryFilter;
    }
}
