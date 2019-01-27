package com.sample.addon1;

import io.jmix.core.JmixCoreConfiguration;
import io.jmix.core.annotation.JmixComponent;
import org.springframework.context.annotation.Configuration;

@Configuration
@JmixComponent(dependsOn = JmixCoreConfiguration.class)
public class TestAddon1Configuration {
}
