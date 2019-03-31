package com.sample.app;


import com.sample.addon1.TestAddon1Configuration;
import io.jmix.core.annotation.JmixComponent;
import io.jmix.core.annotation.JmixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@JmixComponent(dependsOn = TestAddon1Configuration.class, properties = {
        @JmixProperty(name = "jmix.viewsConfig", value = "com/sample/app/views.xml", append = true),
        @JmixProperty(name = "prop1", value = "app_prop1", append = true),
        @JmixProperty(name = "prop2", value = "app_prop2"),
        @JmixProperty(name = "prop3", value = "app_prop3")
})
@PropertySource("classpath:/com/sample/app/app.properties")
public class TestAppConfiguration {

    @Autowired
    Environment environment;

    @Bean
    TestBean testBean() {
        return new TestBean(environment.getProperty("prop1"));
    }
}
