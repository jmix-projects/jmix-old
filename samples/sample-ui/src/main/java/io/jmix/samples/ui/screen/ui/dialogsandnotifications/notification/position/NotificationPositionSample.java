package io.jmix.samples.ui.screen.ui.dialogsandnotifications.notification.position;

import io.jmix.ui.Notifications;
import io.jmix.ui.component.Button;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("notification-position")
@UiDescriptor("notification-position.xml")
public class NotificationPositionSample extends ScreenFragment {

    @Autowired
    protected Notifications notifications;

    @Subscribe("defaultBtn")
    protected void onDefaultBtnClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Position: Default")
                .withPosition(Notifications.Position.DEFAULT)
                .show();
    }

    @Subscribe("topLeft")
    protected void onTopLeftClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Position: Top Left")
                .withPosition(Notifications.Position.TOP_LEFT)
                .show();
    }

    @Subscribe("topCenter")
    protected void onTopCenterClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Position: Top Center")
                .withPosition(Notifications.Position.TOP_CENTER)
                .show();
    }

    @Subscribe("topRight")
    protected void onTopRightClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Position: Top Right")
                .withPosition(Notifications.Position.TOP_RIGHT)
                .show();
    }

    @Subscribe("middleLeft")
    protected void onMiddleLeftClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Position: Middle Left")
                .withPosition(Notifications.Position.MIDDLE_LEFT)
                .show();
    }

    @Subscribe("middleCenter")
    protected void onMiddleCenterClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Position: Middle Center")
                .withPosition(Notifications.Position.MIDDLE_CENTER)
                .show();
    }

    @Subscribe("middleRight")
    protected void onMiddleRightClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Position: Middle Right")
                .withPosition(Notifications.Position.MIDDLE_RIGHT)
                .show();
    }

    @Subscribe("bottomLeft")
    protected void onBottomLeftClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Position: Bottom Left")
                .withPosition(Notifications.Position.BOTTOM_LEFT)
                .show();
    }

    @Subscribe("bottomCenter")
    protected void onBottomCenterClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Position: Bottom Center")
                .withPosition(Notifications.Position.BOTTOM_CENTER)
                .show();
    }

    @Subscribe("bottomRight")
    protected void onBottomRightClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Position: Bottom Right")
                .withPosition(Notifications.Position.BOTTOM_RIGHT)
                .show();
    }
}