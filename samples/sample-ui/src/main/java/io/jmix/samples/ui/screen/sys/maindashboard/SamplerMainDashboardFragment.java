package io.jmix.samples.ui.screen.sys.maindashboard;

import com.google.common.collect.Iterables;
import io.jmix.core.common.event.Subscription;
import io.jmix.samples.ui.config.MenuItem;
import io.jmix.samples.ui.config.SamplerMenuConfig;
import io.jmix.samples.ui.util.SamplerHelper;
import io.jmix.ui.Screens;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.component.ClasspathResource;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.CssLayout;
import io.jmix.ui.component.HBoxLayout;
import io.jmix.ui.component.Image;
import io.jmix.ui.component.Label;
import io.jmix.ui.component.LinkButton;
import io.jmix.ui.component.PopupView;
import io.jmix.ui.component.TextField;
import io.jmix.ui.screen.MessageBundle;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.ScreenOptions;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import io.jmix.ui.sys.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

@UiController("sampler-main-dashboard-fragment")
@UiDescriptor("sampler-main-dashboard-fragment.xml")
public class SamplerMainDashboardFragment extends ScreenFragment {

    public static final String MENU_ROOT_ITEM_ID = "mainMenuItemRootId";

    protected static final String GROUP_PANEL_STYLE = "group-panel";
    protected static final String GROUP_PANEL_ALIGN_STYLE = "group-panel-align";
    protected static final String GROUP_PANEL_ITEM_STYLE = "group-panel-item";
    protected static final String GROUP_PANEL_LABEL_STYLE = "group-panel-label";
    protected static final String LABEL_TITLE_STYLE = "label-title";
    protected static final String LABEL_IMAGE_STYLE = "label-image";

    protected static final String PLACEHOLDER_IMAGE_PATH = "io/jmix/samples/ui/images/dashboard/mainmenu/";

    @Autowired
    protected CssLayout cssLayout;
    @Autowired
    protected HBoxLayout historyBox;
    @Autowired
    protected HBoxLayout header;
    @Autowired
    protected PopupView permalink;
    @Autowired
    protected TextField<String> popupContent;

    @Autowired
    protected SamplerMenuConfig menuConfig;
    @Autowired
    protected SamplerHelper helper;
    @Autowired
    protected UiComponents uiComponents;
    @Autowired
    protected MessageBundle messageBundle;
    @Autowired
    protected Screens screens;

    @Subscribe
    protected void onInit(InitEvent event) {
        initDashboardMenu(MENU_ROOT_ITEM_ID);
    }

    public void initDashboardMenu(String menuItemRootId) {
        clearBreadCrumbs();
        List<MenuItem> menuItems;
        boolean isMainDashboard = menuItemRootId.equals(MENU_ROOT_ITEM_ID);
        if (isMainDashboard) {
            menuItems = menuConfig.getRootItems();

            // create label Title for main menu
            Label<String> label = createLabelTitle();
            label.setValue(messageBundle.getMessage("dashboard." + MENU_ROOT_ITEM_ID));
            cssLayout.add(label);

            header.setVisible(false);
            permalink.setPopupVisible(false);
        } else {
            menuItems = menuConfig.getAllChildrenAsList(menuItemRootId);
            header.setVisible(true);
            addNavigationButtonLink(menuItemRootId, false);
        }

        MenuItem lastMenuItem = null;
        for (MenuItem item : menuItems) {
            if (item.isMenu() && !isMainDashboard) {
                Label<String> title = createLabelTitle();
                String currentNameLabel = menuConfig.getMenuItemCaption(item.getId());
                if (lastMenuItem != null && lastMenuItem.isMenu()) {
                    //noinspection unchecked
                    Label<String> lastLabel = (Label<String>) Iterables.get(cssLayout.getComponents(),
                            cssLayout.getComponents().size() - 1);
                    lastLabel.setValue(currentNameLabel);
                } else {
                    title.setValue(currentNameLabel);
                    cssLayout.add(title);
                }
            } else {
                CssLayout wrapper = createCssWrapper(item.getId(), isMainDashboard);
                CssLayout itemLayout = createItemLayout(item.getId());
                Image image = createImagePreview(item.getImage());

                itemLayout.add(image);
                wrapper.add(itemLayout);
                cssLayout.add(wrapper);
            }
            lastMenuItem = item;
        }
    }

    protected CssLayout createCssWrapper(String itemId, boolean isMainDashboard) {
        CssLayout wrapper = uiComponents.create(CssLayout.class);
        wrapper.setStyleName(GROUP_PANEL_STYLE);
        wrapper.addStyleName(GROUP_PANEL_ALIGN_STYLE);
        wrapper.setWidth(Component.AUTO_SIZE);
        wrapper.setAlignment(Component.Alignment.MIDDLE_CENTER);

        wrapper.addLayoutClickListener(event -> {
            CssLayout groupBox = (CssLayout) event.getChildComponent();

            if (groupBox != null) {
                DashboardItemClickEvent clickEvent = new DashboardItemClickEvent(groupBox, groupBox.getId());
                getEventHub().publish(DashboardItemClickEvent.class, clickEvent);

                if (isMainDashboard) {
                    clearDashboard();
                    initDashboardMenu(groupBox.getId());
                } else {

                    ScreenOptions screenOptions = helper.getScreenOptions(menuConfig.getItemById(itemId));
                    screens.create(helper.getSampleBrowserId(), OpenMode.NEW_TAB, screenOptions)
                            .show();
                }
            }
        });
        return wrapper;
    }

    protected CssLayout createItemLayout(String itemId) {
        CssLayout groupBox = uiComponents.create(CssLayout.class);
        groupBox.setAlignment(Component.Alignment.MIDDLE_CENTER);
        groupBox.setId(itemId);
        groupBox.setHeight("280px");
        groupBox.setWidth("453px");
        groupBox.setStyleName(GROUP_PANEL_ITEM_STYLE);

        Label<String> caption = uiComponents.create(Label.TYPE_STRING);
        caption.setStyleName(GROUP_PANEL_LABEL_STYLE);
        caption.setValue(menuConfig.getMenuItemCaption(itemId));
        caption.setHeight("50px");
        caption.setWidth("100%");
        groupBox.add(caption);
        return groupBox;
    }

    protected Image createImagePreview(String imagePath) {
        Image image = uiComponents.create(Image.NAME);
        image.setStyleName(LABEL_IMAGE_STYLE);
        image.setHeight("208px");
        image.setWidth("431px");
        image.setSource(ClasspathResource.class)
                .setPath(PLACEHOLDER_IMAGE_PATH + imagePath);
        return image;
    }

    protected Label<String> createLabelTitle() {
        Label<String> title = uiComponents.create(Label.TYPE_STRING);
        title.setWidth("100%");
        title.setStyleName(LABEL_TITLE_STYLE);
        return title;
    }

    protected void addNavigationButtonLink(String itemId, boolean isMainDashboard) {
        LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setCaption(messageBundle.getMessage("dashboard.mainMenuItemRootId"));
        linkButton.setAction(new BaseAction("open") {
            @Override
            public void actionPerform(Component component) {
                LinkButton button = (LinkButton) Iterables.get(historyBox.getComponents(),
                        historyBox.getComponents().size() - 1);
                if (!button.getAction().equals(this)) {
                    clearDashboard();
                    initDashboardMenu(itemId);
                }
                DashboardItemClickEvent clickEvent = new DashboardItemClickEvent(linkButton, itemId);
                getEventHub().publish(DashboardItemClickEvent.class, clickEvent);

            }
        });
        if (!isMainDashboard) {
            Label<String> label = uiComponents.create(Label.TYPE_STRING);
            label.setValue(" > ");
            historyBox.add(label);
            linkButton.setCaption(menuConfig.getMenuItemCaption(itemId));
        }
        initPermalink(itemId);
        historyBox.add(linkButton);
    }

    protected void initPermalink(String frameId) {
        String value = ControllerUtils.getLocationWithoutParams() + "open?screen=" + frameId;
        popupContent.setValue(value);
        popupContent.selectAll();
        popupContent.setWidth((value.length() * 8) + "px");

        permalink.addPopupVisibilityListener(event -> {
            if (event.isPopupVisible()) {
                popupContent.focus();
            }
        });
    }

    public void clearDashboard() {
        cssLayout.removeAll();
    }

    protected void clearBreadCrumbs() {
        historyBox.removeAll();
        addNavigationButtonLink(MENU_ROOT_ITEM_ID, true);
    }

    public Subscription addDashboardItemClickListener(Consumer<DashboardItemClickEvent> listener) {
        return getEventHub().subscribe(DashboardItemClickEvent.class, listener);
    }
}
