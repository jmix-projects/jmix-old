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

package io.jmix.remoting.impl;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;

@Component(ClientTokenSupport.NAME)
public class ClientTokenSupport {

    public static final String NAME = "jmix_ClientTokenSupport";

    @Inject
    protected Environment environment;

    @Nullable
    public String current() {
        return environment.getProperty("jmix.clientToken");
    }

    public boolean matches(String clientToken) {
        String clientTokenProp = environment.getProperty("jmix.clientToken");
        if (clientTokenProp == null) {
            return false;
        }
        return clientTokenProp.equals(clientToken);
    }
}
