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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan
@JmixComponent(dependsOn = {}, properties = {
        @JmixProperty(name = "jmix.metadataConfig", value = "io/jmix/core/metadata.xml")
})
public class JmixCoreConfiguration {

    protected Environment environment;

    @Autowired
    void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    static JmixComponents jmixComponents() {
        return new JmixComponents();
    }

    @Bean
    public Greeter greeter() {
        System.out.println("Creating Greeter");
        System.out.println("Environment: " + environment);
        return new Greeter();
    }

    @EventListener
    void onApplicationContextRefresh(ContextRefreshedEvent event) {
        AppContext.Internals.setApplicationContext(event.getApplicationContext());
    }
}
