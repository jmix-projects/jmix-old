package io.jmix.samples.ui.screen.ui.dialogsandnotifications.notification.type;

import io.jmix.ui.Notifications;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.ContentMode;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("notification-type")
@UiDescriptor("notification-type.xml")
public class NotificationTypeSample extends ScreenFragment {

    @Autowired
    protected Notifications notifications;

    @Subscribe("tray")
    protected void onTrayClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Tray notification")
                .withType(Notifications.NotificationType.TRAY)
                .show();
    }

    @Subscribe("humanized")
    protected void onHumanizedClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Humanized notification")
                .withDescription("with description")
                .withType(Notifications.NotificationType.HUMANIZED)
                .show();
    }

    @Subscribe("warning")
    protected void onWarningClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("<i>Warning notification</i>")
                .withType(Notifications.NotificationType.WARNING)
                .withContentMode(ContentMode.HTML)
                .show();
    }

    @Subscribe("error")
    protected void onErrorClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("<code>Error notification</code>")
                .withDescription("<u>with description</u>")
                .withType(Notifications.NotificationType.ERROR)
                .withContentMode(ContentMode.HTML)
                .show();
    }

    @Subscribe("system")
    protected void onSystemClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("System notification")
                .withType(Notifications.NotificationType.SYSTEM)
                .show();
    }
}