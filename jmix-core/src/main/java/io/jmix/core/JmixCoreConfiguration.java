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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

@Configuration
@ComponentScan
@JmixComponent(dependsOn = {}, properties = {
        @JmixProperty(name = "jmix.metadataConfig", value = "io/jmix/core/metadata.xml"),
        @JmixProperty(name = "jmix.viewsConfig", value = "io/jmix/core/views.xml"),
        @JmixProperty(name = "cuba.confDir", value = "./conf")
})
public class JmixCoreConfiguration {
    @Bean
    static JmixComponents jmixComponents() {
        return new JmixComponents();
    }

    @EventListener
    @Order(Events.HIGHEST_CORE_PRECEDENCE + 10)
    void onApplicationContextRefresh(ContextRefreshedEvent event) {
        AppContext.Internals.setApplicationContext(event.getApplicationContext());
    }
}