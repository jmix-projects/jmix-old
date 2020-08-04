package io.jmix.samples.ui.screen.ui.components.button.action;

import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("button-action")
@UiDescriptor("button-action.xml")
public class ButtonActionSample extends ScreenFragment {

    @Autowired
    protected Notifications notifications;

    @Subscribe("someAction")
    protected void onSomeActionActionPerformed(Action.ActionPerformedEvent event) {
        notifications.create()
                .withCaption("Action performed")
                .show();
    }
}
