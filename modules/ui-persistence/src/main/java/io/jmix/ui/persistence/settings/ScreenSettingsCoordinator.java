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

import io.jmix.ui.components.Component;
import io.jmix.ui.components.HasSettings;
import io.jmix.ui.settings.component.SettingsWrapperImpl;
import io.jmix.ui.settings.ScreenSettings;
import io.jmix.ui.settings.component.ComponentSettings;

import javax.inject.Inject;
import java.util.Collection;

@org.springframework.stereotype.Component(ScreenSettingsCoordinator.NAME)
public class ScreenSettingsCoordinator {

    public static final String NAME = "jmix_ui_persistence_ScreenSettingsTools";

    @Inject
    protected SettingsRegister settingsRegister;

    /**
     * @param components
     * @param screenSettings
     */
    public void applySettings(Collection<Component> components, ScreenSettings screenSettings) {
        for (Component component : components) {
            if (!(component instanceof HasSettings)) {
                continue;
            }

            Class<? extends ComponentSettings> settingsClass = settingsRegister.getSettingsClass(component.getClass());

            ComponentSettings settings = screenSettings.getSettingsOrCreate(component.getId(), settingsClass);

            ((HasSettings) component).applySettings(new SettingsWrapperImpl(settings));
        }
    }

    /**
     * @param components
     * @param screenSettings
     */
    public void saveSettings(Collection<Component> components, ScreenSettings screenSettings) {
        boolean isModified = false;

        for (Component component : components) {
            if (!(component instanceof HasSettings)) {
                continue;
            }

            Class<? extends ComponentSettings> settingsClass = settingsRegister.getSettingsClass(component.getClass());

            ComponentSettings settings = screenSettings.getSettingsOrCreate(component.getId(), settingsClass);

            boolean settingsChanged = ((HasSettings) component).saveSettings(new SettingsWrapperImpl(settings));
            if (settingsChanged) {
                isModified = true;

                screenSettings.put(settings);
            }
        }

        if (isModified || screenSettings.isForceModified()) {
            screenSettings.commit();
        }
    }
}
