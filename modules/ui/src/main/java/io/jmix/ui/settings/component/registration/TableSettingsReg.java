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
import io.jmix.ui.components.Table;
import io.jmix.ui.components.impl.WebTable;
import io.jmix.ui.settings.component.ComponentSettings;
import io.jmix.ui.settings.component.TableSettings;

@org.springframework.stereotype.Component(TableSettingsReg.NAME)
public class TableSettingsReg implements SettingsRegistration {

    public static final String NAME = "jmix_ui_persistence_TableSettings";

    @Override
    public String getComponentName() {
        return Table.NAME;
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return WebTable.class;
    }

    @Override
    public Class<? extends ComponentSettings> getSettingsClass() {
        return TableSettings.class;
    }
}
