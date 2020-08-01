package io.jmix.samples.ui.screen.sys.maindashboard;


import io.jmix.ui.component.Component;

import java.util.EventObject;

public class DashboardItemClickEvent extends EventObject {

    private final String menuItemId;

    public DashboardItemClickEvent(Component clickedComponent, String menuItemId) {
        super(clickedComponent);
        this.menuItemId = menuItemId;
    }

    @Override
    public Component getSource() {
        return (Component) super.getSource();
    }

    public String getMenuItemId() {
        return menuItemId;
    }
}
