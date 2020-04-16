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

package io.jmix.ui.components;

import io.jmix.ui.settings.UserSettingService;
import io.jmix.ui.settings.component.SettingsWrapper;
import org.dom4j.Element;

/**
 * Object supporting save/restore of user settings.
 *
 * @see UserSettingService
 */
public interface HasSettings {

    /**
     * Applies user settings for object.
     *
     * @param element settings element
     * @deprecated Use {@link #applySettings(SettingsWrapper)} instead.
     */
    @Deprecated
    void applySettings(Element element);

    /**
     * Saves object settings to the element.
     *
     * @param element settings element
     * @return true if settings were modified
     * @deprecated Use {@link #saveSettings(SettingsWrapper)} instead.
     */
    @Deprecated
    boolean saveSettings(Element element);

    /**
     * Applies user settings for object.
     *
     * @param settings wrapper that contains settings object
     */
    void applySettings(SettingsWrapper settings);

    /**
     * Saves object settings to the wrapped settings.
     *
     * @param settings wrapper that contains settings object
     * @return true if settings were modified
     */
    boolean saveSettings(SettingsWrapper settings);

    /**
     * @return true if object allows to save and apply settings
     */
    boolean isSettingsEnabled();

    /**
     * Set to true if object should allow to save and apply settings
     *
     * @param settingsEnabled whether settings should be enabled for the object
     */
    void setSettingsEnabled(boolean settingsEnabled);
}