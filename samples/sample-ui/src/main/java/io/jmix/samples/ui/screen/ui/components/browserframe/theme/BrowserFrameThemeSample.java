package io.jmix.samples.ui.screen.ui.components.browserframe.theme;

import io.jmix.ui.component.BrowserFrame;
import io.jmix.ui.component.ThemeResource;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("browserframe-theme")
@UiDescriptor("browserframe-theme.xml")
public class BrowserFrameThemeSample extends ScreenFragment {

    @Autowired
    protected BrowserFrame browserFrame;

    @Subscribe
    protected void onInit(InitEvent event) {
        browserFrame.setSource(ThemeResource.class)
                .setPath("files/browserframe-theme.html");
    }
}
