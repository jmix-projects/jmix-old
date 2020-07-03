/*
 * Copyright 2020 Haulmont.
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

package io.jmix.core.impl.session.web;

import io.jmix.core.session.AuthTokenStore;
import io.jmix.core.session.HttpSessionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component(HttpSessionStore.NAME)
public class HttpSessionStore implements AuthTokenStore<HttpSessionWrapper> {

    public static final String NAME = "core_WebSessionStore";

    @Autowired
    protected HttpSessionRegistry webSessionRegistry;

    @Override
    public Stream<HttpSessionWrapper> tokensStream() {
        return webSessionRegistry.getAllPrincipals().stream()
                .flatMap(p -> webSessionRegistry.getAllSessions(p, false).stream())
                .map(HttpSessionWrapper::new);
    }

    @Override
    public Stream<HttpSessionWrapper> tokensStream(Object principal) {
        return webSessionRegistry.getAllSessions(principal, false).stream()
                .map(HttpSessionWrapper::new);
    }

    @Override
    public HttpSessionWrapper get(String id) {
        return new HttpSessionWrapper(webSessionRegistry.getSessionInformation(id));
    }

    @Override
    public void invalidate(HttpSessionWrapper token) {
        webSessionRegistry.expire(token.getId());
    }
}
