/*
 * Copyright (c) 2008-2016 Haulmont.
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

package io.jmix.core.compatibility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.*;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * INTERNAL.
 * Provides access to file-based properties.
 */
@Component("jmix_AppProperties")
public class AppProperties {

    private static final Logger log = LoggerFactory.getLogger(AppProperties.class);

    private Map<String, Object> properties = new ConcurrentHashMap<>();

    @Autowired
    private Environment environment;

    @PostConstruct
    protected void init() {
        if (!(environment instanceof ConfigurableEnvironment)) {
            log.warn("{} is not a ConfigurableEnvironment, cannot register property source");
            return;
        }
        MutablePropertySources sources = ((ConfigurableEnvironment) environment).getPropertySources();
        sources.addFirst(new MapPropertySource("Jmix mutable properties", properties));
    }

    public String[] getPropertyNames() {
        MutablePropertySources propSrcs = ((AbstractEnvironment) environment).getPropertySources();
        return propSrcs.stream()
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .sorted()
                .toArray(String[]::new);
    }

    @Nullable
    public String getProperty(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key passed as parameter");
        }

        return environment.getProperty(key);
    }

    /**
     * Set property value. The new value will be accessible at the runtime through {@link Environment}.
     * @param key       property key
     * @param value     property value. If null, the property will be removed.
     */
    public void setProperty(String key, @Nullable String value) {
        if (value == null)
            properties.remove(key);
        else
            properties.put(key, value);
    }
}