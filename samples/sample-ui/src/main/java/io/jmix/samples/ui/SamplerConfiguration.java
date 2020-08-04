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

package io.jmix.samples.ui;

import io.jmix.core.Messages;
import io.jmix.core.annotation.JmixModule;
import io.jmix.samples.ui.bean.SamplerLinkHandler;
import io.jmix.samples.ui.bean.SamplerMessagesImpl;
import io.jmix.ui.App;
import io.jmix.ui.UiConfiguration;
import io.jmix.ui.sys.LinkHandler;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Configuration
@JmixModule(dependsOn = UiConfiguration.class)
public class SamplerConfiguration {

    @Bean(name = LinkHandler.NAME)
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public LinkHandler linkHandler(App app, String action, Map<String, String> requestParams) {
        return new SamplerLinkHandler(app, action, requestParams);
    }

    @Bean(name = Messages.NAME)
    public Messages messages() {
        return new SamplerMessagesImpl();
    }
}
