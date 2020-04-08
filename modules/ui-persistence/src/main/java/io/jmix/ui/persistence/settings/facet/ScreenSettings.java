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

package io.jmix.ui.persistence.settings.facet;

import io.jmix.core.AppBeans;
import io.jmix.ui.persistence.settings.ComponentSettings;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.settings.SettingsClient;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;

public class ScreenSettings {

    protected SettingsClient settingsClient;

    protected String screenId;

    protected JSONArray root;

    public ScreenSettings(Screen screen) {
        screenId = screen.getId();
    }

    /**
     * @param componentId
     * @param property
     * @param value
     * @return
     */
    public ScreenSettings put(String componentId, String property, String value) {
        JSONObject component = getComponentOrCreate(componentId);
        component.put(property, value);

        return this;
    }

    /**
     * kk
     * @param settings
     * @param <V>
     * @return
     */
    public <V> ScreenSettings put(ComponentSettings<V> settings) {
        String componentId = settings.getComponentId();
        if (componentId == null) {
            throw new IllegalArgumentException("ComponentSettings does not have an component id");
        }

        JSONObject json = settings.toJSONObject();
        json.put("id", settings.getComponentId());

        root.put(json);

        return this;
    }

    /**
     * @param componentId
     * @param property
     * @return
     */
    @Nullable
    public String getString(String componentId, String property) {
        JSONObject component = getComponent(componentId);
        return component == null ? null : component.getString(property);
    }

    /**
     *
     * @param componentId
     * @param property
     * @return
     */
    @Nullable
    public Integer getInteger(String componentId, String property) {
        JSONObject component = getComponent(componentId);
        return component == null ? null : component.getInt(property);
    }

    /**
     *
     * @param componentId
     * @param property
     * @return
     */
    @Nullable
    public Long getLong(String componentId, String property) {
        JSONObject component = getComponent(componentId);
        return component == null ? null : component.getLong(property);
    }

    /**
     *
     * @param componentId
     * @param property
     * @return
     */
    @Nullable
    public Double getDouble(String componentId, String property) {
        JSONObject component = getComponent(componentId);
        return component == null ? null : component.getDouble(property);
    }

    /**
     *
     * @param componentId
     * @param property
     * @return
     */
    @Nullable
    public Boolean getBoolean(String componentId, String property) {
        JSONObject component = getComponent(componentId);
        return component == null ? null : component.getBoolean(property);
    }

    /**
     *
     * @param componentId
     * @return
     */
    @Nullable
    public JSONObject get(String componentId) {
        return getComponent(componentId);
    }

    protected SettingsClient getSettingsClient() {
        if (settingsClient == null) {
            settingsClient = AppBeans.get(SettingsClient.NAME);
        }

        return settingsClient;
    }

    protected void loadSettings() {
        if (root == null) {
            // use cache
            String jsonArray = getSettingsClient().getSetting(screenId);
            if (StringUtils.isBlank(jsonArray)) {
                root = new JSONArray();
            } else {
                root = new JSONArray(jsonArray);
            }
        }
    }

    protected JSONObject getComponentOrCreate(String componentId) {
        JSONObject component = getComponent(componentId);
        if (component == null) {
            component = new JSONObject();
            component.put("id", componentId);
        }

        return component;
    }

    @Nullable
    protected JSONObject getComponent(String componentId) {
        loadSettings();

        for (Object obj : root.toList()) {
            JSONObject component = (JSONObject) obj;
            if (component.getString("id").equals(componentId)) {
                return component;
            }
        }

        return null;
    }
}
