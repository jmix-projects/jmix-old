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

package io.jmix.ui.persistence.settings.facet;

import io.jmix.core.AppBeans;
import io.jmix.core.commons.events.EventHub;
import io.jmix.core.commons.events.Subscription;
import io.jmix.ui.components.Component;
import io.jmix.ui.components.Frame;
import io.jmix.ui.components.impl.WebAbstractFacet;
import io.jmix.ui.persistence.settings.ScreenSettingsManager;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Screen.AfterDetachEvent;
import io.jmix.ui.screen.Screen.AfterShowEvent;
import io.jmix.ui.screen.Screen.BeforeShowEvent;
import io.jmix.ui.screen.UiControllerUtils;
import io.jmix.ui.settings.ScreenSettings;
import io.jmix.ui.settings.facet.ScreenSettingsFacet;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WebScreenSettingsFacet extends WebAbstractFacet implements ScreenSettingsFacet {

    protected Set<String> componentIds;

    protected boolean auto = true;

    protected ScreenSettings screenSettings;

    protected Consumer<ScreenSettings> onApplySettingsHandler;
    protected Consumer<ScreenSettings> onApplyDataLoadingSettingsHandler;
    protected Consumer<ScreenSettings> onSaveSettingsHandler;

    @Inject
    protected ScreenSettingsManager settingsCoordinator;

    @Override
    public ScreenSettings getSettings() {
        return screenSettings;
    }

    @Override
    public void applySettings(ScreenSettings settings) {
        Collection<Component> components = getComponents();

        settingsCoordinator.applySettings(components, settings);
    }

    @Override
    public void applyDataLoadingSettings(ScreenSettings settings) {
        Collection<Component> components = getComponents();

        settingsCoordinator.applyDataLoadingSettings(components, settings);
    }

    @Override
    public void saveSettings(ScreenSettings settings) {
        Collection<Component> components = getComponents();

        settingsCoordinator.saveSettings(components, screenSettings);
    }

    @Override
    public boolean isAuto() {
        return auto;
    }

    @Override
    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    @Override
    public void addComponentIds(String... ids) {
        if (componentIds == null) {
            componentIds = new HashSet<>();
        }

        componentIds.addAll(Arrays.asList(ids));
    }

    @Override
    public void addComponents(Component... components) {
        if (componentIds == null) {
            componentIds = new HashSet<>();
        }

        Set<String> ids = Arrays.stream(components)
                .map(Component::getId)
                .collect(Collectors.toSet());

        componentIds.addAll(ids);
    }

    @Override
    public Set<String> getComponentIds() {
        if (componentIds == null) {
            return Collections.emptySet();
        }

        return componentIds;
    }

    @Override
    public Consumer<ScreenSettings> getOnApplySettingsHandler() {
        return onApplySettingsHandler;
    }

    @Override
    public void setOnApplySettingsHandler(Consumer<ScreenSettings> handler) {
        this.onApplySettingsHandler = handler;
    }

    @Override
    public Consumer<ScreenSettings> getOnApplyDataLoadingSettingsHandler() {
        return onApplyDataLoadingSettingsHandler;
    }

    @Override
    public void setOnApplyDataLoadingSettingsHandler(Consumer<ScreenSettings> handler) {
        this.onApplyDataLoadingSettingsHandler = handler;
    }

    @Override
    public Consumer<ScreenSettings> getOnSaveSettingsHandler() {
        return onSaveSettingsHandler;
    }

    @Override
    public void setOnSaveSettingsHandler(Consumer<ScreenSettings> handler) {
        this.onSaveSettingsHandler = handler;
    }

    @Override
    public Subscription addBeforeApplySettingsListener(Consumer<BeforeApplySettingsEvent> listener) {
        return getEventHub().subscribe(BeforeApplySettingsEvent.class, listener);
    }

    @Override
    public Subscription addBeforeApplyDataLoadSettingsListener(Consumer<BeforeApplyDataLoadSettingsEvent> listener) {
        return getEventHub().subscribe(BeforeApplyDataLoadSettingsEvent.class, listener);
    }

    @Override
    public Subscription addBeforeSaveSettingsListener(Consumer<BeforeSaveSettingsEvent> listener) {
        return getEventHub().subscribe(BeforeSaveSettingsEvent.class, listener);
    }

    @Override
    public void setOwner(@Nullable Frame owner) {
        super.setOwner(owner);

        if (getScreenOwner() != null)
            screenSettings = AppBeans.getPrototype(ScreenSettings.NAME, getScreenOwner().getId());

        subscribe();
    }

    protected void subscribe() {
        Frame frame = getOwner();
        if (frame == null) {
            throw new IllegalStateException("ScreenSettings facet is not attached to Frame");
        }

        EventHub screenEvents = UiControllerUtils.getEventHub(frame.getFrameOwner());

        screenEvents.subscribe(BeforeShowEvent.class, this::onScreenBeforeShow);
        screenEvents.subscribe(AfterShowEvent.class, this::onScreenAfterShow);
        screenEvents.subscribe(AfterDetachEvent.class, this::onScreenAfterDetach);

    }

    @Nullable
    protected Screen getScreenOwner() {
        return getOwner() == null ? null : (Screen) getOwner().getFrameOwner();
    }

    protected void onScreenBeforeShow(BeforeShowEvent event) {
        getEventHub().publish(BeforeApplyDataLoadSettingsEvent.class,
                new BeforeApplyDataLoadSettingsEvent(getScreenOwner(), screenSettings));

        if (onApplyDataLoadingSettingsHandler != null) {
            onApplyDataLoadingSettingsHandler.accept(screenSettings);
        } else {
            applyDataLoadingSettings(screenSettings);
        }
    }

    protected void onScreenAfterShow(AfterShowEvent event) {
        getEventHub().publish(BeforeApplySettingsEvent.class,
                new BeforeApplySettingsEvent(getScreenOwner(), screenSettings));

        if (onApplySettingsHandler != null) {
            onApplySettingsHandler.accept(screenSettings);
        } else {
            applySettings(screenSettings);
        }
    }

    protected void onScreenAfterDetach(AfterDetachEvent event) {
        getEventHub().publish(BeforeSaveSettingsEvent.class,
                new BeforeSaveSettingsEvent(getScreenOwner(), screenSettings));

        if (onSaveSettingsHandler != null) {
            onSaveSettingsHandler.accept(screenSettings);
        } else {
            saveSettings(screenSettings);
        }
    }

    protected Collection<Component> getComponents() {
        checkAttachedFrame();
        assert getOwner() != null;

        if (auto) {
            return getOwner().getComponents();
        }

        if (CollectionUtils.isNotEmpty(componentIds)) {
            return getOwner().getComponents().stream()
                    .filter(component -> componentIds.contains(component.getId()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    protected void checkAttachedFrame() {
        Frame frame = getOwner();
        if (frame == null) {
            throw new IllegalStateException("ScreenSettingsFacet is not attached to the screen");
        }
    }
}
