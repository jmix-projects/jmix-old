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

package io.jmix.ui.settings;

import com.vaadin.server.VaadinSession;
import io.jmix.core.ClientType;
import io.jmix.ui.executors.IllegalConcurrentAccessException;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * User settings provider for web application. Caches settings in HTTP session.
 */
@Component(WebSettingsClient.NAME)
public class WebSettingsClient {

    public static final String NAME = "cuba_SettingsClient";

    @Inject
    protected UserSettingService userSettingService;

    public String getSetting(String name) {
        Map<String, Optional<String>> settings = getCache();
        Optional<String> cached = settings.get(name);
        if (cached != null) {
            return cached.orElse(null);
        }

        String setting = userSettingService.loadSetting(ClientType.WEB, name);
        settings.put(name, Optional.ofNullable(setting));

        return setting;
    }

    public void setSetting(String name, @Nullable String value) {
        getCache().put(name, Optional.ofNullable(value));
        userSettingService.saveSetting(ClientType.WEB, name, value);
    }

    public void deleteSettings(String name) {
        getCache().put(name, Optional.empty());
        userSettingService.deleteSettings(ClientType.WEB, name);
    }

    public void clearCache() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null || !session.hasLock()) {
            throw new IllegalConcurrentAccessException("Illegal access to settings client from background thread");
        }

        session.setAttribute(NAME, null);
    }

    protected Map<String, Optional<String>> getCache() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null || !session.hasLock()) {
            throw new IllegalConcurrentAccessException("Illegal access to settings client from background thread");
        }

        @SuppressWarnings("unchecked")
        Map<String, Optional<String>> settings = (Map<String, Optional<String>>) session.getAttribute(NAME);
        if (settings == null) {
            settings = new HashMap<>();
            session.setAttribute(NAME, settings);
        }
        return settings;
    }
}
