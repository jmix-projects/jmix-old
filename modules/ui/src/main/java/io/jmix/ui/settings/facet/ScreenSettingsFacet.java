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

import io.jmix.ui.components.Accordion;
import io.jmix.ui.components.Component;
import io.jmix.ui.components.Facet;
import io.jmix.ui.components.Window;
import io.jmix.ui.components.impl.WebTabSheet;
import io.jmix.ui.screen.Screen.AfterDetachEvent;
import io.jmix.ui.screen.Screen.AfterShowEvent;
import io.jmix.ui.screen.Screen.BeforeShowEvent;
import io.jmix.ui.settings.ScreenSettings;

import javax.annotation.Nullable;
import java.util.Collection;
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
     * Adds component ids that should be handled when {@link #isAuto()} returns false.
     *
     * @param ids component ids
     */
    void addComponentIds(String... ids);

    /**
     * @return list of component ids that should be handled when {@link #isAuto()} returns false.
     */
    Set<String> getComponentIds();

    /**
     * Returned collection depends on {@link #isAuto()} property. If it returns true collection will be filled by {@link Window},
     * otherwise collection will be filled by components were added by {@link #addComponentIds(String...)}.
     *
     * @return components collection that is used for applying and saving settings.
     */
    Collection<Component> getComponents();

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
    Consumer<SettingsSet> getOnApplySettingsHandler();

    /**
     * Sets apply settings handler. It will replace default behavior of facet and will be invoked on
     * {@link AfterShowEvent}.
     * <p>
     * For instance:
     * <pre>{@code
     * @Install(to = "settingsFacet", subject = "onApplySettingsHandler")
     * private void onApplySetting(SettingsSet settings) {
     *     // default behavior
     *     settingsFacet.applySettings(settings);
     * }
     * }
     * </pre>
     *
     * @param handler apply settings handler
     */
    void setOnApplySettingsHandler(Consumer<SettingsSet> handler);

    /**
     * @return apply data loading settings handler or null if not set
     */
    @Nullable
    Consumer<SettingsSet> getOnApplyDataLoadingSettingsHandler();

    /**
     * Sets apply data loading settings handler. It will replace default behavior of facet and will be invoked on
     * {@link BeforeShowEvent}.
     * <p>
     * For instance:
     * <pre>{@code
     * @Install(to = "settingsFacet", subject = "onApplyDataLoadingSettingsHandler")
     * private void onApplyDataLoadingSetting(SettingsSet settings) {
     *     // default behavior
     *     settingsFacet.applyDataLoadingSettings(settings);
     * }
     * }
     * </pre>
     *
     * @param handler apply settings handler
     */
    void setOnApplyDataLoadingSettingsHandler(Consumer<SettingsSet> handler);

    /**
     * @return save settings handler or null if not set
     */
    @Nullable
    Consumer<SettingsSet> getOnSaveSettingsHandler();

    /**
     * Set save settings handler. It will replace default behavior of facet and will be invoked on
     * {@link AfterDetachEvent}.
     * <p>
     * For instance:
     * <pre>{@code
     * @Install(to = "settingsFacet", subject = "onSaveSettingsHandler")
     * private void onSaveSetting(SettingsSet settings) {
     *     // default behavior
     *     settingsFacet.saveSettings(settings);
     * }
     * }
     * </pre>
     *
     * @param handler save settings handler
     */
    void setOnSaveSettingsHandler(Consumer<SettingsSet> handler);

    /**
     * Provides information about source component due to settings apply is invoked and its child components.
     */
    class SettingsSet {

        protected Component source;
        protected Collection<Component> components;
        protected ScreenSettings screenSettings;

        public SettingsSet(Component source, Collection<Component> components, ScreenSettings screenSettings) {
            this.source = source;
            this.components = components;
            this.screenSettings = screenSettings;
        }

        /**
         * @return {@link Window} for first settings applying. Also can return {@link WebTabSheet} or {@link Accordion}
         * if it has lazy tab.
         */
        public Component getSource() {
            return source;
        }

        /**
         * @return child component of source component. For  {@link WebTabSheet} and {@link Accordion} it will return
         * components from lazy tab.
         */
        public Collection<Component> getComponents() {
            return components;
        }

        /**
         * @return screen settings
         */
        public ScreenSettings getScreenSettings() {
            return screenSettings;
        }
    }
}
