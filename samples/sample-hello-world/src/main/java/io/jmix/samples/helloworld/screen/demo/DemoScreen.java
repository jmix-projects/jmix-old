package io.jmix.samples.helloworld.screen.demo;

import io.jmix.core.entity.BaseUser;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.component.TextField;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Locale;

@UiController("demoScreen")
@UiDescriptor("demo-screen.xml")
@Route("demo")
@LoadDataBeforeShow
public class DemoScreen extends Screen {

    @Autowired
    private TextField<String> userField;

    @Autowired
    private TextField<String> localeField;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Subscribe
    protected void onInit(InitEvent event) {
        BaseUser user = currentAuthentication.getUser();
        Locale locale = currentAuthentication.getLocale();

        userField.setValue(user.getUsername());
        localeField.setValue(locale.toString());
    }
}