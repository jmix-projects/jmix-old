package io.jmix.samples.ui.screen.sys.login;

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
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Locale;

@UiController("sampler_LoginScreen")
@UiDescriptor("sample-login-screen.xml")
@Route(path = "login", root = true)
public class SamplerLoginScreen extends Screen {

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
        initLocalesField();
        initDefaultCredentials();
    }

    protected void initLocalesField() {
        localesField.setOptionsMap(coreProperties.getAvailableLocales());
        localesField.setValue(coreProperties.getAvailableLocales().values().iterator().next());
    }

    protected void initDefaultCredentials() {
        usernameField.setValue("admin");
        passwordField.setValue("admin");
    }

    @Subscribe("submit")
    protected void onSubmitActionPerformed(Action.ActionPerformedEvent event) {
        login();
    }

    protected void login() {
        String username = usernameField.getValue();
        String password = passwordField.getValue() != null ? passwordField.getValue() : "";

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(messages.getMessage(getClass(), "emptyUsernameOrPassword"))
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
            notifications.create(Notifications.NotificationType.ERROR)
                    .withCaption(messages.getMessage(getClass(), "loginFailed"))
                    .withDescription(e.getMessage())
                    .show();
        }
    }
}
