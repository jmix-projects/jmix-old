package io.jmix.samples.ui.screen.ui.components.popupbutton.simple;

import io.jmix.core.Messages;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.component.PopupButton;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("popup-button-simple")
@UiDescriptor("popup-button-simple.xml")
public class PopupButtonSimpleSample extends ScreenFragment {

    @Autowired
    protected PopupButton popupButton3;

    @Autowired
    protected Notifications notifications;

    @Autowired
    protected Messages messages;

    @Subscribe
    protected void onInit(InitEvent event) {
        popupButton3.addAction(new BaseAction("saveAsDocAction")
                .withCaption(messages.getMessage(PopupButtonSimpleSample.class, "saveAsDocAction.caption"))
                .withHandler(actionPerformedEvent -> saveAsDoc()));

        popupButton3.addAction(new BaseAction("saveAsPdfAction")
                .withCaption(messages.getMessage(PopupButtonSimpleSample.class, "saveAsPdfAction.caption"))
                .withHandler(actionPerformedEvent -> saveAsPdf()));
    }

    @Subscribe("popupButton1.popupAction1")
    protected void onPopupButton1PopupAction1ActionPerformed(Action.ActionPerformedEvent event) {
        saveAsDoc();
    }

    @Subscribe("popupButton1.popupAction2")
    protected void onPopupButton1PopupAction2ActionPerformed(Action.ActionPerformedEvent event) {
        saveAsPdf();
    }

    @Subscribe("popupButton2.popupAction1")
    protected void onPopupButton2PopupAction1ActionPerformed(Action.ActionPerformedEvent event) {
        saveAsDoc();
    }

    @Subscribe("popupButton2.popupAction2")
    protected void onPopupButton2PopupAction2ActionPerformed(Action.ActionPerformedEvent event) {
        saveAsPdf();
    }

    public void saveAsDoc() {
        notifications.create()
                .withCaption("Saved as DOC")
                .show();
    }

    public void saveAsPdf() {
        notifications.create()
                .withCaption("Saved as PDF")
                .show();
    }
}