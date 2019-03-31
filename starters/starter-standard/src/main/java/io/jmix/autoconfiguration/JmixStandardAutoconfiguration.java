package io.jmix.autoconfiguration;

import io.jmix.core.JmixCoreConfiguration;
import io.jmix.security.JmixSecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({JmixCoreConfiguration.class, JmixSecurityConfiguration.class})
public class JmixStandardAutoconfiguration {
}
