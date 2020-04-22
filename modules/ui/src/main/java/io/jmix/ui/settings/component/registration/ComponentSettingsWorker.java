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

package io.jmix.ui.settings.component.registration;

import io.jmix.ui.components.Component;
import io.jmix.ui.components.impl.WebTable;
import io.jmix.ui.settings.component.ComponentSettings;
import io.jmix.ui.settings.component.SettingsWrapper;
import io.jmix.ui.settings.component.TableSettings;

/**
 * Base interface for component settings registration. As an example see {@link TableSettingsWorker}.
 */
public interface ComponentSettingsWorker {

    /**
     * @return component class, e.g. {@link WebTable}
     */
    Class<? extends Component> getComponentClass();

    /**
     * @return component settings class, e.g. {@link TableSettings}
     */
    Class<? extends ComponentSettings> getSettingsClass();

    void applySettings(Component component, SettingsWrapper wrapper);

    void applyDataLoadingSettings(Component component, SettingsWrapper wrapper);

    boolean saveSettings(Component component, SettingsWrapper wrapper);

    /**
     *
     * @param component
     * @return
     */
    ComponentSettings getSettings(Component component);
}
