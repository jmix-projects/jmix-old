package io.jmix.samples.helloworld.screen.main;

import com.vaadin.server.WebBrowser;
import io.jmix.core.Messages;
import io.jmix.ui.*;
import io.jmix.ui.components.*;
import io.jmix.ui.components.dev.LayoutAnalyzerContextMenuProvider;
import io.jmix.ui.components.mainwindow.AppMenu;
import io.jmix.ui.components.mainwindow.SideMenu;
import io.jmix.ui.components.mainwindow.UserIndicator;
import io.jmix.ui.screen.*;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

import static io.jmix.ui.components.ComponentsHelper.setStyleName;

@UiController("sample_MainScreen")
@UiDescriptor("sample-main-screen.xml")
@LoadDataBeforeShow
public class SampleMainScreen extends Screen implements Window.HasWorkArea {

    public static final String SIDEMENU_COLLAPSED_STATE = "sidemenuCollapsed";
    public static final String SIDEMENU_COLLAPSED_STYLENAME = "collapsed";

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
        if (menu == null) {
            menu = getSideMenu();
        }

        if (menu != null) {
            ((Component.Focusable) menu).focus();
        }

        initCollapsibleMenu();
    }

    protected void initCollapsibleMenu() {
        Component sideMenuContainer = getWindow().getComponent("sideMenuContainer");
        if (sideMenuContainer instanceof CssLayout) {
            if (isMobileDevice()) {
                setSideMenuCollapsed(true);
            } else {
                String menuCollapsedCookie = App.getInstance()
                        .getCookieValue(SIDEMENU_COLLAPSED_STATE);

                boolean menuCollapsed = Boolean.parseBoolean(menuCollapsedCookie);

                setSideMenuCollapsed(menuCollapsed);
            }

            initCollapseMenuControls();
        }
    }

    protected void initCollapseMenuControls() {
        Button collapseMenuButton = getCollapseMenuButton();
        if (collapseMenuButton != null) {
            collapseMenuButton.addClickListener(event ->
                    setSideMenuCollapsed(!isMenuCollapsed()));
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
    protected Button getCollapseMenuButton() {
        return (Button) getWindow().getComponent("collapseMenuButton");
    }

    @Nullable
    protected Image getLogoImage() {
        return (Image) getWindow().getComponent("logoImage");
    }

    @Nullable
    protected AppMenu getAppMenu() {
        return (AppMenu) getWindow().getComponent("appMenu");
    }

    @Nullable
    protected SideMenu getSideMenu() {
        return (SideMenu) getWindow().getComponent("sideMenu");
    }

    protected void setSideMenuCollapsed(boolean collapsed) {
        Component sideMenuContainer = getWindow().getComponent("sideMenuContainer");
        CssLayout sideMenuPanel = (CssLayout) getWindow().getComponent("sideMenuPanel");
        Button collapseMenuButton = getCollapseMenuButton();

        setStyleName(sideMenuContainer, SIDEMENU_COLLAPSED_STYLENAME, collapsed);
        setStyleName(sideMenuPanel, SIDEMENU_COLLAPSED_STYLENAME, collapsed);

        if (collapseMenuButton != null) {
            Messages messages = getBeanLocator().get(Messages.class);
            if (collapsed) {
                collapseMenuButton.setCaption(messages.getMessage("menuExpandGlyph"));
                collapseMenuButton.setDescription(messages.getMessage("sideMenuExpand"));
            } else {
                collapseMenuButton.setCaption(messages.getMessage("menuCollapseGlyph"));
                collapseMenuButton.setDescription(messages.getMessage("sideMenuCollapse"));
            }
        }

        App.getInstance()
                .addCookie(SIDEMENU_COLLAPSED_STATE, String.valueOf(collapsed));
    }

    protected boolean isMenuCollapsed() {
        CssLayout sideMenuPanel = (CssLayout) getWindow().getComponent("sideMenuPanel");
        return sideMenuPanel != null
                && sideMenuPanel.getStyleName() != null
                && sideMenuPanel.getStyleName().contains(SIDEMENU_COLLAPSED_STYLENAME);
    }

    protected boolean isMobileDevice() {
        WebBrowser browser = AppUI.getCurrent()
                .getPage()
                .getWebBrowser();

        return browser.getScreenWidth() < 500
                || browser.getScreenHeight() < 800;
    }

}
