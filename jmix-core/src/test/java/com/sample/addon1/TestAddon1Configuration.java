package com.sample.addon1;

import io.jmix.core.JmixCoreConfiguration;
import io.jmix.core.annotation.JmixComponent;
import io.jmix.core.annotation.JmixProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@JmixComponent(dependsOn = JmixCoreConfiguration.class, properties = {
        @JmixProperty(name = "jmix.metadataConfig", value = "com/sample/addon1/metadata.xml", append = true),
        @JmixProperty(name = "jmix.viewsConfig", value = "com/sample/addon1/views.xml", append = true),
        @JmixProperty(name = "prop1", value = "addon1_prop1", append = true),
        @JmixProperty(name = "prop2", value = "addon1_prop2", append = true)
})
public class TestAddon1Configuration {
}
