package io.jmix.autoconfiguration;

import io.jmix.core.JmixCoreConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JmixCoreConfiguration.class)
public class JmixStandardAutoconfiguration {
}
