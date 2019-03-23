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

package io.jmix.core.impl;

import io.jmix.core.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL.
 * Factory of {@link DataStore} implementations.
 */
@Component(StoreFactory.NAME)
public class StoreFactory {

    private static final Logger log = LoggerFactory.getLogger(StoreFactory.class);

    public static final String NAME = "cuba_StoreFactory";

    static final String NULL_NAME = "_NULL_";

    private Map<String, DataStore> stores = new HashMap<>();

    @Inject
    private Environment environment;

    @Inject
    private ApplicationContext applicationContext;

    /**
     * Get a {@link DataStore} implementation by name.
     * The implementation bean should be registered in a {@code cuba.storeImpl_<storeName>} app property. If no such
     * property specified, the {@code cuba_RdbmsStore} is returned.
     */
    public DataStore get(String name) {
        DataStore store = stores.get(name);
        if (store != null) {
            return store;
        }

        // todo rework
        String implName;
        if (NULL_NAME.equals(name)) {
            implName = "cuba_NullStore";
        } else {
            implName = environment.getProperty("cuba.storeImpl_" + name);
            if (implName == null) {
                log.debug("No implementation is specified for {} store, using RdbmsStore", name);
                implName = "cuba_RdbmsStore";
            }
        }
        store = (DataStore) applicationContext.getBean(implName, name);
        stores.put(name, store);
        return store;
    }
}
