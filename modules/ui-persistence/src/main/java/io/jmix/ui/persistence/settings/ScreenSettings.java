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

import io.jmix.core.AppBeans;
import io.jmix.ui.persistence.settings.component.AbstractSettings;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.settings.SettingsClient;
import io.jmix.ui.settings.component.ComponentSettings;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;

public class ScreenSettings {

    protected static final int JSON_INDENT = 4;

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

        root.put(component);

        return this;
    }

    /**
     *
     * @param settings
     * @return
     */
    public ScreenSettings put(ComponentSettings settings) {
        root.put(new JSONObject(settings));

        return this;
    }

    public ScreenSettings put(JSONObject json) {
        root.put(json);

        return this;
    }

    /**
     * @param componentId
     * @param property
     * @return
     */
    @Nullable
    public Optional<String> getString(String componentId, String property) {
        JSONObject component = getComponent(componentId);

        if (component == null)
            return Optional.empty();

        return component.keySet().contains(property) ?
                Optional.of(component.getString(property)) : Optional.empty();
    }

    /**
     *
     * @param componentId
     * @param property
     * @return
     */
    @Nullable
    public Optional<Integer> getInteger(String componentId, String property) {
        JSONObject component = getComponent(componentId);

        if (component == null)
            return Optional.empty();

        return component.keySet().contains(property) ?
                Optional.of(component.getInt(property)) : Optional.empty();
    }

    /**
     *
     * @param componentId
     * @param property
     * @return
     */
    @Nullable
    public Optional<Long> getLong(String componentId, String property) {
        JSONObject component = getComponent(componentId);

        if (component == null)
            return Optional.empty();

        return component.keySet().contains(property) ?
                Optional.of(component.getLong(property)) : Optional.empty();
    }

    /**
     *
     * @param componentId
     * @param property
     * @return
     */
    @Nullable
    public Optional<Double> getDouble(String componentId, String property) {
        JSONObject component = getComponent(componentId);

        if (component == null)
            return Optional.empty();

        return component.keySet().contains(property) ?
                Optional.of(component.getDouble(property)) : Optional.empty();
    }

    /**
     *
     * @param componentId
     * @param property
     * @return
     */
    @Nullable
    public Optional<Boolean> getBoolean(String componentId, String property) {
        JSONObject component = getComponent(componentId);

        if (component == null)
            return Optional.empty();

        return component.keySet().contains(property) ?
                Optional.of(component.getBoolean(property)) : Optional.empty();
    }

    /**
     *
     * @param componentId
     * @return
     */
    public Optional<JSONObject> getSettingsRaw(String componentId) {
        return Optional.ofNullable(getComponent(componentId));
    }

    /**
     *
     * @param componentId
     * @param settingsClass
     * @return
     */
    public Optional<ComponentSettings> getSettings(String componentId, Class<? extends AbstractSettings> settingsClass) {
        SettingsRegister register = AppBeans.get(SettingsRegister.NAME);

        JSONObject json = getComponent(componentId);
        if (json == null) {
            return Optional.empty();
        }

        return Optional.of(register.createSettings(settingsClass, json));
    }

    public <T extends AbstractSettings> Optional<T> getTypeSettings(String componentId, Class<T> settingsClass) {
        ComponentSettings settings = getSettings(componentId, settingsClass).orElse(null);
        return settings == null ? Optional.empty() : Optional.of(settingsClass.cast(settings));
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

        Iterator<Object> iterator = root.iterator();
        while (iterator.hasNext()) {
            JSONObject component = (JSONObject) iterator.next();

            boolean keyExist = component.keySet().contains("id");
            if (keyExist && component.getString("id").equals(componentId)) {
                return component;
            }
        }

        return null;
    }

    protected void commit() {
        getSettingsClient().setSetting(screenId, root.toString(JSON_INDENT));
    }
}
