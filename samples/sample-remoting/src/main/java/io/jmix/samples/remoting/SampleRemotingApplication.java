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

package io.jmix.samples.remoting;

import io.jmix.core.JmixCoreConfiguration;
import io.jmix.core.annotation.JmixComponent;
import io.jmix.data.JmixDataConfiguration;
import io.jmix.remoting.JmixRemotingConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

@SpringBootApplication
@Import({JmixCoreConfiguration.class, JmixDataConfiguration.class, JmixRemotingConfiguration.class})
@JmixComponent(dependsOn = JmixRemotingConfiguration.class)
public class SampleRemotingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleRemotingApplication.class, args);
	}

	@Bean
	protected DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:hsqldb:mem:testdb");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}
}
