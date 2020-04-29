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
import io.jmix.core.DevelopmentException;
import io.jmix.ui.presentations.model.Presentation;
import io.jmix.ui.AppUI;
import io.jmix.ui.components.HasPresentations;
import io.jmix.ui.components.Table;
import io.jmix.ui.components.presentations.PresentationEditor;
import io.jmix.ui.settings.ScreenSettings;
import io.jmix.ui.settings.component.worker.ComponentSettingsWorker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class AbstractEditPresentationAction extends AbstractPresentationAction {

    protected Class<? extends PresentationEditor> editorClass;

    public AbstractEditPresentationAction(Table table, String id, ComponentSettingsWorker settingsWorker) {
        super(table, id, settingsWorker);
    }

    protected void openEditor(Presentation presentation) {
        PresentationEditor window = settingsWorker == null ?
                createEditor(presentation) : createEditor(presentation, settingsWorker);
        AppUI.getCurrent().addWindow(window);
        window.center();
    }

    protected PresentationEditor createEditor(Presentation presentation, ComponentSettingsWorker settingsWorker) {
        Class<? extends PresentationEditor> windowClass = getPresentationEditorClass();
        try {
            Constructor<? extends PresentationEditor> windowConstructor = windowClass.getConstructor(
                    Presentation.class, HasPresentations.class, ComponentSettingsWorker.class, ScreenSettings.class);

            ScreenSettings screenSettings = AppBeans.getPrototype(ScreenSettings.NAME, table.getFrame().getId());

            return windowConstructor.newInstance(presentation, table, settingsWorker, screenSettings);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DevelopmentException("Invalid presentation's screen");
        }
    }

    protected PresentationEditor createEditor(Presentation presentation) {
        Class<? extends PresentationEditor> windowClass = getPresentationEditorClass();
        PresentationEditor window;
        try {
            Constructor<? extends PresentationEditor> windowConstructor = windowClass
                    .getConstructor(Presentation.class, HasPresentations.class);
            window = windowConstructor.newInstance(presentation, table);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DevelopmentException("Invalid presentation's screen");
        }
        return window;
    }

    protected Class<? extends PresentationEditor> getPresentationEditorClass() {
        return editorClass == null ? PresentationEditor.class : editorClass;
    }

    public void setEditorClass(Class<? extends PresentationEditor> editorClass) {
        this.editorClass = editorClass;
    }
}
