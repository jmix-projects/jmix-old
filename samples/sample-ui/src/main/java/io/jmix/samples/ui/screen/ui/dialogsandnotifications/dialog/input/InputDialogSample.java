package io.jmix.samples.ui.screen.ui.dialogsandnotifications.dialog.input;

import com.google.common.base.Strings;
import io.jmix.core.DataManager;
import io.jmix.core.MetadataTools;
import io.jmix.samples.ui.entity.Customer;
import io.jmix.samples.ui.entity.CustomerGrade;
import io.jmix.ui.Dialogs;
import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.app.inputdialog.DialogActions;
import io.jmix.ui.app.inputdialog.InputDialog;
import io.jmix.ui.app.inputdialog.InputParameter;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.ComboBox;
import io.jmix.ui.component.ContentMode;
import io.jmix.ui.component.ValidationErrors;
import io.jmix.ui.component.inputdialog.InputDialogAction;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("input-dialog")
@UiDescriptor("input-dialog.xml")
public class InputDialogSample extends ScreenFragment {

    @Autowired
    protected Dialogs dialogs;
    @Autowired
    protected Notifications notifications;
    @Autowired
    protected UiComponents uiComponents;
    @Autowired
    protected MetadataTools metadataTools;
    @Autowired
    protected DataManager dataManager;

    @Subscribe("standardDialogBtn")
    protected void onStandardDialogBtnClick(Button.ClickEvent event) {
        dialogs.createInputDialog(this)
                .withCaption("Enter values")
                .withParameters(
                        InputParameter.stringParameter("name")
                                .withCaption("Name").withRequired(true),
                        InputParameter.doubleParameter("quantity")
                                .withCaption("Quantity").withDefaultValue(1.0),
                        InputParameter.entityParameter("customer", Customer.class)
                                .withCaption("Customer"),
                        InputParameter.enumParameter("grade", CustomerGrade.class)
                                .withCaption("Grade")
                )
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(closeEvent -> {
                    if (closeEvent.getCloseAction().equals(InputDialog.INPUT_DIALOG_OK_ACTION)) {
                        String name = closeEvent.getValue("name");
                        Double quantity = closeEvent.getValue("quantity");
                        Customer customer = closeEvent.getValue("customer");
                        CustomerGrade grade = closeEvent.getValue("grade");

                        notifications.create()
                                .withCaption("Entered Values")
                                .withDescription("<strong>Name:</strong> " + name +
                                        "<br/><strong>Quantity:</strong> " + quantity +
                                        "<br/><strong>Customer:</strong> " + metadataTools.format(customer) +
                                        "<br/><strong>Grade:</strong> " + metadataTools.format(grade))
                                .withContentMode(ContentMode.HTML)
                                .show();
                    }
                })
                .show();
    }

    @Subscribe("customParameterBtn")
    protected void onCustomParameterBtnClick(Button.ClickEvent event) {
        dialogs.createInputDialog(this)
                .withCaption("Enter values")
                .withParameters(
                        InputParameter.stringParameter("name").withCaption("Name"),
                        InputParameter.parameter("customer")
                                .withField(() -> {
                                    ComboBox<Customer> field = uiComponents.create(ComboBox.of(Customer.class));
                                    field.setOptionsList(dataManager.load(Customer.class).list());
                                    field.setCaption("Customer");
                                    field.setWidthFull();
                                    return field;
                                })
                )
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(closeEvent -> {
                    if (closeEvent.getCloseAction().equals(InputDialog.INPUT_DIALOG_OK_ACTION)) {
                        String name = closeEvent.getValue("name");
                        Customer customer = closeEvent.getValue("customer");

                        notifications.create()
                                .withCaption("Entered Values")
                                .withDescription("<strong>Name:</strong> " + name +
                                        "<br/><strong>Customer:</strong> " + metadataTools.format(customer))
                                .withContentMode(ContentMode.HTML)
                                .show();
                    }
                })
                .show();
    }

    @Subscribe("customActionsBtn")
    protected void onCustomActionsBtnClick(Button.ClickEvent event) {
        dialogs.createInputDialog(this)
                .withCaption("Enter values")
                .withParameters(
                        InputParameter.stringParameter("name").withCaption("Name")
                )
                .withActions(
                        InputDialogAction.action("confirm")
                                .withCaption("Confirm")
                                .withPrimary(true)
                                .withHandler(actionEvent -> {
                                    InputDialog dialog = actionEvent.getInputDialog();
                                    String name = dialog.getValue("name");
                                    dialog.closeWithDefaultAction();

                                    notifications.create()
                                            .withCaption("Entered Values")
                                            .withDescription("<strong>Name:</strong> " + name)
                                            .withContentMode(ContentMode.HTML)
                                            .show();
                                }),
                        InputDialogAction.action("refuse")
                                .withCaption("Refuse")
                                .withValidationRequired(false)
                                .withHandler(actionEvent ->
                                        actionEvent.getInputDialog().closeWithDefaultAction())
                )
                .show();
    }

    @Subscribe("validationBtn")
    protected void onValidationBtnClick(Button.ClickEvent event) {
        dialogs.createInputDialog(this)
                .withCaption("Enter values")
                .withParameters(
                        InputParameter.stringParameter("name").withCaption("Name"),
                        InputParameter.entityParameter("customer", Customer.class).withCaption("Customer")
                )
                .withValidator(context -> {
                    String name = context.getValue("name");
                    Customer customer = context.getValue("customer");
                    if (Strings.isNullOrEmpty(name) && customer == null) {
                        return ValidationErrors.of("Enter name or select a customer");
                    }
                    return ValidationErrors.none();
                })
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(closeEvent -> {
                    if (closeEvent.getCloseAction().equals(InputDialog.INPUT_DIALOG_OK_ACTION)) {
                        String name = closeEvent.getValue("name");
                        Customer customer = closeEvent.getValue("customer");

                        notifications.create()
                                .withCaption("Entered Values")
                                .withDescription("<strong>Name:</strong> " + name +
                                        "<br/><strong>Customer:</strong> " + metadataTools.format(customer))
                                .withContentMode(ContentMode.HTML)
                                .show();
                    }
                })
                .show();
    }
}