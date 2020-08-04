package io.jmix.samples.ui.screen.ui.components.linkbutton;


import io.jmix.ui.Notifications;
import io.jmix.ui.component.Button;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("link-button")
@UiDescriptor("link-button.xml")
public class LinkButtonSample extends ScreenFragment {

    @Autowired
    protected Notifications notifications;

    @Subscribe("helloButton")
    protected void onHelloButtonClick(Button.ClickEvent event) {
        notifications.create()
                .withCaption("Hello, world!")
                .show();
    }

    @Subscribe("saveButton1")
    protected void onSaveButton1Click(Button.ClickEvent event) {
        save(event.getSource().getId());
    }

    @Subscribe("saveButton2")
    protected void onSaveButton2Click(Button.ClickEvent event) {
        save(event.getSource().getId());
    }

    public void save(String id) {
        notifications.create()
                .withCaption("Save called from " + id)
                .show();
    }
}