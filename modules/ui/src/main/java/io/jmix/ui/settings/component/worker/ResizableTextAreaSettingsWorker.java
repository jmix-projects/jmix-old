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

package io.jmix.ui.settings.component.worker;

import io.jmix.ui.components.Component;
import io.jmix.ui.components.ResizableTextArea;
import io.jmix.ui.components.ResizableTextArea.ResizeDirection;
import io.jmix.ui.components.impl.WebResizableTextArea;
import io.jmix.ui.settings.component.ComponentSettings;
import io.jmix.ui.settings.component.ResizableTextAreaSettings;
import io.jmix.ui.settings.component.SettingsWrapper;
import io.jmix.ui.widgets.CubaResizableTextAreaWrapper;

@SuppressWarnings("rawtypes")
@org.springframework.stereotype.Component(ResizableTextAreaSettingsWorker.NAME)
public class ResizableTextAreaSettingsWorker implements ComponentSettingsWorker {

    public static final String NAME = "jmix_ResizableTextAreaSettingsWorker";

    @Override
    public Class<? extends Component> getComponentClass() {
        return WebResizableTextArea.class;
    }

    @Override
    public Class<? extends ComponentSettings> getSettingsClass() {
        return ResizableTextAreaSettings.class;
    }

    @Override
    public void applySettings(Component component, SettingsWrapper wrapper) {
        ResizableTextArea textArea = (ResizableTextArea) component;
        ResizableTextAreaSettings settings = wrapper.getSettings();

        if (textArea.getResizableDirection() == ResizeDirection.NONE) {
            return;
        }

        if (settings.getHeight() != null
                && settings.getWidth() != null) {
            textArea.setWidth(settings.getWidth());
            textArea.setHeight(settings.getHeight());
        }
    }

    @Override
    public void applyDataLoadingSettings(Component component, SettingsWrapper wrapper) {
        // does not have data loading settings
    }

    @Override
    public boolean saveSettings(Component component, SettingsWrapper wrapper) {
        ResizableTextArea textArea = (ResizableTextArea) component;
        ResizableTextAreaSettings settings = wrapper.getSettings();

        if (textArea.getResizableDirection() == ResizeDirection.NONE) {
            return false;
        }


        if (isSettingsChanged(textArea, settings)) {
            settings.setWidth(getWidth(textArea));
            settings.setHeight(getHeight(textArea));

            return true;
        }

        return false;
    }

    @Override
    public ComponentSettings getSettings(Component component) {
        ResizableTextArea textArea = (ResizableTextArea) component;
        ResizableTextAreaSettings settings = createSettings();

        settings.setWidth(getWidth(textArea));
        settings.setHeight(getHeight(textArea));

        return settings;
    }

    protected boolean isSettingsChanged(ResizableTextArea textArea, ResizableTextAreaSettings settings) {
        if (settings.getHeight() == null || settings.getWidth() == null) {
            return true;
        }

        return !getWidth(textArea).equals(settings.getWidth())
                || !getHeight(textArea).equals(settings.getHeight());
    }

    protected String getWidth(ResizableTextArea textArea) {
        CubaResizableTextAreaWrapper textAreaWrapper =
                (CubaResizableTextAreaWrapper) ((Component.Wrapper) textArea).getComposition();

        return textArea.getWidth() + textAreaWrapper.getWidthUnits().toString();
    }

    protected String getHeight(ResizableTextArea textArea) {
        CubaResizableTextAreaWrapper textAreaWrapper =
                (CubaResizableTextAreaWrapper) ((Component.Wrapper) textArea).getComposition();

        return textArea.getHeight() + textAreaWrapper.getHeightUnits().toString();
    }

    protected ResizableTextAreaSettings createSettings() {
        return new ResizableTextAreaSettings();
    }
}
