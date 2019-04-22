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

package io.jmix.remoting.gateway;

import io.jmix.core.impl.ConfigStorage;
import io.jmix.remoting.annotation.Remote;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

@Component(ServerConfigStorage.NAME)
@Profile("remoting")
@Remote
public class ServerConfigStorageImpl implements ServerConfigStorage {

    @Inject
    protected ConfigStorage configStorage;

    @Override
    public Map<String, String> getDbProperties() {
        return configStorage.getDbProperties();
    }

    @Nullable
    @Override
    public String getDbProperty(String name) {
        return configStorage.getDbProperty(name);
    }

    @Override
    public void setDbProperty(String name, String value) {
        configStorage.setDbProperty(name, value);
    }

    @Override
    public void clearCache() {
        configStorage.clearCache();
    }
}

