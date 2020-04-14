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

package io.jmix.ui.settings.facet;

import io.jmix.core.commons.events.Subscription;
import io.jmix.ui.components.Facet;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.settings.ScreenSettings;

import java.util.EventObject;
import java.util.Set;
import java.util.function.Consumer;

public interface ScreenSettingsFacet extends Facet {

    boolean isIncludeAll();

    void setIncludeAll(boolean includeAll);

    void addExcludeComponentIds(String... ids);

    Set<String> getExcludeIds();

    void addIncludeComponentIds(String... ids);

    Set<String> getIncludeIds();

    ScreenSettings getSettings();

    void applySettings(ScreenSettings settings);

    void applyDataLoadSettings(ScreenSettings settings);

    void saveSettings(ScreenSettings settings);

    Consumer<ScreenSettings> getApplySettingsProvider();
    void setApplySettingsProvider(Consumer<ScreenSettings> provider);

    Consumer<ScreenSettings> getApplyDataLoadSettingsProvider();
    void setApplyDataLoadSettingsProvider(Consumer<ScreenSettings> provider);

    Consumer<ScreenSettings> getSaveSettingsProvider();
    void setSaveSettingsProvider(Consumer<ScreenSettings> provider);

    // todo remove?
    Subscription addBeforeApplySettingsListener(Consumer<BeforeApplySettingsEvent> listener);
    Subscription addBeforeApplyDataLoadSettingsListener(Consumer<BeforeApplyDataLoadSettingsEvent> listener);
    Subscription addBeforeSaveSettingsListener(Consumer<BeforeSaveSettingsEvent> listener);

    abstract class AbstractSettingsEvent extends EventObject {

        protected ScreenSettings settings;

        public AbstractSettingsEvent(Screen source, ScreenSettings settings) {
            super(source);

            this.settings = settings;
        }

        public ScreenSettings getSettings() {
            return settings;
        }

        @Override
        public Screen getSource() {
            return (Screen) super.getSource();
        }
    }

    class BeforeApplySettingsEvent extends AbstractSettingsEvent {

        public BeforeApplySettingsEvent(Screen source, ScreenSettings settings) {
            super(source, settings);
        }
    }

    class BeforeApplyDataLoadSettingsEvent extends AbstractSettingsEvent {

        public BeforeApplyDataLoadSettingsEvent(Screen source, ScreenSettings settings) {
            super(source, settings);
        }
    }

    class BeforeSaveSettingsEvent extends AbstractSettingsEvent {

        public BeforeSaveSettingsEvent(Screen source, ScreenSettings settings) {
            super(source, settings);
        }
    }
}
