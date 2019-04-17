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

package io.jmix.core;

import io.jmix.core.annotation.JmixComponent;
import io.jmix.core.annotation.JmixProperty;
import io.jmix.core.compatibility.AppContext;
import io.jmix.core.security.JmixCoreSecurityConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;

/**
 * Configuration of the core module.
 *
 * <p>It implements {@link BeanDefinitionRegistryPostProcessor} with {@link PriorityOrdered} in order to be processed
 * before {@code @Conditional} annotations that depend on {@code @JmixProperty} values.
 */
@Configuration
@Import(JmixCoreSecurityConfiguration.class)
@ComponentScan
@JmixComponent(dependsOn = {}, properties = {
        @JmixProperty(name = "jmix.viewsConfig", value = "io/jmix/core/views.xml"),
        @JmixProperty(name = "cuba.confDir", value = "./conf")
})
public class JmixCoreConfiguration {

    @Bean
    public static JmixComponentsProcessor jmixComponentsProcessor() {
        return new JmixComponentsProcessor();
    }

    @Bean
    public JmixComponents jmixComponents(JmixComponentsProcessor processor) {
        return processor.getJmixComponents();
    }

    @EventListener
    @Order(Events.HIGHEST_CORE_PRECEDENCE + 10)
    void onApplicationContextRefreshFirst(ContextRefreshedEvent event) {
        AppContext.Internals.setApplicationContext(event.getApplicationContext());
    }

    @EventListener
    @Order(Events.LOWEST_CORE_PRECEDENCE - 10)
    void onApplicationContextRefreshLast(ContextRefreshedEvent event) {
        AppContext.Internals.startContext();
    }
}