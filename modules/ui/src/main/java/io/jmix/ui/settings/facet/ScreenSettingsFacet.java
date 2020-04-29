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
import io.jmix.ui.components.Component;
import io.jmix.ui.components.Facet;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Screen.AfterDetachEvent;
import io.jmix.ui.screen.Screen.AfterShowEvent;
import io.jmix.ui.screen.Screen.BeforeShowEvent;
import io.jmix.ui.settings.ScreenSettings;

import javax.annotation.Nullable;
import java.util.EventObject;
import java.util.Set;
import java.util.function.Consumer;

public interface ScreenSettingsFacet extends Facet {

    /**
     * @return true if facet should apply and save settings for all supported component in the screen. True by default.
     */
    boolean isAuto();

    /**
     * Set to true if facet should apply and save settings for all supported component in the screen. True by default.
     *
     * @param auto whether facet should include all components for saving settings
     */
    void setAuto(boolean auto);

    /**
     * Adds component ids that should be handled when settings are applied and saved.
     *
     * @param ids component ids
     */
    void addComponentIds(String... ids);

    /**
     * Adds components that should be handled when settings are applied and saved settings.
     *
     * @param components components to handle
     */
    void addComponents(Component... components);

    /**
     * @return list of component ids that were added by {@link #addComponents(Component...)} and
     * {@link #addComponentIds(String...)}
     */
    Set<String> getComponentIds();

    /**
     * @return screen settings or null if facet is not attached to the screen
     */
    @Nullable
    ScreenSettings getSettings();

    /**
     * Applies screen settings. By default facet applies setting on {@link AfterShowEvent}.
     *
     * @param settings screen settings
     */
    void applySettings(ScreenSettings settings);

    /**
     * Applies data loading settings. By default facet applies data loading settings on {@link BeforeShowEvent}.
     *
     * @param settings screen settings
     */
    void applyDataLoadingSettings(ScreenSettings settings);

    /**
     * Saves and persist settings. By default facet saves settings on {@link AfterDetachEvent}.
     *
     * @param settings screen settings
     */
    void saveSettings(ScreenSettings settings);

    /**
     * @return apply settings handler or null if not set
     */
    @Nullable
    Consumer<ScreenSettings> getOnApplySettingsHandler();

    /**
     * Sets apply settings handler. It will replace default behavior of facet and will be invoked on
     * {@link AfterShowEvent}.
     * <p>
     * For instance:
     * <pre>{@code
     * @Install(to = "settingsFacet", subject = "onApplySettingsHandler")
     * private void onApplySetting(ScreenSettings settings) {
     *     // default behavior
     *     settingsFacet.applySettings(settings);
     * }
     * }
     * </pre>
     *
     * @param handler apply settings handler
     */
    void setOnApplySettingsHandler(Consumer<ScreenSettings> handler);

    /**
     * @return apply data loading settings handler or null if not set
     */
    @Nullable
    Consumer<ScreenSettings> getOnApplyDataLoadingSettingsHandler();

    /**
     * Sets apply data loading settings handler. It will replace default behavior of facet and will be invoked on
     * {@link BeforeShowEvent}.
     * <p>
     * For instance:
     * <pre>{@code
     * @Install(to = "settingsFacet", subject = "onApplyDataLoadingSettingsHandler")
     * private void onApplyDataLoadingSetting(ScreenSettings settings) {
     *     // default behavior
     *     settingsFacet.applyDataLoadingSettings(settings);
     * }
     * }
     * </pre>
     *
     * @param handler apply settings handler
     */
    void setOnApplyDataLoadingSettingsHandler(Consumer<ScreenSettings> handler);

    /**
     * @return save settings handler or null if not set
     */
    @Nullable
    Consumer<ScreenSettings> getOnSaveSettingsHandler();

    /**
     * Set save settings handler. It will replace default behavior of facet and will be invoked on
     * {@link AfterDetachEvent}.
     * <p>
     * For instance:
     * <pre>{@code
     * @Install(to = "settingsFacet", subject = "onSaveSettingsHandler")
     * private void onSaveSetting(ScreenSettings settings) {
     *     // default behavior
     *     settingsFacet.saveSettings(settings);
     * }
     * }
     * </pre>
     *
     * @param handler save settings handler
     */
    void setOnSaveSettingsHandler(Consumer<ScreenSettings> handler);

    /**
     * Adds before apply settings listener.
     *
     * @param listener listener to add
     * @return registration object for removing an event listener
     */
    Subscription addBeforeApplySettingsListener(Consumer<BeforeApplySettingsEvent> listener);

    /**
     * Adds before apply data loading settings listener.
     *
     * @param listener listener to add
     * @return registration object for removing an event listener
     */
    Subscription addBeforeApplyDataLoadSettingsListener(Consumer<BeforeApplyDataLoadSettingsEvent> listener);

    /**
     * Adds before save settings listener.
     *
     * @param listener listener to add
     * @return registration object for removing an event listener
     */
    Subscription addBeforeSaveSettingsListener(Consumer<BeforeSaveSettingsEvent> listener);

    /**
     * Base class for screen settings facet events.
     */
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
