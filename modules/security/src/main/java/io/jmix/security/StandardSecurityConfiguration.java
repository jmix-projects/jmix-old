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

package io.jmix.security;

import io.jmix.core.CoreProperties;
import io.jmix.core.impl.session.HttpSessionRegistryImpl;
import io.jmix.core.impl.session.SessionAuthenticationStrategies;
import io.jmix.core.security.UserRepository;
import io.jmix.core.security.impl.SystemAuthenticationProvider;
import io.jmix.core.session.SessionProperties;
import io.jmix.core.session.HttpSessionRegistry;
import io.jmix.core.impl.session.SessionDataAuthenticationStrategy;
import io.jmix.security.authentication.SecuredAuthenticationProvider;
import io.jmix.security.role.RoleRepository;
import io.jmix.security.role.assignment.RoleAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.*;

import java.util.LinkedList;
import java.util.List;

@Configuration
@ComponentScan
@ConfigurationPropertiesScan
@Conditional(OnStandardSecurityImplementation.class)
@EnableWebSecurity
public class StandardSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CoreProperties coreProperties;

    @Autowired
    private SessionProperties sessionProperties;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleAssignmentRepository roleAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new SystemAuthenticationProvider(userRepository));

        SecuredAuthenticationProvider securedAuthenticationProvider = new SecuredAuthenticationProvider(roleRepository,
                roleAssignmentRepository);
        securedAuthenticationProvider.setUserDetailsService(userRepository);
        securedAuthenticationProvider.setPasswordEncoder(getPasswordEncoder());
        auth.authenticationProvider(securedAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        HttpSessionRegistry sessionRegistry = sessionRegistry();
        http.antMatcher("/**")
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .anonymous(anonymousConfigurer -> {
                    anonymousConfigurer.key(coreProperties.getAnonymousAuthenticationTokenKey());
                    anonymousConfigurer.principal(userRepository.getAnonymousUser());
                })
                .csrf().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .sessionManagement().sessionAuthenticationStrategy(sessionControlAuthenticationStrategy(sessionRegistry))
                .maximumSessions(sessionProperties.getMaximumUserSessions()).sessionRegistry(sessionRegistry);
    }

    @Bean
    protected SessionAuthenticationStrategy sessionControlAuthenticationStrategy(SessionRegistry sessionRegistry) {
        return new SessionAuthenticationStrategies(sessionRegistry, sessionProperties.getMaximumUserSessions());
    }

    @Bean(HttpSessionRegistry.NAME)
    protected HttpSessionRegistry sessionRegistry() {
        HttpSessionRegistryImpl httpSessionRegistry = new HttpSessionRegistryImpl();
        httpSessionRegistry.setDelegate(new SessionRegistryImpl());
        return httpSessionRegistry;
    }

    @Bean(name = "sec_AuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean(name = "sec_PasswordEncoder")
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
