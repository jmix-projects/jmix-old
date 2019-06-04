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

package io.jmix.ui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import io.jmix.core.BeanLocator;
import io.jmix.core.Events;
import io.jmix.core.GlobalConfig;
import io.jmix.core.security.UserSession;
import io.jmix.core.security.UserSessionSource;
import io.jmix.ui.components.RootWindow;
import io.jmix.ui.generic.*;
import io.jmix.ui.icons.IconResolver;
import io.jmix.ui.sys.TestIdManager;
import io.jmix.ui.sys.WebJarResourceResolver;
import io.jmix.ui.sys.events.UiEventsMulticaster;
import io.jmix.ui.theme.ThemeConstantsRepository;
import org.springframework.context.MessageSource;

import javax.inject.Inject;

@Theme("valo") // todo use halo
@Push(transport = Transport.WEBSOCKET_XHR)
@SpringUI
public class AppUI extends UI {
    @Inject
    protected MessageSource messageSource;
    @Inject
    protected Events events;

    @Inject
    protected GlobalConfig globalConfig;
    @Inject
    protected WebConfig webConfig;

//    @Inject
//    protected UserSettingsTools userSettingsTools; todo settings
    @Inject
    protected ThemeConstantsRepository themeConstantsRepository;

    @Inject
    protected UserSessionSource userSessionSource;
//    @Inject
//    protected UserSessionService userSessionService; todo ping session ?

    @Inject
    protected UiEventsMulticaster uiEventsMulticaster;

    @Inject
    protected IconResolver iconResolver;
    @Inject
    protected WebJarResourceResolver webJarResourceResolver;

    @Inject
    protected BeanLocator beanLocator;

    protected TestIdManager testIdManager = new TestIdManager();

    protected boolean testMode = false;
    protected boolean performanceTestMode = false;

    protected CubaFileDownloader fileDownloader;

    protected RootWindow topLevelWindow;

    protected Fragments fragments;
    protected Screens screens;
    protected Dialogs dialogs;
    protected Notifications notifications;
    protected WebBrowserTools webBrowserTools;

//    todo navigation
//    protected UrlChangeHandler urlChangeHandler;
//    protected UrlRouting urlRouting;
//    protected History history;

    protected UserSession userSession;

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    /**
     * Dynamically init external JS libraries.
     * You should create JavaScriptExtension class and extend UI object here. <br>
     * <p>
     * Example: <br>
     * <pre><code>
     * JavaScriptExtension:
     *
     * {@literal @}JavaScript("resources/jquery/jquery-1.10.2.min.js")
     * public class JQueryIntegration extends AbstractJavaScriptExtension {
     *
     *     {@literal @}Override
     *     public void extend(AbstractClientConnector target) {
     *         super.extend(target);
     *     }
     *
     *     {@literal @}Override
     *     protected Class&lt;? extends ClientConnector&gt; getSupportedParentType() {
     *         return UI.class;
     *     }
     * }
     *
     * AppUI:
     *
     * protected void initJsLibraries() {
     *     new JQueryIntegration().extend(this);
     * }</code></pre>
     * <p>
     * If you want to include scripts to generated page statically see {@link com.haulmont.cuba.web.sys.CubaBootstrapListener}.
     */
    protected void initJsLibraries() {
    }

    protected void initInternalComponents() {
        fileDownloader = new CubaFileDownloader();
        fileDownloader.extend(this);
    }

    protected App createApplication() {
        return beanLocator.getPrototype(App.NAME);
    }

    @Override
    protected void init(VaadinRequest request) {

    }
}