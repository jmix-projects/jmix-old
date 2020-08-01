package io.jmix.samples.ui.screen.ui.dialogsandnotifications.dialog.message;

import io.jmix.ui.Dialogs;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.ContentMode;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("message-dialog")
@UiDescriptor("message-dialog.xml")
public class MessageDialogSample extends ScreenFragment {
    @Autowired
    protected Dialogs dialogs;

    @Subscribe("showPlainText")
    protected void onShowPlainTextClick(Button.ClickEvent event) {
        dialogs.createMessageDialog()
                .withCaption("Confirmation")
                .withMessage("You clicked the button")
                .show();
    }

    @Subscribe("showHtmlContent")
    protected void onShowHtmlContentClick(Button.ClickEvent event) {
        dialogs.createMessageDialog()
                .withCaption("Warning")
                .withMessage("<i>Something</i> <u>is</u> <b>wrong</b>")
                .withContentMode(ContentMode.HTML)
                .show();
    }
}