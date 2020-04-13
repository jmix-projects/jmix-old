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

import io.jmix.ui.settings.component.ComponentSettings;

import java.util.Optional;

public interface ScreenSettings {

    String NAME = "jmix_ui_ScreenSettings";

    void setForceModified(boolean forceModified);

    boolean isForceModified();

    ScreenSettings put(String componentId, String property, String value);

    ScreenSettings put(String componentId, String property, Integer value);

    ScreenSettings put(String componentId, String property, Long value);

    ScreenSettings put(String componentId, String property, Double value);

    ScreenSettings put(String componentId, String property, Boolean value);

    ScreenSettings remove(String componentId);

    ScreenSettings remove(String componentId, String property);

    ScreenSettings put(ComponentSettings settings);

    Optional<String> getString(String componentId, String property);

    Optional<Integer> getInteger(String componentId, String property);

    Optional<Long> getLong(String componentId, String property);

    Optional<Double> getDouble(String componentId, String property);

    Optional<Boolean> getBoolean(String componentId, String property);

    <T extends ComponentSettings> Optional<T> getSettings(String componentId, Class<T> settingsClass);

    <T extends ComponentSettings> T getSettingsOrCreate(String componentId, Class<T> settingsClass);

    /**
     * INTERNAL. The lifecycle of screen settings is controlled by the framework.
     */
    void commit();
}
