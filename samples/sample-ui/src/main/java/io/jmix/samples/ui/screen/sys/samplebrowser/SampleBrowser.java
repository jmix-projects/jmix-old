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

package io.jmix.samples.ui.screen.sys.samplebrowser;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import io.jmix.core.CoreProperties;
import io.jmix.core.MessageTools;
import io.jmix.core.Messages;
import io.jmix.samples.ui.config.MenuItem;
import io.jmix.samples.ui.config.SamplerMenuConfig;
import io.jmix.samples.ui.screen.sys.main.SamplerMainScreen;
import io.jmix.samples.ui.util.SamplerHelper;
import io.jmix.ui.App;
import io.jmix.ui.AppUI;
import io.jmix.ui.Fragments;
import io.jmix.ui.Screens;
import io.jmix.ui.UiComponents;
import io.jmix.ui.WindowConfig;
import io.jmix.ui.WindowInfo;
import io.jmix.ui.component.*;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.navigation.UrlIdSerializer;
import io.jmix.ui.navigation.UrlParamsChangedEvent;
import io.jmix.ui.navigation.UrlRouting;
import io.jmix.ui.screen.LoadDataBeforeShow;
import io.jmix.ui.screen.MapScreenOptions;
import io.jmix.ui.screen.MessageBundle;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiControllerUtils;
import io.jmix.ui.screen.UiDescriptor;
import io.jmix.ui.sys.ControllerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Route("sample")
@UiController("sample-browser")
@UiDescriptor("sample-browser.xml")
public class SampleBrowser extends Screen {

    protected static final String DESCRIPTION_BOX_STYLE = "description-box";
    protected static final String DOC_URL_MESSAGES_KEY = "docUrl";

    @Autowired
    protected UiComponents uiComponents;
    @Autowired
    protected SamplerHelper samplerHelper;
    @Autowired
    protected SamplerMenuConfig samplerMenuConfig;
    @Autowired
    protected Messages messages;
    @Autowired
    protected MessageBundle messageBundle;
    @Autowired
    protected MessageTools messageTools;
    @Autowired
    protected CoreProperties coreProperties;
    @Autowired
    protected Fragments fragments;
    @Autowired
    protected UrlRouting urlRouting;
    @Autowired
    protected WindowConfig windowConfig;

    protected String sampleId;
    protected TabSheet tabSheet;

    @Subscribe
    protected void onInit(InitEvent event) {
        if (!(event.getOptions() instanceof MapScreenOptions)) {
            return;
        }

        MapScreenOptions options = (MapScreenOptions) event.getOptions();
        Map<String, Object> params = options.getParams();

        sampleId = (String) params.get("windowId");
        updateSample(sampleId);
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        // When a locale is changed, UrlParamsChangedEvent is fired after AfterShowEvent
        // See cuba-platform/cuba#1665
        if (sampleId == null) {
            return;
        }

        String serializedSampleId = UrlIdSerializer.serializeId(sampleId);
        urlRouting.replaceState(this, ImmutableMap.of("id", serializedSampleId));
    }

    @Subscribe
    protected void onUrlParamsChanged(UrlParamsChangedEvent event) {
        String serializedSampleId = event.getParams().get("id");
        sampleId = (String) UrlIdSerializer.deserializeId(String.class, serializedSampleId);
        updateSample(sampleId);

        Screens.OpenedScreens openedScreens = AppUI.getCurrent().getScreens().getOpenedScreens();
        Screen rootScreen = openedScreens.getRootScreen();
        if (rootScreen instanceof SamplerMainScreen) {
            ((SamplerMainScreen) rootScreen).expandItemsFromDirectLink(sampleId);
        }
    }

    protected void updateSample(String sampleId) {
        MenuItem item = samplerMenuConfig.getItemById(sampleId);

        ScreenFragment screenFragment = fragments
                .create(this, sampleId, new MapScreenOptions(item.getScreenParams()))
                .init();
        Fragment fragment = screenFragment.getFragment();
        fragment.setId("sampleFrame");

        loadScreenData(screenFragment, fragment);
        updateLayout(fragment, item);
        updateCaption(sampleId, item);
        updateTabs(sampleId, item);
        focusFirstPossibleComponent(fragment);
    }

    protected void loadScreenData(ScreenFragment screenFragment, Fragment fragment) {
        LoadDataBeforeShow annotation = screenFragment.getClass().getAnnotation(LoadDataBeforeShow.class);
        if (annotation != null && annotation.value()) {
            UiControllerUtils.getScreenData(fragment.getFrameOwner())
                    .loadAll();
        }
    }

    protected void updateLayout(Fragment fragment, MenuItem item) {
        getWindow().removeAll();

        TabSheet tabSheet = createTabSheet();

        String splitEnabled = item.getSplitEnabled();
        if (BooleanUtils.toBoolean(splitEnabled)) {
            SplitPanel split = uiComponents.create(SplitPanel.class);
            split.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
            split.setWidth("100%");
            split.setHeight("100%");

            ComponentContainer vBox = createContainer(false, false, true, false);
            vBox.add(fragment);

            split.add(vBox);
            split.add(tabSheet);

            fragment.setHeight("100%");

            getWindow().add(split);
        } else {
            getWindow().add(fragment);
            getWindow().add(createSpacer());
            getWindow().add(tabSheet);
            getWindow().expand(tabSheet);
        }
    }

    protected TabSheet createTabSheet() {
        tabSheet = uiComponents.create(TabSheet.NAME);
        tabSheet.setId("tabSheet");
        tabSheet.setHeightFull();
        return tabSheet;
    }

    protected Component createSpacer() {
        Component spacer = uiComponents.create(Label.TYPE_STRING);
        spacer.setId("spacer");
        spacer.setHeight("10px");
        return spacer;
    }

    protected void updateCaption(String id, MenuItem item) {
        String caption = samplerMenuConfig.getMenuItemCaption(item.getId());
        if (Strings.isNullOrEmpty(caption)) {
            caption = id;
        }
        getWindow().setCaption(caption);
    }

    protected void updateTabs(String id, MenuItem item) {
        tabSheet.removeAllTabs();

        WindowInfo info = windowConfig.getWindowInfo(item.getId());

        String descriptionsPack = info.getControllerClass().getPackage().getName();
        addTab(messageBundle.getMessage("description"), createDescription(descriptionsPack, item.getUrl(), item.getDocPack(), id));

        String screenSrc = info.getTemplate();
        addSourceTab(screenSrc);

        addSourceTab(getControllerFileName(info.getControllerClassName()));

        List<String> otherFiles = item.getOtherFiles();
        if (CollectionUtils.isNotEmpty(otherFiles)) {
            otherFiles.forEach(this::addSourceTab);
        }

        String messagesPack = samplerHelper.findMessagePack(info);
        if (StringUtils.isNotEmpty(messagesPack)) {
            createMessagesContainers(messagesPack);
        }
    }

    protected Component createDescription(String descriptionsPack,
                                          @Nullable String docUrlSuffix,
                                          @Nullable String docPack,
                                          String frameId) {
        ScrollBoxLayout scrollBoxLayout = uiComponents.create(ScrollBoxLayout.class);
        scrollBoxLayout.addStyleName(DESCRIPTION_BOX_STYLE);
        scrollBoxLayout.setWidth("100%");
        scrollBoxLayout.setHeight("100%");
        scrollBoxLayout.setSpacing(true);

        scrollBoxLayout.add(descriptionText(frameId, descriptionsPack));

        HBoxLayout hbox = uiComponents.create(HBoxLayout.class);
        hbox.setWidth("100%");

        if (!Strings.isNullOrEmpty(docUrlSuffix)) {
            Component docLinks = documentLinks(docPack, docUrlSuffix);
            hbox.add(docLinks);
        }

        hbox.add(permalink(frameId));
        scrollBoxLayout.add(hbox);
        return scrollBoxLayout;
    }

    protected Component descriptionText(String frameId, String descriptionsPack) {
        StringBuilder sb = new StringBuilder();
        String text = samplerHelper.getFileContent(getDescriptionFileName(descriptionsPack, frameId));
        if (!Strings.isNullOrEmpty(text)) {
            sb.append(text);
            sb.append("<hr>");
        }
        Label<String> doc = uiComponents.create(Label.TYPE_STRING);
        doc.setHtmlEnabled(true);
        doc.setHtmlSanitizerEnabled(false);
        doc.setWidth("100%");
        doc.setValue(sb.toString());
        return doc;
    }

    protected String getDescriptionFileName(String descriptionsPack, String frameId) {
        descriptionsPack = descriptionsPack.replaceAll("\\.", "/");
        StringBuilder sb = new StringBuilder(descriptionsPack);
        if (!descriptionsPack.endsWith("/")) {
            sb.append("/");
        }
        sb.append(frameId).append("-");
        sb.append(getUserLocale().toString());
        sb.append(".html");
        return sb.toString();
    }

    protected Component documentLinks(@Nullable String dockPack, String docUrlSuffix) {
        Link docLink = uiComponents.create(Link.class);
        Locale locale = getUserLocale();
        String message = dockPack != null
                ? messages.getMessage(dockPack, DOC_URL_MESSAGES_KEY, locale)
                : messages.getMessage(DOC_URL_MESSAGES_KEY, locale);
        String url = String.format(message, docUrlSuffix);
        docLink.setUrl(url);
        docLink.setCaption(messages.getMessage(getClass(), "documentation"));
        docLink.setTarget("_blank");
        return docLink;
    }

    protected PopupView permalink(String frameId) {
        PopupView permalink = uiComponents.create(PopupView.class);
        permalink.setAlignment(Component.Alignment.TOP_RIGHT);
        permalink.setHideOnMouseOut(false);
        permalink.setDescription(messages.getMessage(this.getClass(), "permalink.description"));
        permalink.setStyleName("external-link");

        TextField<String> content = uiComponents.create(TextField.TYPE_STRING);
        String value = ControllerUtils.getLocationWithoutParams() + "open?screen=" + frameId;
        content.setValue(value);
        content.setWidth((value.length() * 8) + "px");
        content.setEditable(false);
        content.selectAll();
        permalink.setPopupContent(content);

        permalink.addPopupVisibilityListener(event -> {
            if (event.isPopupVisible()) {
                content.focus();
            }
        });

        return permalink;
    }

    protected String getControllerFileName(String controllerName) {
        return controllerName.replaceAll("\\.", "/") + ".java";
    }

    protected void focusFirstPossibleComponent(Fragment fragment) {
        fragment.getComponents().stream()
                .filter(component -> component instanceof Component.Focusable)
                .findFirst()
                .ifPresent(component -> ((Component.Focusable) component).focus());
    }

    protected ComponentContainer createContainer() {
        return createContainer(true, true, true, true);
    }

    protected ComponentContainer createContainer(boolean topEnable, boolean rightEnable,
                                                 boolean bottomEnable, boolean leftEnable) {
        VBoxLayout vBox = uiComponents.create(VBoxLayout.class);
        vBox.setMargin(topEnable, rightEnable, bottomEnable, leftEnable);
        vBox.setHeight("100%");

        return vBox;
    }

    protected void createMessagesContainers(String messagesPack) {
        Locale defaultLocale = messageTools.getDefaultLocale();
        for (Locale locale : coreProperties.getAvailableLocales().values()) {
            String tabTitle;
            if (defaultLocale.equals(locale)) {
                tabTitle = "messages.properties";
            } else {
                tabTitle = String.format("messages_%s.properties", locale.toString());
            }

            String src = samplerHelper.packageToPath(messagesPack) + "/" + tabTitle;
            String content = samplerHelper.getFileContent(src);
            if (StringUtils.isNotBlank(content)) {
                SourceCodeEditor sourceCodeEditor = createSourceCodeEditor(getAceMode(src));
                sourceCodeEditor.setValue(content);
                addTab(tabTitle, sourceCodeEditor);
            }
        }
    }

    protected void addTab(String name, Component component) {
        ComponentContainer container = createContainer();
        container.add(component);
        tabSheet.addTab(name, container);
        TabSheet.Tab tab = tabSheet.getTab(name);
        tab.setCaption(name);
    }

    protected void addSourceTab(String src) {
        if (!Strings.isNullOrEmpty(src)) {
            SourceCodeEditor sourceCodeEditor = createSourceCodeEditor(getAceMode(src));
            sourceCodeEditor.setValue(samplerHelper.getFileContent(src));
            addTab(samplerHelper.getFileName(src), sourceCodeEditor);
        }
    }

    protected SourceCodeEditor createSourceCodeEditor(SourceCodeEditor.Mode mode) {
        SourceCodeEditor editor = uiComponents.create(SourceCodeEditor.class);
        editor.setStyleName("sample-browser");
        editor.setShowPrintMargin(false);
        editor.setMode(mode);
        editor.setEditable(false);
        editor.setWidth("100%");
        editor.setHeight("100%");

        return editor;
    }

    protected SourceCodeEditor.Mode getAceMode(String src) {
        String fileExtension = samplerHelper.getFileExtension(src);

        SourceCodeEditor.Mode mode = SourceCodeEditor.Mode.Text;
        if (fileExtension != null) {
            switch (fileExtension) {
                case "xsd":
                case "xml":
                    mode = SourceCodeEditor.Mode.XML;
                    break;
                case "java":
                    mode = SourceCodeEditor.Mode.Java;
                    break;
                case "js":
                    mode = SourceCodeEditor.Mode.JavaScript;
                    break;
                case "properties":
                    mode = SourceCodeEditor.Mode.Properties;
                    break;
            }
        }

        return mode;
    }

    protected Locale getUserLocale() {
        return App.getInstance().getLocale();
    }
}