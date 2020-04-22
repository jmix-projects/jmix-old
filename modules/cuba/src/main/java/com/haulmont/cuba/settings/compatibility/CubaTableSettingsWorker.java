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

package com.haulmont.cuba.settings.compatibility;

import com.haulmont.cuba.web.gui.components.WebTable;
import io.jmix.ui.components.Component;
import io.jmix.ui.settings.component.ComponentSettings;
import io.jmix.ui.settings.component.SettingsWrapper;
import io.jmix.ui.settings.component.TableSettings;
import io.jmix.ui.settings.component.registration.ComponentSettingsWorker;

@org.springframework.stereotype.Component(CubaTableSettingsWorker.NAME)
public class CubaTableSettingsWorker implements ComponentSettingsWorker {

    public static final String NAME = "jmix_CubaTableSettingsReg";

    @Override
    public Class<? extends Component> getComponentClass() {
        return WebTable.class;
    }

    @Override
    public Class<? extends ComponentSettings> getSettingsClass() {
        return TableSettings.class;
    }

    @Override
    public void applySettings(Component component, SettingsWrapper wrapper) {

    }

    @Override
    public void applyDataLoadingSettings(Component component, SettingsWrapper wrapper) {

    }

    @Override
    public boolean saveSettings(Component component, SettingsWrapper wrapper) {
        return false;
    }

    @Override
    public ComponentSettings getSettings(Component component) {
        return null;
    }
}
