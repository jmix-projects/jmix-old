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

import io.jmix.core.commons.events.EventHub;
import io.jmix.core.commons.events.Subscription;
import io.jmix.core.commons.util.Preconditions;
import io.jmix.ui.components.Component;
import io.jmix.ui.components.Frame;
import io.jmix.ui.components.impl.WebAbstractFacet;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Screen.AfterDetachEvent;
import io.jmix.ui.screen.Screen.AfterShowEvent;
import io.jmix.ui.screen.Screen.BeforeShowEvent;
import io.jmix.ui.screen.UiControllerUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class WebScreenSettingsFacet extends WebAbstractFacet implements ScreenSettingsFacet {

    protected List<Component> includeComponents;
    protected List<Component> excludeComponents;

    protected boolean includeAll = true;

    protected ScreenSettings screenSettings;

    protected Consumer<ScreenSettings> applySettingsProvider;
    protected Consumer<ScreenSettings> applyDataLoadSettingsProvider;
    protected Consumer<ScreenSettings> saveSettingsProvider;

    @Override
    public ScreenSettings getSettings() {
        return screenSettings;
    }

    @Override
    public void applySettings(ScreenSettings settings) {
    }

    @Override
    public void applyDataLoadSettings(ScreenSettings settings) {
    }

    @Override
    public void saveSettings(ScreenSettings settings) {
    }

    @Override
    public boolean isIncludeAll() {
        return includeAll;
    }

    @Override
    public void setIncludeAll(boolean includeAll) {
        this.includeAll = includeAll;
    }

    @Override
    public void addExcludeComponentIds(String... ids) {
        if (excludeComponents == null) {
            excludeComponents = new ArrayList<>();
        }

        for (String id : ids) {
            excludeComponents.add(getComponent(id));
        }
    }

    @Override
    public void addExcludeComponents(Component... components) {
        if (excludeComponents == null) {
            excludeComponents = new ArrayList<>();
        }

        for (Component component : components) {
            checkNotNullId(component);

            excludeComponents.add(component);
        }
    }

    @Override
    public Collection<Component> getExcludeComponents() {
        if (excludeComponents == null) {
            return Collections.emptyList();
        }

        return excludeComponents;
    }

    @Override
    public void addIncludeComponentIds(String... ids) {
        if (includeComponents == null) {
            includeComponents = new ArrayList<>();
        }

        for (String id : ids) {
            includeComponents.add(getComponent(id));
        }
    }

    @Override
    public void addIncludeComponents(Component... components) {
        if (includeComponents == null) {
            includeComponents = new ArrayList<>();
        }

        for (Component component : components) {
            checkNotNullId(component);
            includeComponents.add(component);
        }
    }

    @Override
    public Collection<Component> getIncludeComponents() {
        if (includeComponents == null) {
            return Collections.emptyList();
        }

        return includeComponents;
    }

    @Override
    public Consumer<ScreenSettings> getApplySettingsProvider() {
        return applySettingsProvider;
    }

    @Override
    public void setApplySettingsProvider(Consumer<ScreenSettings> provider) {
        this.applySettingsProvider = provider;
    }

    @Override
    public Consumer<ScreenSettings> getApplyDataLoadSettingsProvider() {
        return applyDataLoadSettingsProvider;
    }

    @Override
    public void setApplyDataLoadSettingsProvider(Consumer<ScreenSettings> provider) {
        this.applyDataLoadSettingsProvider = provider;
    }

    @Override
    public Consumer<ScreenSettings> getSaveSettingsProvider() {
        return saveSettingsProvider;
    }

    @Override
    public void setSaveSettingsProvider(Consumer<ScreenSettings> provider) {
        this.saveSettingsProvider = provider;
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

        screenSettings = new ScreenSettings(getScreenOwner());

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

    protected Screen getScreenOwner() {
        assert getOwner() != null;
        return (Screen) getOwner().getFrameOwner();
    }

    protected void onScreenBeforeShow(BeforeShowEvent event) {
        getEventHub().publish(BeforeApplyDataLoadSettingsEvent.class,
                new BeforeApplyDataLoadSettingsEvent(getScreenOwner(), screenSettings));

        if (applyDataLoadSettingsProvider != null) {
            applyDataLoadSettingsProvider.accept(screenSettings);
        } else {
            applyDataLoadSettings(screenSettings);
        }
    }

    protected void onScreenAfterShow(AfterShowEvent event) {
        getEventHub().publish(BeforeApplySettingsEvent.class,
                new BeforeApplySettingsEvent(getScreenOwner(), screenSettings));

        if (applySettingsProvider != null) {
            applySettingsProvider.accept(screenSettings);
        } else {
            applySettings(screenSettings);
        }
    }

    protected void onScreenAfterDetach(AfterDetachEvent event) {
        getEventHub().publish(BeforeSaveSettingsEvent.class,
                new BeforeSaveSettingsEvent(getScreenOwner(), screenSettings));

        if (saveSettingsProvider != null) {
            saveSettingsProvider.accept(screenSettings);
        } else {
            saveSettings(screenSettings);
        }
    }

    protected Component getComponent(String id) {
        Preconditions.checkNotNullArgument(id);

        assert getOwner() != null;
        Component component = getOwner().getComponent(id);

        if (component != null) {
            return component;
        } else {
            throw new IllegalArgumentException(String.format("There is no component with id: '%s'", id));
        }
    }

    protected void checkNotNullId(Component component) {
        if (component.getId() == null) {
            throw new IllegalArgumentException(
                    String.format("Component %s does not have an id", component.getClass()));
        }
    }
}
