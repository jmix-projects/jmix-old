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

package io.jmix.samples.ui.screen;

import io.jmix.ui.Screens;
import io.jmix.ui.UiComponents;
import io.jmix.ui.actions.BaseAction;
import io.jmix.ui.components.AppWorkArea;
import io.jmix.ui.components.Button;
import io.jmix.ui.components.VBoxLayout;
import io.jmix.ui.components.Window;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Base class for a controller of application Main screen.
 */
@UiDescriptor("custom-main-screen.xml")
@UiController("custom-main")
public class CustomMainScreen extends Screen implements Window.HasWorkArea {

    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected Screens screens;

    @Subscribe
    protected void afterShow(AfterShowEvent event) {
        VBoxLayout sideMenuPanel = (VBoxLayout) getWindow().getComponentNN("sideMenuPanel");

        Button showLegacyScreenBtn = uiComponents.create(Button.class);
        showLegacyScreenBtn.setCaption("Show LegacyScreen");
        showLegacyScreenBtn.setAction(
                new BaseAction("showLegacyScreen")
                        .withHandler(e -> {
                            Screen legacyScreen = screens.create("legacyScreen", OpenMode.NEW_TAB);
                            legacyScreen.show();
                        }));

        sideMenuPanel.add(showLegacyScreenBtn);

        Button showUserBrowser = uiComponents.create(Button.class);
        showUserBrowser.setCaption("Show User Browser");
        showUserBrowser.setAction(
                new BaseAction("showUserBrowser")
                        .withHandler(e -> {
                            screens.create("userBrowser", OpenMode.NEW_TAB)
                                    .show();
                        }));

        sideMenuPanel.add(showUserBrowser);
    }

    @Nullable
    @Override
    public AppWorkArea getWorkArea() {
        return (AppWorkArea) getWindow().getComponent("workArea");
    }
}
