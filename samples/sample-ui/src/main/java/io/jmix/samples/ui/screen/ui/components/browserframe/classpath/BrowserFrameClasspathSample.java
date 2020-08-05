package io.jmix.samples.ui.screen.ui.components.browserframe.classpath;

import io.jmix.ui.component.BrowserFrame;
import io.jmix.ui.component.ClasspathResource;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("browserframe-classpath")
@UiDescriptor("browserframe-classpath.xml")
public class BrowserFrameClasspathSample extends ScreenFragment {

    @Autowired
    protected BrowserFrame browserFrame;

    @Subscribe
    protected void onInit(InitEvent event) {
        browserFrame.setSource(ClasspathResource.class)
                .setPath("io/jmix/samples/ui/screen/ui/components/browserframe/classpath/browserframe-classpath.html");
    }
}
