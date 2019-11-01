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

package io.jmix.autoconfiguration;

import io.jmix.core.JmixCoreConfiguration;
import io.jmix.data.JmixDataConfiguration;
import io.jmix.security.JmixSecurityConfiguration;
import io.jmix.ui.JmixUiConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.sql.DataSource;

@Configuration
@Import({JmixCoreConfiguration.class, JmixDataConfiguration.class, JmixSecurityConfiguration.class, JmixUiConfiguration.class})
public class JmixStandardAutoconfiguration {

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    protected DataSource dataSource() {
        JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiName("java:comp/env/jdbc/JmixDataSource");
        return (DataSource) factoryBean.getObject();
    }
}