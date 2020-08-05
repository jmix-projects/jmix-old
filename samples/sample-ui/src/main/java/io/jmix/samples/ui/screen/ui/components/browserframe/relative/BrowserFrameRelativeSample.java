package io.jmix.samples.ui.screen.ui.components.browserframe.relative;

import io.jmix.ui.component.BrowserFrame;
import io.jmix.ui.component.RelativePathResource;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("browserframe-relative")
@UiDescriptor("browserframe-relative.xml")
public class BrowserFrameRelativeSample extends ScreenFragment {

    @Autowired
    protected BrowserFrame browserFrame;

    @Subscribe
    protected void onInit(InitEvent event) {
        browserFrame.setSource(RelativePathResource.class)
                .setPath("VAADIN/themes/demo-helium/files/browserframe-relative.html");
    }
}
