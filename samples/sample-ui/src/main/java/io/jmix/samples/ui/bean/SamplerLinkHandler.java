package io.jmix.samples.ui.bean;

import io.jmix.samples.ui.config.MenuItem;
import io.jmix.samples.ui.config.SamplerMenuConfig;
import io.jmix.samples.ui.screen.sys.main.SamplerMainScreen;
import io.jmix.samples.ui.util.SamplerHelper;
import io.jmix.ui.App;
import io.jmix.ui.AppUI;
import io.jmix.ui.Screens;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.ScreenOptions;
import io.jmix.ui.sys.LinkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.jmix.ui.screen.FrameOwner.WINDOW_CLOSE_ACTION;

public class SamplerLinkHandler extends LinkHandler {

    private static final Logger log = LoggerFactory.getLogger(SamplerLinkHandler.class);

    @Autowired
    protected SamplerHelper samplerHelper;
    @Autowired
    protected SamplerMenuConfig samplerMenuConfig;

    public SamplerLinkHandler(App app, String action, Map<String, String> requestParams) {
        super(app, action, requestParams);
    }

    @Override
    public void handle() {
        String screenName = requestParams.get("screen");
        if (screenName == null) {
            log.warn("ScreenId not found in request parameters");
            return;
        }
        // clear requestParams, because after refreshing page this screen always shown
        requestParams.clear();

        MenuItem item = samplerMenuConfig.findItemById(screenName);
        if (item == null) {
            super.handle();
            return;
        }

        AppUI ui = AppUI.getCurrent();
        if (ui == null) {
            return;
        }

        Screens.OpenedScreens openedScreens = ui.getScreens().getOpenedScreens();
        Screen rootScreen = openedScreens.getRootScreen();
        if (!item.isMenu()) {
            ScreenOptions screenOptions = samplerHelper.getScreenOptions(item);
            ui.getScreens()
                    .create(samplerHelper.getSampleBrowserId(), OpenMode.NEW_TAB, screenOptions)
                    .show();
            if (rootScreen instanceof SamplerMainScreen) {
                ((SamplerMainScreen) rootScreen).expandItemsFromDirectLink(item.getId());
            }
        } else if (samplerMenuConfig.isRootItem(item.getId())) {
            if (rootScreen instanceof SamplerMainScreen) {
                openedScreens.getAll().forEach(screen -> screen.close(WINDOW_CLOSE_ACTION));
                ((SamplerMainScreen) rootScreen).initDashboardByRootItemId(item.getId());
                ((SamplerMainScreen) rootScreen).expandItemsFromDirectLink(item.getId());
            }
        }
    }
}
