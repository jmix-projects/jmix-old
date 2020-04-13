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

package io.jmix.ui.persistence.settings;

import com.google.gson.JsonArray;
import io.jmix.ui.settings.ScreenSettings;
import io.jmix.ui.settings.SettingsClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component(ScreenSettings.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PersistentScreenSettings extends AbstractScreenSettings {

    @Inject
    protected SettingsClient settingsClient;

    public PersistentScreenSettings(String screenId) {
        super(screenId);
    }

    @Override
    public void commit() {
        settingsClient.setSetting(screenId, gson.toJson(root));
    }

    @Override
    protected void loadSettings() {
        if (root == null) {
            String jsonArray = settingsClient.getSetting(screenId);

            root = StringUtils.isNotBlank(jsonArray) ?
                    gson.fromJson(jsonArray, JsonArray.class) : new JsonArray();
        }
    }
}
