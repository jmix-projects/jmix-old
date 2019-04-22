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

package io.jmix.remoting;

import io.jmix.core.DataManager;
import io.jmix.core.annotation.JmixComponent;
import io.jmix.core.impl.ConfigStorage;
import io.jmix.core.security.Authenticator;
import io.jmix.core.security.UserSessionManager;
import io.jmix.core.security.UserSessions;
import io.jmix.core.security.impl.SystemSessions;
import io.jmix.data.JmixDataConfiguration;
import io.jmix.remoting.gateway.*;
import io.jmix.remoting.impl.ClientTokenSupport;
import io.jmix.remoting.impl.RemotingAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.inject.Inject;

@Configuration
@ComponentScan
@JmixComponent(dependsOn = JmixDataConfiguration.class)
public class JmixRemotingConfiguration {

    @Bean(name = UserSessionManager.NAME)
    @Profile("client")
    public UserSessionManager userSessionManager() {
        return new ClientUserSessionManager();
    }

    @Bean(name = Authenticator.NAME)
    @Profile("client")
    public Authenticator authenticator(SystemSessions systemSessions) {
        return new ClientAuthenticator(systemSessions);
    }

    @Bean(name = DataManager.NAME)
    @Profile("client")
    public DataManager dataManager() {
        return new ClientDataManager();
    }

    @Bean(name = ConfigStorage.NAME)
    @Profile("client")
    public ConfigStorage configStorage() {
        return new ClientConfigStorage();
    }

    @Configuration
    @Order(80)
    public static class AuthenticationEndpointSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers()
                    .antMatchers("/remoting/" + ServerUserSessionManager.NAME + "/**",
                            "/remoting/" + ServerAuthenticator.NAME + "/**")
                    .and()
                    .authorizeRequests().anyRequest().permitAll()
                    .and()
                    .csrf().disable();
        }
    }

    @Configuration
    @Order(90)
    public static class RemotingEndpointsSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Inject
        protected UserSessions userSessions;
        @Inject
        protected Authenticator authenticator;
        @Inject
        protected ClientTokenSupport clientTokenSupport;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            RemotingAuthenticationFilter remotingFilter = new RemotingAuthenticationFilter(
                    userSessions, authenticator, clientTokenSupport);

            http.antMatcher("/remoting/**")
                    .addFilterBefore(remotingFilter, BasicAuthenticationFilter.class)
                    .authorizeRequests().anyRequest().permitAll()
                    .and()
                    .csrf().disable();
        }
    }
}
