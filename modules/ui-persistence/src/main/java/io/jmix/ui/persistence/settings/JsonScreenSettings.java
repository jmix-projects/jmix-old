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

import com.google.gson.*;
import io.jmix.ui.settings.ScreenSettings;
import io.jmix.ui.settings.SettingsClient;
import io.jmix.ui.settings.component.ComponentSettings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Optional;

/**
 * Base class for screen settings that use JSON structure for stored settings.
 */
@Component(ScreenSettings.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class JsonScreenSettings extends AbstractScreenSettings {

    @Inject
    protected SettingsClient settingsClient;

    protected JsonArray root;

    protected Gson gson;

    public JsonScreenSettings(String screenId) {
        super(screenId);

        initGson();
    }

    @Override
    public JsonScreenSettings put(String componentId, String property, String value) {
        JsonObject component = getComponentOrCreate(componentId);

        component.addProperty(property, value);

        put(component, componentId);

        return this;
    }

    @Override
    public ScreenSettings put(String componentId, String property, Integer value) {
        JsonObject component = getComponentOrCreate(componentId);

        component.addProperty(property, value);

        put(component, componentId);

        return this;
    }

    @Override
    public ScreenSettings put(String componentId, String property, Long value) {
        JsonObject component = getComponentOrCreate(componentId);

        component.addProperty(property, value);

        put(component, componentId);

        return this;
    }

    @Override
    public ScreenSettings put(String componentId, String property, Double value) {
        JsonObject component = getComponentOrCreate(componentId);

        component.addProperty(property, value);

        put(component, componentId);

        return this;
    }

    @Override
    public ScreenSettings put(String componentId, String property, Boolean value) {
        JsonObject component = getComponentOrCreate(componentId);

        component.addProperty(property, value);

        put(component, componentId);

        return this;
    }

    @Override
    public JsonScreenSettings put(ComponentSettings settings) {
        put(gson.toJsonTree(settings), settings.getId());

        return this;
    }

    /**
     * @param json json object that represents component settings
     * @return current instance of {@link ScreenSettings}
     */
    public JsonScreenSettings put(JsonObject json) {
        if (!json.keySet().contains("id")) {
            throw new IllegalArgumentException("Cannot put settings, json must have an id property");
        }

        String componentId = json.getAsJsonPrimitive("id").getAsString();
        put(json, componentId);

        return this;
    }

    @Override
    public Optional<String> getString(String componentId, String property) {
        JsonObject component = getComponent(componentId);

        if (component == null || isValueNull(component, property))
            return Optional.empty();

        return component.keySet().contains(property) ?
                Optional.of(component.getAsJsonPrimitive(property).getAsString()) : Optional.empty();
    }

    @Override
    public Optional<Integer> getInteger(String componentId, String property) {
        JsonObject component = getComponent(componentId);

        if (component == null || isValueNull(component, property))
            return Optional.empty();

        return component.keySet().contains(property) ?
                Optional.of(component.getAsJsonPrimitive(property).getAsInt()) : Optional.empty();
    }

    @Override
    public Optional<Long> getLong(String componentId, String property) {
        JsonObject component = getComponent(componentId);

        if (component == null || isValueNull(component, property))
            return Optional.empty();

        return component.keySet().contains(property) ?
                Optional.of(component.getAsJsonPrimitive(property).getAsLong()) : Optional.empty();
    }

    @Override
    public Optional<Double> getDouble(String componentId, String property) {
        JsonObject component = getComponent(componentId);

        if (component == null || isValueNull(component, property))
            return Optional.empty();

        return component.keySet().contains(property) ?
                Optional.of(component.getAsJsonPrimitive(property).getAsDouble()) : Optional.empty();
    }

    @Override
    public Optional<Boolean> getBoolean(String componentId, String property) {
        JsonObject component = getComponent(componentId);

        if (component == null || isValueNull(component, property))
            return Optional.empty();

        return component.keySet().contains(property) ?
                Optional.of(component.getAsJsonPrimitive(property).getAsBoolean()) : Optional.empty();
    }

    @Override
    public <T extends ComponentSettings> Optional<T> getSettings(String componentId, Class<T> settingsClass) {
        JsonObject json = getComponent(componentId);
        if (json == null)
            return Optional.empty();

        return Optional.ofNullable(gson.fromJson(json, settingsClass));
    }

    @Override
    public <T extends ComponentSettings> T getSettingsOrCreate(String componentId, Class<T> settingsClass) {
        return getSettings(componentId, settingsClass).orElseGet(() -> {
            JsonObject json = new JsonObject();
            json.addProperty("id", componentId);
            return settingsClass.cast(gson.fromJson(json, settingsClass));
        });
    }

    @Override
    public ScreenSettings remove(String componentId) {
        JsonObject component = getComponent(componentId);

        if (component != null) {
            root.remove(component);

            setModified(true);
        }

        return this;
    }

    @Override
    public ScreenSettings remove(String componentId, String property) {
        JsonObject component = getComponent(componentId);

        if (component != null) {
            component.remove(property);

            setModified(true);
        }

        return this;
    }

    /**
     * @param componentId component id
     * @return json object that represents component settings
     */
    public Optional<JsonObject> getSettingsRaw(String componentId) {
        return Optional.ofNullable(getComponent(componentId));
    }

    protected void put(JsonElement json, String componentId) {
        loadSettings();

        remove(componentId);

        root.add(json);

        setModified(true);
    }

    protected void initGson() {
        gson = new GsonBuilder()
                .create();
    }

    protected void loadSettings() {
        if (root == null) {
            String jsonArray = settingsClient.getSetting(screenId);

            root = StringUtils.isNotBlank(jsonArray) ?
                    gson.fromJson(jsonArray, JsonArray.class) : new JsonArray();
        }
    }

    protected void commit() {
        if (isModified() && root != null) {
            settingsClient.setSetting(screenId, gson.toJson(root));

            setModified(false);
        }
    }

    protected JsonObject getComponentOrCreate(String componentId) {
        JsonObject component = getComponent(componentId);
        if (component == null) {
            component = new JsonObject();
            component.addProperty("id", componentId);
        }

        return component;
    }

    @Nullable
    protected JsonObject getComponent(String componentId) {
        loadSettings();

        for (JsonElement jsonElement : root) {
            JsonObject component = (JsonObject) jsonElement;

            boolean keyExist = component.keySet().contains("id");
            if (keyExist) {
                String id = component.getAsJsonPrimitive("id").getAsString();
                if (id.equals(componentId)) {
                    return component;
                }
            }
        }

        return null;
    }

    protected boolean isValueNull(JsonObject json, String property) {
        if (json.keySet().contains(property))
            return json.get(property).isJsonNull();

        return true;
    }
}
