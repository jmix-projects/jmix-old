/*
 * Copyright 2020 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.auditui.screen.entityinspector;

import io.jmix.core.*;
import io.jmix.core.common.util.ParamsMap;
import io.jmix.core.entity.EntityValues;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.security.EntityOp;
import io.jmix.ui.Screens;
import io.jmix.ui.UiComponents;
import io.jmix.ui.UiProperties;
import io.jmix.ui.action.AbstractAction;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.component.*;
import io.jmix.ui.component.data.ValueSource;
import io.jmix.ui.component.data.table.ContainerTableItems;
import io.jmix.ui.component.data.value.ContainerValueSource;
import io.jmix.ui.model.*;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.jmix.auditui.screen.entityinspector.EntityFormUtils.*;
import static io.jmix.ui.component.Window.COMMIT_ACTION_ID;

@UiController("entityInspector.edit")
@UiDescriptor("entity-inspector-edit.xml")
public class EntityInspectorJmix extends Screen {

    public static final int MAX_TEXTFIELD_STRING_LENGTH = 255;
    public static final int CAPTION_MAX_LENGTH = 100;
    public static final int MAX_TEXT_LENGTH = 50;
    public static final Screens.LaunchMode OPEN_TYPE = OpenMode.THIS_TAB;

    @Autowired
    protected Metadata metadata;
    @Autowired
    protected MetadataTools metadataTools;
    @Autowired
    protected DataManager dataManager;
    @Autowired
    protected FetchPlanRepository fetchPlanRepository;
    @Autowired
    protected Messages messages;
    @Autowired
    protected MessageTools messageTools;
    @Autowired
    protected UiComponents uiComponents;
    @Autowired
    protected UiComponentsGenerator uiComponentsGenerator;
    @Autowired
    protected DataComponents dataComponents;
    @Autowired
    protected UiProperties uiProperties;
    @Autowired
    protected Screens screens;

    @Autowired
    protected EntityStates entityStates;

    @Autowired
    protected BoxLayout contentPane;

    @Autowired
    protected TabSheet tablesTabSheet;

    private DataContext dataContext;

    protected Entity item;
    protected Boolean isNew = false;
    protected MetaClass meta;

    protected Boolean autocommit;

    @Subscribe
    protected void onInit(InitEvent initEvent) {
        MapScreenOptions screenOptions = (MapScreenOptions) initEvent.getOptions();
        Map<String, Object> params = screenOptions.getParams();

        init(params);
        getOrCreateNewItem(params);
        setWindowCaption();
        initShortcuts();

        InstanceContainer container = initMainContainer(params);
        container.setItem(item);
        createForm(null, container);
    }

    private InstanceContainer initMainContainer(Map<String, Object> params) {
        return params.get("parentContainer") != null && params.get("parentProperty") != null ?
                dataComponents.createInstanceContainer(
                        item.getClass(), (InstanceContainer) params.get("parentContainer"), (String) params.get("parentProperty"))
                : dataComponents.createInstanceContainer(item.getClass());
    }

    @Subscribe("windowClose")
    protected void onWindowCloseActionPerformed(Action.ActionPerformedEvent event) {
        close(StandardOutcome.CLOSE);
    }

    @Subscribe("windowCommitAndClose")
    protected void onWindowCommitAndCloseActionPerformed(Action.ActionPerformedEvent event) {
        if (autocommit) {
            dataContext.commit();
        }
        close(StandardOutcome.COMMIT);
    }

    protected void initShortcuts() {
        Action commitAction = new BaseAction("commitAndClose")
                .withCaption(messages.getMessage("actions.OkClose"))
                .withShortcut(uiProperties.getCommitShortcut())
                .withHandler(e ->
                        onWindowCommitAndCloseActionPerformed(null)
                );
        getWindow().addAction(commitAction);
    }

    private void createForm(String caption, InstanceContainer container) {
        MetaClass metaClass = container.getEntityMetaClass();
        Entity item = container.getItem();

        Form form = uiComponents.create(Form.class);
        if (caption != null) {
            form.setCaption(caption);
        }

        contentPane.add(form);
        MetaProperty primaryKeyProperty = metadataTools.getPrimaryKeyProperty(metaClass);

        for (MetaProperty metaProperty : metaClass.getProperties()) {
            boolean isReadonly = metaProperty.isReadOnly();
            switch (metaProperty.getType()) {
                case DATATYPE:
                case ENUM:
                    boolean includeId = primaryKeyProperty.equals(metaProperty)
                            && String.class.equals(metaProperty.getJavaType());
                    //skip system properties
                    if (metadataTools.isSystem(metaProperty) && !includeId) {
                        continue;
                    }
                    if (metaProperty.getType() != MetaProperty.Type.ENUM
                            && (isByteArray(metaProperty) || isUuid(metaProperty))) {
                        continue;
                    }

                    if (includeId && !isNew) {
                        isReadonly = true;
                    }

                    addField(container, form, metaProperty, isReadonly);
                    break;
                case COMPOSITION:
                case ASSOCIATION:
                    if (metaProperty.getRange().getCardinality().isMany()) {
                        addTable(container, metaProperty);
                    } else {
                        if (isEmbedded(metaProperty)) {
                            Entity propertyValue = EntityValues.getValue(item, metaProperty.getName());
                            propertyValue = dataContext.merge(propertyValue);
                            InstanceContainer embeddedContainer = dataComponents.createInstanceContainer(
                                    item.getClass(), container, metaProperty.getName());
                            embeddedContainer.setItem(propertyValue);
                            createForm(getPropertyCaption(metaClass, metaProperty), embeddedContainer);
                        } else {
                            addField(container, form, metaProperty, isReadonly);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void init(Map<String, Object> params) {
        dataContext = dataComponents.createDataContext();
        autocommit = params.get("autocommit") != null ? (Boolean) params.get("autocommit") : true;
    }

    private void setWindowCaption() {
        getWindow().setCaption(messages.getMessage(meta.getJavaClass(), meta.getName()));
    }

    private void getOrCreateNewItem(Map<String, Object> params) {
        item = (Entity) params.get("item");
        isNew = item == null || entityStates.isNew(item);
        meta = item != null ? metadata.getClass(item) : metadata.getSession().getClass((String) params.get("metaClass"));

        if (item == null) {
            item = metadata.create(meta);
            if (params.get("parent") != null && params.get("parentProperty") != null) {
                EntityValues.setValue(item, (String) params.get("parentProperty"), params.get("parent"));
            }
        } else {
            Object itemId = Id.of(item).getValue();
            if (!isNew) {
                item = loadSingleItem(meta, itemId, createView(meta));
            }
            if (item == null) {
                throw new EntityAccessException(meta, itemId);
            }
        }
        item = dataContext.merge(item);
    }

    /**
     * Loads single item by id.
     *
     * @param meta      item's meta class
     * @param id        item's id
     * @param fetchPlan fetchPlan
     * @return loaded item if found, null otherwise
     */
    protected Entity loadSingleItem(MetaClass meta, Object id, FetchPlan fetchPlan) {
        String primaryKeyName = metadataTools.getPrimaryKeyName(meta);
        if (primaryKeyName == null) {
            throw new IllegalStateException(String.format("Entity %s has no primary key", meta.getName()));
        }

        LoadContext ctx = new LoadContext(meta);
        ctx.setLoadDynamicAttributes(true);
        ctx.setSoftDeletion(false);
        ctx.setFetchPlan(fetchPlan);

        String query = String.format("select e from %s e where e.%s = :id", meta.getName(), primaryKeyName);
        LoadContext.Query q = ctx.setQueryString(query);
        q.setParameter("id", id);
        return dataManager.load(ctx);
    }

    /**
     * Creates a view, loading all the properties.
     * Referenced entities will be loaded with a LOCAL view.
     *
     * @param meta meta class
     * @return View instance
     */
    protected FetchPlan createView(MetaClass meta) {
        FetchPlan fetchPlan = new FetchPlan(meta.getJavaClass(), false);
        for (MetaProperty metaProperty : meta.getProperties()) {
            switch (metaProperty.getType()) {
                case DATATYPE:
                case ENUM:
                    fetchPlan.addProperty(metaProperty.getName());
                    break;
                case ASSOCIATION:
                case COMPOSITION:
                    MetaClass metaPropertyClass = metaProperty.getRange().asClass();
                    String metaPropertyClassName = metaPropertyClass.getName();

                    if (metadataTools.isEmbedded(metaProperty)) {
                        FetchPlan embeddedViewWithRelations = createEmbeddedPlan(metaPropertyClass);
                        fetchPlan.addProperty(metaProperty.getName(), embeddedViewWithRelations);
                    } else {
                        String viewName;
                        if (metaProperty.getRange().getCardinality().isMany()) {
                            viewName = FetchPlan.LOCAL;
                        } else {
                            viewName = FetchPlan.MINIMAL;
                        }
                        FetchPlan propView = fetchPlanRepository.getFetchPlan(metaPropertyClass, viewName);
                        fetchPlan.addProperty(metaProperty.getName(),
                                new FetchPlan(propView, metaPropertyClassName + ".entity-inspector-fetchPlan", true));
                    }
                    break;
                default:
                    throw new IllegalStateException("unknown property type");
            }
        }
        return fetchPlan;
    }

    protected FetchPlan createEmbeddedPlan(MetaClass metaPropertyClass) {
        FetchPlan propView = fetchPlanRepository.getFetchPlan(metaPropertyClass, FetchPlan.BASE);
        FetchPlan embeddedViewWithRelations = new FetchPlan(propView, metaPropertyClass.getName() + ".entity-inspector-view", true);

        // iterate embedded properties and add relations with MINIMAL view
        for (MetaProperty embeddedNestedProperty : metaPropertyClass.getProperties()) {
            if (embeddedNestedProperty.getRange().isClass() &&
                    !embeddedNestedProperty.getRange().getCardinality().isMany()) {
                FetchPlan embeddedRelationView = fetchPlanRepository.getFetchPlan(
                        embeddedNestedProperty.getRange().asClass(), FetchPlan.MINIMAL);

                embeddedViewWithRelations.addProperty(embeddedNestedProperty.getName(), embeddedRelationView);
            }
        }

        return embeddedViewWithRelations;
    }

    /**
     * Adds field to the specified field group.
     * If the field should be custom, adds it to the specified customFields collection
     * which can be used later to create fieldGenerators
     *
     * @param metaProperty meta property of the item's property which field is creating
     * @param form         field group to which created field will be added
     */
    protected void addField(InstanceContainer container, Form form, MetaProperty metaProperty, boolean isReadonly) {
        MetaClass metaClass = container.getEntityMetaClass();
        boolean isRequired = isRequired(metaProperty);
        if (!attrViewPermitted(metaClass, metaProperty))
            return;

        if ((metaProperty.getType() == MetaProperty.Type.COMPOSITION
                || metaProperty.getType() == MetaProperty.Type.ASSOCIATION)
                && !entityOpPermitted(metaProperty.getRange().asClass(), EntityOp.READ))
            return;

        ValueSource valueSource = new ContainerValueSource<>(container, metaProperty.getName());

        ComponentGenerationContext componentContext =
                new ComponentGenerationContext(metaClass, metaProperty.getName());
        componentContext.setValueSource(valueSource);

        Field field = (Field) uiComponentsGenerator.generate(componentContext);

        if (requireTextArea(metaProperty, item, MAX_TEXTFIELD_STRING_LENGTH)) {
            field = uiComponents.create(TextArea.NAME);
        }

        if (isBoolean(metaProperty)) {
            field = createBooleanField();
        }

        field.setValueSource(valueSource);
        field.setCaption(getPropertyCaption(metaClass, metaProperty));
        field.setRequired(isRequired);

        if (metaProperty.getRange().isClass() && !metadataTools.isEmbedded(metaProperty)) {
            field.setEditable(metadataTools.isOwningSide(metaProperty) && !isReadonly);
        } else {
            field.setEditable(!isReadonly);
        }

        field.setWidth("400px");

        if (isRequired) {
            field.setRequiredMessage(messageTools.getDefaultRequiredMessage(metaClass, metaProperty.getName()));
        }
        form.add(field);
    }

    /**
     * Creates a table for the entities in ONE_TO_MANY or MANY_TO_MANY relation with the current one
     */
    protected void addTable(InstanceContainer parent, MetaProperty childMeta) {
        MetaClass meta = childMeta.getRange().asClass();

        //don't show empty table if the user don't have permissions on the attribute or the entity
        if (!attrViewPermitted(parent.getEntityMetaClass(), childMeta) ||
                !entityOpPermitted(meta, EntityOp.READ)) {
            return;
        }

        //don't show table on new master item, because an exception occurred on safe new item in table
        if (isNew && childMeta.getType().equals(MetaProperty.Type.ASSOCIATION)) {
            return;
        }

        //vertical box for the table and its label
        BoxLayout vbox = uiComponents.create(VBoxLayout.class);
        vbox.setWidth("100%");
        vbox.setHeight("100%");
//        CollectionLoader loader = dataComponents.createCollectionLoader();
        CollectionContainer container = dataComponents.createCollectionContainer(meta.getJavaClass(), parent, childMeta.getName());
//        loader.setContainer(container);
        //TODO replace to query
//        loader.setLoadDelegate(loadContext -> new ArrayList<>(EntityValues.getValue(parent.getItem(), childMeta.getName())));

        Table<?> table = uiComponents.create(Table.NAME);
        table.setMultiSelect(true);

        //place non-system properties columns first
        List<Table.Column> nonSystemPropertyColumns = new ArrayList<>();
        List<Table.Column> systemPropertyColumns = new ArrayList<>();
        for (MetaProperty metaProperty : meta.getProperties()) {
            if (metaProperty.getRange().isClass() || isRelatedToNonLocalProperty(metaProperty))
                continue; // because we use local views
            Table.Column column = new Table.Column(meta.getPropertyPath(metaProperty.getName()));
            if (!metadataTools.isSystem(metaProperty)) {
                column.setCaption(getPropertyCaption(meta, metaProperty));
                nonSystemPropertyColumns.add(column);
            } else {
                column.setCaption(metaProperty.getName());
                systemPropertyColumns.add(column);
            }
            if (metaProperty.getJavaType().equals(String.class)) {
                column.setMaxTextLength(MAX_TEXT_LENGTH);
            }
        }
        for (Table.Column column : nonSystemPropertyColumns) {
            table.addColumn(column);
        }

        for (Table.Column column : systemPropertyColumns) {
            table.addColumn(column);
        }

        table.setItems(new ContainerTableItems(container));

        ButtonsPanel propertyButtonsPanel = createButtonsPanel(childMeta, container, table);
        table.setButtonsPanel(propertyButtonsPanel);

        //TODO supports paging
//        if (propertyDs instanceof CollectionDatasource.SupportsPaging) {
//            RowsCount rowsCount = uiComponents.create(RowsCount.class);
//            rowsCount.setDatasource(propertyDs);
//            table.setRowsCount(rowsCount);
//        }

        table.setWidth("100%");

        vbox.add(table);
        vbox.expand(table);
        vbox.setMargin(true);

        TabSheet.Tab tab = tablesTabSheet.addTab(childMeta.toString(), vbox);
        tab.setCaption(getPropertyCaption(parent.getEntityMetaClass(), childMeta));
    }

    private Field createBooleanField() {
        LookupField field = uiComponents.create(LookupField.NAME);
        field.setOptionsMap(ParamsMap.of(
                messages.getMessage("trueString"), Boolean.TRUE,
                messages.getMessage("falseString"), Boolean.FALSE));
        field.setTextInputAllowed(false);
        return field;
    }

    protected String getPropertyCaption(MetaClass metaClass, MetaProperty metaProperty) {
        String caption = messageTools.getPropertyCaption(metaClass, metaProperty.getName());
        if (caption.length() < CAPTION_MAX_LENGTH)
            return caption;
        else
            return caption.substring(0, CAPTION_MAX_LENGTH);
    }


    /**
     * Determine whether the given metaProperty relates to at least one non local property
     */
    protected boolean isRelatedToNonLocalProperty(MetaProperty metaProperty) {
        MetaClass metaClass = metaProperty.getDomain();
        for (String relatedProperty : metadataTools.getRelatedProperties(metaProperty)) {
            //noinspection ConstantConditions
            if (metaClass.getProperty(relatedProperty).getRange().isClass()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a buttons panel managing table's content.
     *
     * @param metaProperty property representing table's data
     * @param propertyDs   property's Datasource (CollectionPropertyDatasource usually)
     * @param table        table
     * @return buttons panel
     */
    protected ButtonsPanel createButtonsPanel(MetaProperty metaProperty,
                                              CollectionContainer container, Table table) {
        MetaClass propertyMetaClass = metaProperty.getRange().asClass();
        ButtonsPanel propertyButtonsPanel = uiComponents.create(ButtonsPanel.class);
        Button createButton = uiComponents.create(Button.class);
        CreateAction createAction = new CreateAction(metaProperty, container, propertyMetaClass);
        createButton.setAction(createAction);
        table.addAction(createAction);
        createButton.setCaption(messages.getMessage(EntityInspectorEditor.class, "create"));
        createButton.setIcon("icons/create.png");

        propertyButtonsPanel.add(createButton);
        return propertyButtonsPanel;
    }

    public Entity getItem() {
        return item;
    }

    /**
     * Opens entity inspector's editor to create entity
     */
    protected class CreateAction extends AbstractAction {

        private CollectionContainer container;
        private MetaClass entityMeta;
        protected MetaProperty metaProperty;

        protected CreateAction(MetaProperty metaProperty, CollectionContainer container, MetaClass metaClass) {
            super("create");
            this.container = container;
            this.entityMeta = metaClass;
            this.metaProperty = metaProperty;
            setShortcut(uiProperties.getTableInsertShortcut());
        }

        @Override
        @SuppressWarnings("unchecked")
        public void actionPerform(Component component) {
            Map<String, Object> editorParams = new HashMap<>();
            editorParams.put("metaClass", entityMeta.getName());
            editorParams.put("autocommit", Boolean.FALSE);
            MetaProperty inverseProperty = metaProperty.getInverse();
            if (inverseProperty != null) {
                editorParams.put("parentProperty", inverseProperty.getName());
            }
            editorParams.put("parent", item);
            if (metaProperty.getType() == MetaProperty.Type.COMPOSITION) {
                editorParams.put("parentContainer", container);
            }

            screens.create(EntityInspectorEditor.class, OPEN_TYPE, new MapScreenOptions(editorParams))
                    .show()
                    .addAfterCloseListener(afterCloseEvent -> {
                        if (COMMIT_ACTION_ID.equals(((StandardCloseAction) afterCloseEvent.getCloseAction()).getActionId())
                                && metaProperty.getType() == MetaProperty.Type.ASSOCIATION) {
                            EntityInspectorEditor screen = (EntityInspectorEditor) afterCloseEvent.getScreen();
                            container.getMutableItems().add(screen.getItem());
                        }
                    });

        }
    }

}
