package io.jmix.samples.ui.screen.ui.dialogsandnotifications.dialog.option;

import io.jmix.ui.Dialogs;
import io.jmix.ui.Notifications;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.action.DialogAction;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.ContentMode;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("option-dialog")
@UiDescriptor("option-dialog.xml")
public class OptionDialogSample extends ScreenFragment {
    @Autowired
    protected Dialogs dialogs;
    @Autowired
    protected Notifications notifications;

    @Subscribe("okAndCancel")
    protected void onOkAndCancelClick(Button.ClickEvent event) {
        dialogs.createOptionDialog()
                .withCaption("Title")
                .withMessage("Message")
                .withActions(
                        new DialogAction(DialogAction.Type.OK)
                                .withHandler(e ->
                                        notifications.create()
                                                .withCaption("OK pressed")
                                                .show()
                                ),
                        new DialogAction(DialogAction.Type.CANCEL))
                .show();
    }

    @Subscribe("allActions")
    protected void onAllActionsClick(Button.ClickEvent event) {
        dialogs.createOptionDialog()
                .withCaption("Title")
                .withMessage("All available DialogActions")
                .withWidth("500px")
                .withActions(
                        new DialogAction(DialogAction.Type.OK),
                        new DialogAction(DialogAction.Type.CANCEL),
                        new DialogAction(DialogAction.Type.YES),
                        new DialogAction(DialogAction.Type.NO),
                        new DialogAction(DialogAction.Type.CLOSE))
                .show();
    }

    @Subscribe("customAction")
    protected void onCustomActionClick(Button.ClickEvent event) {
        dialogs.createOptionDialog()
                .withCaption("Title")
                .withMessage("Message")
                .withContentMode(ContentMode.HTML)
                .withActions(
                        new BaseAction("customAction")
                                .withCaption("Do something")
                                .withHandler(e ->
                                        notifications.create()
                                                .withCaption("Done")
                                                .show()
                                ),
                        new DialogAction(DialogAction.Type.CANCEL))
                .show();
    }
}