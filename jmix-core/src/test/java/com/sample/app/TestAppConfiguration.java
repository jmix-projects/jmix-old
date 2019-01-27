package com.sample.app;


import com.sample.addon1.TestAddon1Configuration;
import io.jmix.core.annotation.JmixComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@JmixComponent(dependsOn = TestAddon1Configuration.class)
public class TestAppConfiguration {

    @Autowired
    Environment environment;

    @Bean
    TestBean testBean() {
        return new TestBean(environment.getProperty("prop1"));
    }
}

