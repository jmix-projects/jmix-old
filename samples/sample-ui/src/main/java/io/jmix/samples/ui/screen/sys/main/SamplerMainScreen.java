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

package io.jmix.samples.ui.screen.sys.main;

import io.jmix.core.CoreProperties;
import io.jmix.core.entity.BaseUser;
import io.jmix.core.security.ClientDetails;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.core.security.SecurityContextHelper;
import io.jmix.core.security.authentication.CoreAuthenticationToken;
import io.jmix.samples.ui.config.MenuItem;
import io.jmix.samples.ui.config.SamplerMenuConfig;
import io.jmix.samples.ui.screen.sys.maindashboard.DashboardItemClickEvent;
import io.jmix.samples.ui.screen.sys.maindashboard.SamplerMainDashboardFragment;
import io.jmix.samples.ui.util.SamplerHelper;
import io.jmix.security.authentication.SecuredAuthenticationToken;
import io.jmix.ui.App;
import io.jmix.ui.AppUI;
import io.jmix.ui.Screens;
import io.jmix.ui.component.AppWorkArea;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.ComboBox;
import io.jmix.ui.component.SplitPanel;
import io.jmix.ui.component.TextField;
import io.jmix.ui.component.TextInputField;
import io.jmix.ui.component.Window;
import io.jmix.ui.component.mainwindow.SideMenu;
import io.jmix.ui.navigation.RedirectHandler;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.navigation.UrlRouting;
import io.jmix.ui.screen.LoadDataBeforeShow;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.ScreenOptions;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import io.jmix.ui.settings.UserSettingsTools;
import io.jmix.ui.theme.ThemeConstantsRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Route(path = "main")
@UiDescriptor("sampler-main-screen.xml")
@UiController("sampler_MainScreen")
@LoadDataBeforeShow
public class SamplerMainScreen extends Screen implements Window.HasWorkArea {

    protected final static String FOUND_ITEM_STYLE = "found-item";

    @Autowired
    protected SamplerMenuConfig menuConfig;
    @Autowired
    protected SamplerHelper helper;
    @Autowired
    protected Screens screens;
    @Autowired
    protected CoreProperties coreProperties;
    @Autowired
    protected UrlRouting urlRouting;
    @Autowired
    protected CurrentAuthentication currentAuthentication;
    @Autowired
    protected ThemeConstantsRepository themeConstantsRepository;
    @Autowired
    protected UserSettingsTools userSettingsTools;

    @Autowired
    protected TextField<String> searchField;
    @Autowired
    protected SideMenu sideMenu;
    @Autowired
    protected SplitPanel mainSplit;
    @Autowired
    protected ComboBox<Locale> localesComboBox;
    @Autowired
    protected ComboBox<String> themeComboBox;
    @Autowired
    protected SamplerMainDashboardFragment dashboardFragment;

    protected List<SideMenu.MenuItem> foundItems = new ArrayList<>();
    protected List<String> parentListIdsToExpand = new ArrayList<>();

    @Override
    public AppWorkArea getWorkArea() {
        return (AppWorkArea) getWindow().getComponent("workArea");
    }

    public void expandItemsFromDirectLink(String id) {
        sideMenu.removeAllMenuItems();
        initMenuItems();
        expandAllParentRecursively(id);
        SideMenu.MenuItem item = sideMenu.getMenuItemNN(id);
        item.setStyleName(FOUND_ITEM_STYLE);
        item.setExpanded(true);
    }

    public void initDashboardByRootItemId(String itemId) {
        dashboardFragment.clearDashboard();
        if (menuConfig.isRootItem(itemId)) {
            dashboardFragment.initDashboardMenu(itemId);
        } else {
            dashboardFragment.initDashboardMenu(SamplerMainDashboardFragment.MENU_ROOT_ITEM_ID);
        }
    }

    @Subscribe
    public void onInit(InitEvent event) {
        initSideMenu();
        initMainSplit();
        initLocales();
        initThemes();
        initDashboardFragment();
        initRedirectHandler();

        searchField.focus();
    }

    protected void initSideMenu() {
        sideMenu.removeAllMenuItems();
        initMenuItems();
    }

    protected void initMenuItems() {
        List<MenuItem> menuItems = menuConfig.getRootItems();
        for (MenuItem item : menuItems) {
            SideMenu.MenuItem menuItem = sideMenu.createMenuItem(item.getId());
            menuItem.setCaption(menuConfig.getMenuItemCaption(item.getId()));
            loadMenuItems(item, menuItem);
            sideMenu.addMenuItem(menuItem);
        }
    }

    protected void initMainSplit() {
        mainSplit.setDockable(true);
    }

    protected void initLocales() {
        AppUI ui = AppUI.getCurrent();
        if (ui == null) {
            return;
        }

        localesComboBox.setOptionsMap(coreProperties.getAvailableLocales());
        localesComboBox.setValue(currentAuthentication.getLocale());

        localesComboBox.setVisible(coreProperties.isLocaleSelectVisible());

        localesComboBox.addValueChangeListener(e -> {
            Locale selectedLocale = e.getValue();
            if (selectedLocale != null) {
                RedirectHandler handler = ui.getUrlChangeHandler().getRedirectHandler();
                if (handler != null) {
                    handler.schedule(urlRouting.getState());
                }
                // todo change locale
            }
        });

        localesComboBox.setOptionStyleProvider(locale ->
                locale.equals(currentAuthentication.getLocale()) ? "selected-locale" : null
        );
    }

    protected void initThemes() {
        Map<String, String> availableThemes = themeConstantsRepository.getAvailableThemes().stream()
                .collect(Collectors.toMap(theme -> StringUtils.substringAfter(theme, "demo-"), theme -> theme));
        themeComboBox.setOptionsMap(availableThemes);

        String userAppTheme = userSettingsTools.loadTheme();
        themeComboBox.setValue(userAppTheme);

        themeComboBox.addValueChangeListener(valueChangeEvent -> {
            AppUI ui = AppUI.getCurrent();
            String selectedTheme = valueChangeEvent.getValue();
            if (ui != null && selectedTheme != null) {
                RedirectHandler redirectHandler = ui.getUrlChangeHandler().getRedirectHandler();
                if (redirectHandler != null) {
                    redirectHandler.schedule(urlRouting.getState());
                }

                userSettingsTools.saveAppWindowTheme(selectedTheme);
                ui.getApp().createTopLevelWindow();
            }
        });

        themeComboBox.setOptionStyleProvider(theme -> theme.equals(userSettingsTools.loadTheme()) ? "selected-theme" : null);
    }

    protected void initDashboardFragment() {
        dashboardFragment.addDashboardItemClickListener(this::onDashboardItemClicked);
    }

    protected void onDashboardItemClicked(DashboardItemClickEvent event) {
        sideMenu.removeAllMenuItems();
        initMenuItems();

        SideMenu.MenuItem item = sideMenu.getMenuItem(event.getMenuItemId());
        if (item != null) {
            item.setStyleName(FOUND_ITEM_STYLE);
            item.setExpanded(true);
            expandAllParentRecursively(event.getMenuItemId());
        }
    }

    protected void initRedirectHandler() {
        AppUI ui = AppUI.getCurrent();
        if (ui == null) {
            return;
        }

        if (ui.getUrlChangeHandler().getRedirectHandler() == null) {
            RedirectHandler redirectHandler = getBeanLocator().getPrototype(RedirectHandler.class, ui);
            ui.getUrlChangeHandler().setRedirectHandler(redirectHandler);
        }
    }

    protected void loadMenuItems(MenuItem parentSamplerItem,
                                 SideMenu.MenuItem parentSideMenuItem) {
        for (MenuItem currentItem : parentSamplerItem.getChildren()) {
            SideMenu.MenuItem child;
            String id = currentItem.getId();
            if (currentItem.isMenu()) {
                child = sideMenu.createMenuItem(id);
                child.setCaption(menuConfig.getMenuItemCaption(currentItem.getId()));

                loadMenuItems(currentItem, child);
                parentSideMenuItem.addChildItem(child);
            } else {
                child = sideMenu.createMenuItem(id);
                child.setCaption(menuConfig.getMenuItemCaption(currentItem.getId()));
                child.setCommand(item -> {
                    ScreenOptions screenOptions = helper.getScreenOptions(menuConfig.getItemById(item.getId()));
                    removeStyleNameFromAll(FOUND_ITEM_STYLE, sideMenu.getMenuItems());
                    item.setStyleName(FOUND_ITEM_STYLE);
                    screens.create(helper.getSampleBrowserId(), OpenMode.NEW_TAB, screenOptions)
                            .show();
                });
                parentSideMenuItem.addChildItem(child);
            }
        }
    }

    protected void removeStyleNameFromAll(String styleName, List<SideMenu.MenuItem> list) {
        for (SideMenu.MenuItem menuItem : list) {
            menuItem.removeStyleName(styleName);
            if (menuItem.hasChildren()) {
                removeStyleNameFromAll(styleName, menuItem.getChildren());
            }
        }
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        AppUI ui = AppUI.getCurrent();
        if (ui != null) {
            RedirectHandler redirectHandler = ui.getUrlChangeHandler().getRedirectHandler();
            if (redirectHandler != null
                    && redirectHandler.scheduled()) {
                redirectHandler.redirect();
            }
        }
    }

    @Subscribe("searchButton")
    protected void onSearchButtonClick(Button.ClickEvent event) {
        search(searchField.getValue());
    }

    @Subscribe("searchField")
    protected void onSearchFieldEnterPress(TextInputField.EnterPressEvent event) {
        search(searchField.getValue());
    }

    protected void search(String searchValue) {
        foundItems.clear();
        sideMenu.removeAllMenuItems();
        initMenuItems();
        if (!StringUtils.isEmpty(searchValue)) {
            findItemsRecursively(sideMenu.getMenuItems(), searchValue);

            for (SideMenu.MenuItem item : foundItems) {
                if (menuConfig.getItemById(item.getId()).getParent() != null) {
                    expandAllParentRecursively(item.getId());
                }
                if (CollectionUtils.isNotEmpty(item.getChildren())) {
                    expand(sideMenu.getMenuItemNN(item.getId()), true);
                }
            }

            removeNotRequestedItems(sideMenu.getMenuItems(), null, searchValue);
        }
    }

    protected void findItemsRecursively(List<SideMenu.MenuItem> items, String searchValue) {
        for (SideMenu.MenuItem item : items) {
            if (StringUtils.containsIgnoreCase(item.getCaption(), searchValue)) {
                item.setStyleName(FOUND_ITEM_STYLE);
                foundItems.add(item);
            }
            if (item.hasChildren()) {
                findItemsRecursively(item.getChildren(), searchValue);
            }
        }
    }

    protected void removeNotRequestedItems(List<SideMenu.MenuItem> list,
                                           SideMenu.MenuItem parentItem, String searchValue) {
        for (SideMenu.MenuItem item : list) {
            if (item.hasChildren()) {
                if (!item.isExpanded()) {
                    if (parentItem != null) {
                        parentItem.removeChildItem(item);
                    }
                    sideMenu.removeMenuItem(item);
                } else if (!StringUtils.containsIgnoreCase(item.getCaption(), searchValue)) {
                    removeNotRequestedItems(item.getChildren(), item, searchValue);
                }
            } else if (!StringUtils.containsIgnoreCase(item.getCaption(), searchValue)) {
                if (parentItem != null) {
                    parentItem.removeChildItem(item);
                }
                sideMenu.removeMenuItem(item);
            }
        }
    }

    @Subscribe("expandAllBtn")
    protected void onExpandAllBtnClick(Button.ClickEvent event) {
        for (SideMenu.MenuItem item : sideMenu.getMenuItems()) {
            expand(item, true);
        }
    }

    @Subscribe("collapseAllBtn")
    protected void onCollapseAllBtnClick(Button.ClickEvent event) {
        for (SideMenu.MenuItem item : sideMenu.getMenuItems()) {
            expand(item, false);
        }
    }

    protected void expand(SideMenu.MenuItem item, boolean isExpand) {
        if (item.hasChildren()) {
            item.setExpanded(isExpand);
            for (SideMenu.MenuItem menuItem : item.getChildren()) {
                if (menuItem.hasChildren()) {
                    expand(menuItem, isExpand);
                }
            }
        }
    }

    protected void expandAllParentRecursively(String id) {
        parentListIdsToExpand.clear();
        fillParentListToExpand(id);
        for (String s : parentListIdsToExpand) {
            SideMenu.MenuItem item = sideMenu.getMenuItem(s);
            if (item != null) {
                item.setExpanded(true);
            }
        }
    }

    protected void fillParentListToExpand(String id) {
        MenuItem itemToExpand = menuConfig.getItemById(id);
        if (itemToExpand.getParent() != null) {
            parentListIdsToExpand.add(itemToExpand.getParent().getId());
            fillParentListToExpand(itemToExpand.getParent().getId());
        }
    }
}
