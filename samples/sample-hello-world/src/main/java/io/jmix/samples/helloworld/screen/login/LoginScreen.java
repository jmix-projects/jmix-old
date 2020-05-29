/*
 * Copyright (c) 2008-2019 Haulmont.
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

package io.jmix.samples.helloworld.screen.login;


import io.jmix.core.CoreProperties;
import io.jmix.core.Messages;
import io.jmix.core.security.ClientDetails;
import io.jmix.core.security.SecurityContextHelper;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiProperties;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.ComboBox;
import io.jmix.ui.component.PasswordField;
import io.jmix.ui.component.TextField;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Locale;

@Route(path = "login", root = true)
@UiDescriptor("login-screen.xml")
@UiController("login")
public class LoginScreen extends Screen {

    private static final Logger log = LoggerFactory.getLogger(LoginScreen.class);

    @Autowired
    protected TextField<String> usernameField;

    @Autowired
    protected PasswordField passwordField;

    @Autowired
    protected ComboBox<Locale> localesField;

    @Autowired
    protected Notifications notifications;

    @Autowired
    protected Messages messages;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    protected CoreProperties coreProperties;

    @Autowired
    protected UiProperties uiProperties;

    @Autowired
    protected ScreenBuilders screenBuilders;

    @Subscribe
    protected void onInit(InitEvent event) {
        usernameField.focus();
        initDefaultCredentials();
        initLocalesField();
    }

    @Subscribe("submit")
    protected void onSubmitActionPerformed(Action.ActionPerformedEvent event) {
        login();
    }

    protected void initLocalesField() {
        localesField.setOptionsMap(coreProperties.getAvailableLocales());
        localesField.setValue(coreProperties.getAvailableLocales().values().iterator().next());
    }

    protected void initDefaultCredentials() {
        usernameField.setValue("admin");
        passwordField.setValue("admin");
    }

    protected void login() {
        String username = usernameField.getValue();
        String password = passwordField.getValue() != null ? passwordField.getValue() : "";

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(messages.getMessage("loginWindow.emptyLoginOrPassword"))
                    .show();
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            ClientDetails clientDetails = ClientDetails.builder()
                    .locale(localesField.getValue())
                    .build();
            authenticationToken.setDetails(clientDetails);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHelper.setAuthentication(authentication);

            String mainScreenId = uiProperties.getMainScreenId();
            screenBuilders.screen(this)
                    .withScreenId(mainScreenId)
                    .withOpenMode(OpenMode.ROOT)
                    .build()
                    .show();
        } catch (BadCredentialsException e) {
            showLoginException(e.getMessage());
        }
    }

    protected void showLoginException(String message) {
        String title = messages.getMessage("loginWindow.loginFailed");
        notifications.create(Notifications.NotificationType.ERROR)
                .withCaption(title)
                .withDescription(message)
                .show();
    }
}
