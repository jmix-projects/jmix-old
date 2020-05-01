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

package io.jmix.ui.components.presentations.actions;

import io.jmix.core.AppBeans;
import io.jmix.ui.presentations.model.Presentation;
import io.jmix.ui.components.Component;
import io.jmix.ui.components.Table;
import io.jmix.ui.presentations.Presentations;
import io.jmix.ui.screen.compatibility.CubaLegacySettings;
import io.jmix.ui.settings.ScreenSettings;
import io.jmix.ui.settings.SettingsHelper;
import io.jmix.ui.settings.component.ComponentSettings;
import io.jmix.ui.settings.component.SettingsWrapperImpl;
import io.jmix.ui.settings.component.worker.ComponentSettingsWorker;
import org.dom4j.Element;

import java.util.Optional;

public class SavePresentationAction extends AbstractPresentationAction {

    public SavePresentationAction(Table table, ComponentSettingsWorker settingsWorker) {
        super(table, "PresentationsPopup.save", settingsWorker);
    }

    @Override
    public void actionPerform(Component component) {
        tableImpl.hidePresentationsPopup();

        Presentations presentations = table.getPresentations();
        Presentation current = presentations.getCurrent();

        if (table.getFrame().getFrameOwner() instanceof CubaLegacySettings) {
            Element e = presentations.getSettings(current);
            table.saveSettings(e);
            presentations.setSettings(current, e);
        } else {
            ScreenSettings screenSettings = AppBeans.getPrototype(ScreenSettings.NAME, table.getFrame().getId());
            String rawSettings = presentations.getRawSettings(current);
            ComponentSettings componentSettings;

            Optional<? extends ComponentSettings> optSettings =
                    screenSettings.toComponentSettings(rawSettings, settingsWorker.getSettingsClass());
            if (optSettings.isPresent()) {
                componentSettings = optSettings.get();
            } else {
                componentSettings = SettingsHelper.createSettings(settingsWorker.getSettingsClass());
                componentSettings.setId(table.getId());
            }

            settingsWorker.saveSettings(table, new SettingsWrapperImpl(componentSettings));
            presentations.setSettings(current, screenSettings.toRawSettings(componentSettings));
        }

        presentations.commit();
    }
}
