package io.jmix.core;

import io.jmix.core.annotation.JmixComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan
@JmixComponent(dependsOn = {})
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
}
