package io.jmix.samples.ui.screen.ui.components.button.programmaticaction;

import io.jmix.ui.Notifications;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.component.Button;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("button-programmatic-action")
@UiDescriptor("button-programmatic-action.xml")
public class ButtonProgrammaticActionSample extends ScreenFragment {

    @Autowired
    protected Button button;

    @Autowired
    protected Notifications notifications;

    @Subscribe
    protected void onInit(InitEvent event) {
        button.setAction(new BaseAction("theAction")
                .withCaption("Click Me!")
                .withHandler(actionPerformedEvent ->
                        notifications.create()
                                .withCaption("Action performed")
                                .show()));
    }
}