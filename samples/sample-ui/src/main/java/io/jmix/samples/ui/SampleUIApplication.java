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

package io.jmix.samples.ui;

import io.jmix.core.impl.scanning.AnnotationScanMetadataReaderFactory;
import io.jmix.ui.sys.UiControllersConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@SpringBootApplication
public class SampleUIApplication implements CommandLineRunner {

	@Autowired
	private Greeter greeter;

	public static void main(String[] args) {
		SpringApplication.run(SampleUIApplication.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println(greeter.sayHello("there"));
	}

	@Bean("my_UiControllers")
	public UiControllersConfiguration screens(ApplicationContext applicationContext,
											  AnnotationScanMetadataReaderFactory metadataReaderFactory) {
		UiControllersConfiguration uiControllers
				= new UiControllersConfiguration(applicationContext, metadataReaderFactory);
		uiControllers.setBasePackages(Collections.singletonList("io.jmix.samples.ui"));
		return uiControllers;
	}
}