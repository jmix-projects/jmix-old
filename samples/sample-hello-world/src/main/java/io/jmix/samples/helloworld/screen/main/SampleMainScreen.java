package io.jmix.samples.helloworld.screen.main;

import io.jmix.core.Messages;
import io.jmix.ui.AppUI;
import io.jmix.ui.ScreenTools;
import io.jmix.ui.Screens;
import io.jmix.ui.component.AppWorkArea;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.Image;
import io.jmix.ui.component.ThemeResource;
import io.jmix.ui.component.Window;
import io.jmix.ui.component.dev.LayoutAnalyzerContextMenuProvider;
import io.jmix.ui.component.mainwindow.AppMenu;
import io.jmix.ui.component.mainwindow.UserIndicator;
import io.jmix.ui.screen.LoadDataBeforeShow;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiControllerUtils;
import io.jmix.ui.screen.UiDescriptor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

@UiController("sample_MainScreen")
@UiDescriptor("sample-main-screen.xml")
@LoadDataBeforeShow
public class SampleMainScreen extends Screen implements Window.HasWorkArea {

    protected static final String APP_LOGO_IMAGE = "application.logoImage";

    public SampleMainScreen() {
        addInitListener(this::initComponents);
    }

    protected void initComponents(@SuppressWarnings("unused") InitEvent e) {
        initLogoImage();
        initUserIndicator();
        initMenu();
        initLayoutAnalyzerContextMenu();
    }

    protected void initUserIndicator() {
        UserIndicator userIndicator = getUserIndicator();
        if (userIndicator != null) {
            boolean authenticated = AppUI.getCurrent().hasAuthenticatedSession();
            userIndicator.setVisible(authenticated);
        }
    }

    protected void initLogoImage() {
        Image logoImage = getLogoImage();
        String logoImagePath = getBeanLocator().get(Messages.class)
                .getMessage(APP_LOGO_IMAGE);

        if (logoImage != null
                && StringUtils.isNotBlank(logoImagePath)
                && !APP_LOGO_IMAGE.equals(logoImagePath)) {
            logoImage.setSource(ThemeResource.class).setPath(logoImagePath);
        }
    }

    protected void initLayoutAnalyzerContextMenu() {
        Image logoImage = getLogoImage();
        if (logoImage != null) {
            LayoutAnalyzerContextMenuProvider laContextMenuProvider =
                    getBeanLocator().get(LayoutAnalyzerContextMenuProvider.NAME);
            laContextMenuProvider.initContextMenu(this, logoImage);
        }
    }

    protected void initMenu() {
        Component menu = getAppMenu();
        if (menu != null) {
            ((Component.Focusable) menu).focus();
        }
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        Screens screens = UiControllerUtils.getScreenContext(this)
                .getScreens();
        getBeanLocator().get(ScreenTools.class)
                .openDefaultScreen(screens);
    }

    @Nullable
    @Override
    public AppWorkArea getWorkArea() {
        return (AppWorkArea) getWindow().getComponent("workArea");
    }

    @Nullable
    public UserIndicator getUserIndicator() {
        return (UserIndicator) getWindow().getComponent("userIndicator");
    }

    @Nullable
    protected Image getLogoImage() {
        return (Image) getWindow().getComponent("logoImage");
    }

    @Nullable
    protected AppMenu getAppMenu() {
        return (AppMenu) getWindow().getComponent("mainMenu");
    }
}
