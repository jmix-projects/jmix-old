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
import io.jmix.core.entity.SoftDelete;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.metamodel.model.Range;
import io.jmix.core.metamodel.model.Session;
import io.jmix.core.security.EntityOp;
import io.jmix.core.security.Security;
import io.jmix.ui.Screens;
import io.jmix.ui.UiComponents;
import io.jmix.ui.UiProperties;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.action.ItemTrackingAction;
import io.jmix.ui.action.list.RefreshAction;
import io.jmix.ui.action.list.RemoveAction;
import io.jmix.ui.component.*;
import io.jmix.ui.component.data.table.ContainerTableItems;
import io.jmix.ui.export.ExportFormat;
import io.jmix.ui.icon.Icons;
import io.jmix.ui.icon.JmixIcon;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.DataComponents;
import io.jmix.ui.screen.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

import static io.jmix.ui.export.ExportFormat.JSON;
import static io.jmix.ui.export.ExportFormat.ZIP;

@UiController("entityInspector.browse")
@UiDescriptor("entity-inspector-browser.xml")
public class EntityInspectorBrowser extends StandardLookup<Entity> {

    public static final int MAX_TEXT_LENGTH = 50;

    protected static final Logger log = LoggerFactory.getLogger(EntityInspectorBrowser.class);

    @Inject
    protected Messages messages;
    @Inject
    protected Screens screens;
    @Inject
    protected Metadata metadata;
    @Inject
    protected MetadataTools metadataTools;
    @Inject
    protected MessageTools messageTools;
    @Inject
    protected Security security;
    @Inject
    protected DataComponents dataComponents;
    @Inject
    protected FetchPlanRepository fetchPlanRepository;

    @Inject
    protected BoxLayout lookupBox;
    @Inject
    protected BoxLayout tableBox;

    @Inject
    protected UiComponents uiComponents;

    @Inject
    protected UiProperties uiProperties;

    @Inject
    protected LookupField<MetaClass> entitiesLookup;

    @Inject
    protected CheckBox removedRecords;

    @Inject
    protected CheckBox textSelection;

    @Inject
    protected BoxLayout filterBox;

    //TODO Import/export service
//    @Inject
//    protected EntityImportExportService entityImportExportService;

//    @Inject
//    protected ExportDisplay exportDisplay;

    //TODO file upload API (File storage API and UI components #103)
//    @Inject
//    protected FileUploadingAPI fileUploadingAPI;

    @Inject
    protected Icons icons;

    //TODO filter implementation component (Filter in Table/DataGrid #221)
    protected Component filter;
    protected Table entitiesTable;

    protected Button createButton;
    protected Button editButton;
    protected Button removeButton;
    protected Button excelButton;
    protected Button refreshButton;
    protected FileUploadField importUpload;
    protected PopupButton exportPopupButton;

    protected CollectionContainer<Entity> entitiesDc;
    protected CollectionLoader<Entity> entitiesDl;

    protected MetaClass selectedMeta;

    @Subscribe
    protected void onInit(InitEvent initEvent) {
        Map<String, Object> params = new HashMap<>(0);
        ScreenOptions options = initEvent.getOptions();
        if (options instanceof MapScreenOptions) {
            params = ((MapScreenOptions) options).getParams();
        }
        String entityName = (String) params.get("entity");
        if (entityName != null) {
            Session session = metadata.getSession();
            selectedMeta = session.getClass(entityName);
            createEntitiesTable(selectedMeta);

            lookupBox.setVisible(false);
        } else {
            entitiesLookup.setOptionsMap(getEntitiesLookupFieldOptions());
            entitiesLookup.addValueChangeListener(e -> showEntities());
            removedRecords.addValueChangeListener(e -> showEntities());
        }
    }

    //TODO Lookup component
//    @Override
//    public void setSelectHandler(Consumer lookupHandler) {
//        super.setSelectHandler(lookupHandler);
//
//        setLookupComponent(entitiesTable);
//
//        Action selectAction = getAction(LOOKUP_SELECT_ACTION_ID);
//        entitiesTable.setLookupSelectHandler(items ->
//                selectAction.actionPerform(entitiesTable)
//        );
//    }

    protected Map<String, MetaClass> getEntitiesLookupFieldOptions() {
        Map<String, MetaClass> options = new TreeMap<>();

        for (MetaClass metaClass : metadata.getClasses()) {
            if (readPermitted(metaClass)) {
                Class javaClass = metaClass.getJavaClass();
                if (Entity.class.isAssignableFrom(javaClass) && isNotAbstract(javaClass)) {
                    options.put(messageTools.getEntityCaption(metaClass) + " (" + metaClass.getName() + ")", metaClass);
                }
            }
        }

        return options;
    }

    private boolean isNotAbstract(Class javaClass) {
        return !javaClass.isInterface() && !Modifier.isAbstract(javaClass.getModifiers());
    }

    private void showEntities() {
        selectedMeta = entitiesLookup.getValue();
        if (selectedMeta != null) {
            createEntitiesTable(selectedMeta);
            getWindow().setCaption(messageTools.getEntityCaption(selectedMeta));
        }
    }

    protected void changeTableTextSelectionEnabled() {
        entitiesTable.setTextSelectionEnabled(textSelection.isChecked());
    }

    protected void createEntitiesTable(MetaClass meta) {
        if (entitiesTable != null)
            tableBox.remove(entitiesTable);
        if (filter != null) {
            filterBox.remove(filter);
        }

        entitiesTable = uiComponents.create(Table.NAME);

        textSelection.setVisible(true);
        textSelection.addValueChangeListener(e -> changeTableTextSelectionEnabled());

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(messages.getMessage("dateTimeFormat"));
        Function<?, String> dateTimeFormatter = value -> {
            if (value == null) {
                return StringUtils.EMPTY;
            }

            return dateTimeFormat.format(value);
        };

        //collect properties in order to add non-system columns first
        List<Table.Column> nonSystemPropertyColumns = new ArrayList<>(10);
        List<Table.Column> systemPropertyColumns = new ArrayList<>(10);
        for (MetaProperty metaProperty : meta.getProperties()) {
            //don't show embedded, transient & multiple referred entities
            if (isEmbedded(metaProperty) || !metadataTools.isPersistent(metaProperty)) {
                continue;
            }

            Range range = metaProperty.getRange();
            if (range.getCardinality().isMany()) {
                continue;
            }

            Table.Column column = new Table.Column(meta.getPropertyPath(metaProperty.getName()));

            if (range.isDatatype() && range.asDatatype().getJavaClass().equals(Date.class)) {
                column.setFormatter(dateTimeFormatter);
            }

            if (metaProperty.getJavaType().equals(String.class)) {
                column.setMaxTextLength(MAX_TEXT_LENGTH);
            }

            if (!metadataTools.isSystem(metaProperty)) {
                column.setCaption(getPropertyCaption(meta, metaProperty));
                nonSystemPropertyColumns.add(column);
            } else {
                column.setCaption(metaProperty.getName());
                systemPropertyColumns.add(column);
            }
        }
        for (Table.Column column : nonSystemPropertyColumns) {
            entitiesTable.addColumn(column);
        }

        for (Table.Column column : systemPropertyColumns) {
            entitiesTable.addColumn(column);
        }

        if (entitiesDc != null) {

            //TODO unregister DC
//            ((DsContextImplementation) getDsContext()).unregister(entitiesDc);
        }

        entitiesDc = dataComponents.createCollectionContainer(meta.getJavaClass());
        entitiesDc.setFetchPlan(createView(meta));

        entitiesDl = dataComponents.createCollectionLoader();
        entitiesDl.setFetchPlan(createView(meta));
        entitiesDl.setContainer(entitiesDc);
        entitiesDl.setLoadDynamicAttributes(true);
        entitiesDl.setSoftDeletion(!removedRecords.isChecked());
        entitiesDl.setQuery(String.format("select e from %s e", meta.getName()));

        entitiesTable.setItems(new ContainerTableItems(entitiesDc));

        tableBox.add(entitiesTable);
        tableBox.expand(entitiesTable);

        entitiesTable.setSizeFull();

        createButtonsPanel(entitiesTable);

        RowsCount rowsCount = uiComponents.create(RowsCount.class);
        rowsCount.setRowsCountTarget(entitiesTable);
        entitiesTable.setRowsCount(rowsCount);

        entitiesTable.setEnterPressAction(entitiesTable.getAction("edit"));
        entitiesTable.setItemClickAction(entitiesTable.getAction("edit"));
        entitiesTable.setMultiSelect(true);

        entitiesTable.addStyleName("table-boolean-text");

        createFilter();
    }

    //TODO create filter component (Filter in Table/DataGrid #221)
    protected void createFilter() {
//        filter = uiComponents.create(FilterImplementation.class);
//        filter.setId("filter");
//        filter.setFrame(frame);
//
//        filterBox.add(filter);
//
//        filter.setUseMaxResults(true);
//        filter.setManualApplyRequired(true);
//        filter.setEditable(true);
//
//        filter.setDatasource(entitiesDc);
//        ((FilterImplementation) filter).loadFiltersAndApplyDefault();
//        filter.apply(true);
    }

    protected boolean isEmbedded(MetaProperty metaProperty) {
        return metaProperty.getAnnotatedElement().isAnnotationPresent(javax.persistence.Embedded.class);
    }

    protected void createButtonsPanel(Table table) {
        ButtonsPanel buttonsPanel = uiComponents.create(ButtonsPanel.class);

        createButton = uiComponents.create(Button.class);
        createButton.setCaption(messages.getMessage(EntityInspectorBrowser.class, "create"));
        CreateAction createAction = new CreateAction();
        table.addAction(createAction);
        createButton.setAction(createAction);
        createButton.setIcon(icons.get(JmixIcon.CREATE_ACTION));
        createButton.setFrame(getWindow().getFrame());

        editButton = uiComponents.create(Button.class);
        editButton.setCaption(messages.getMessage(EntityInspectorBrowser.class, "edit"));
        EditAction editAction = new EditAction();
        table.addAction(editAction);
        editButton.setAction(editAction);
        editButton.setIcon(icons.get(JmixIcon.EDIT_ACTION));
        editButton.setFrame(getWindow().getFrame());

        removeButton = uiComponents.create(Button.class);
        removeButton.setCaption(messages.getMessage(EntityInspectorBrowser.class, "remove"));
        RemoveAction removeAction = new RemoveAction("remove") {
            @Override
            protected boolean isPermitted() {
                if (!getTarget().getSelected().isEmpty()) {
                    if (getTarget().getSingleSelected() instanceof SoftDelete) {
                        for (Object e : getTarget().getSelected()) {
                            if (((SoftDelete) e).isDeleted())
                                return false;
                        }
                    }
                }
                //TODO Add security
//                return super.isPermitted();
                return true;
            }


        };
        removeAction.setTarget(entitiesTable);
        removeAction.setAfterActionPerformedHandler(event -> entitiesDl.load());
        table.addAction(removeAction);
        removeButton.setAction(removeAction);
        removeButton.setIcon(icons.get(JmixIcon.REMOVE_ACTION));
        removeButton.setFrame(getWindow().getFrame());

        //TODO excel action
        excelButton = uiComponents.create(Button.class);
//        excelButton.setCaption(messages.getMessage(com.haulmont.cuba.gui.app.core.entityinspector.EntityInspectorBrowse.class, "excel"));
//        excelButton.setAction(new ExcelAction(entitiesTable));
//        excelButton.setIcon(icons.get(CubaIcon.EXCEL_ACTION));
//        excelButton.setFrame(frame);

        refreshButton = uiComponents.create(Button.class);
        refreshButton.setCaption(messages.getMessage(EntityInspectorBrowser.class, "refresh"));
        RefreshAction refreshAction = new RefreshAction("refresh");
        refreshAction.setTarget(entitiesTable);
        refreshButton.setAction(refreshAction);
        refreshButton.setIcon(icons.get(JmixIcon.REFRESH_ACTION));
        refreshButton.setFrame(getWindow().getFrame());

        exportPopupButton = uiComponents.create(PopupButton.class);
        exportPopupButton.setIcon(icons.get(JmixIcon.DOWNLOAD));
        exportPopupButton.addAction(new ExportAction("exportJSON", JSON));
        exportPopupButton.addAction(new ExportAction("exportZIP", ZIP));

        //TODO init file upload (File storage API and UI components #103)
//        importUpload = uiComponents.create(FileUploadField.class);
//        importUpload.setFrame(getWindow().getFrame());
//        importUpload.setPermittedExtensions(Sets.newHashSet(".json", ".zip"));
//        importUpload.setUploadButtonIcon(icons.get(JmixIcon.UPLOAD));
//        importUpload.setUploadButtonCaption(messages.getMessage("import"));

//        importUpload.addFileUploadSucceedListener(event -> {
//            File file = fileUploadingAPI.getFile(importUpload.getFileId());
//            if (file == null) {
//                String errorMsg = String.format("Entities import upload error. File with id %s not found", importUpload.getFileId());
//                throw new RuntimeException(errorMsg);
//            }
//            byte[] fileBytes;
//            try (InputStream is = new FileInputStream(file)) {
//                fileBytes = IOUtils.toByteArray(is);
//            } catch (IOException e) {
//                throw new RuntimeException("Unable to upload file", e);
//            }
//            try {
//                fileUploadingAPI.deleteFile(importUpload.getFileId());
//            } catch (FileStorageException e) {
//                log.error("Unable to delete temp file", e);
//            }
//            String fileName = importUpload.getFileName();
//            try {
//                Collection<Entity> importedEntities;
//                if ("json".equals(Files.getFileExtension(fileName))) {
//                    String content = new String(fileBytes, StandardCharsets.UTF_8);
//                    importedEntities = entityImportExportService.importEntitiesFromJSON(content, createEntityImportView(selectedMeta));
//                } else {
//                    importedEntities = entityImportExportService.importEntitiesFromZIP(fileBytes, createEntityImportView(selectedMeta));
//                }
//
//                // todo localize the message !
//                showNotification(importedEntities.size() + " entities imported", NotificationType.HUMANIZED);
//            } catch (Exception e) {
//                showNotification(getMessage("importFailed"),
//                        formatMessage("importFailedMessage", fileName, nullToEmpty(e.getMessage())),
//                        NotificationType.ERROR);
//                log.error("Entities import error", e);
//            }
//            entitiesDc.refresh();
//        });

        buttonsPanel.add(createButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(removeButton);
//        buttonsPanel.add(excelButton);
        buttonsPanel.add(refreshButton);
//        buttonsPanel.add(exportPopupButton);
//        buttonsPanel.add(importUpload);

        table.setButtonsPanel(buttonsPanel);
    }

    protected FetchPlan createView(MetaClass meta) {
        FetchPlan view = new FetchPlan(meta.getJavaClass(), false);
        for (MetaProperty metaProperty : meta.getProperties()) {
            switch (metaProperty.getType()) {
                case DATATYPE:
                case ENUM:
                    view.addProperty(metaProperty.getName());
                    break;
                case ASSOCIATION:
                case COMPOSITION:
                    if (!metaProperty.getRange().getCardinality().isMany()) {
                        FetchPlan minimal = fetchPlanRepository
                                .getFetchPlan(metaProperty.getRange().asClass(), FetchPlan.MINIMAL);
                        FetchPlan propView = new FetchPlan(minimal, metaProperty.getName() + "Ds", false);
                        view.addProperty(metaProperty.getName(), propView);
                    }
                    break;
                default:
                    throw new IllegalStateException("unknown property type");
            }
        }
        return view;
    }

    protected EntityImportView createEntityImportView(MetaClass metaClass) {
        Class<? extends Entity> javaClass = metaClass.getJavaClass();
        EntityImportView entityImportView = new EntityImportView(javaClass);

        for (MetaProperty metaProperty : metaClass.getProperties()) {
            if (!metadataTools.isPersistent(metaProperty)) {
                continue;
            }

            switch (metaProperty.getType()) {
                case DATATYPE:
                case ENUM:
                    entityImportView.addLocalProperty(metaProperty.getName());
                    break;
                case ASSOCIATION:
                case COMPOSITION:
                    Range.Cardinality cardinality = metaProperty.getRange().getCardinality();
                    if (cardinality == Range.Cardinality.MANY_TO_ONE) {
                        entityImportView.addManyToOneProperty(metaProperty.getName(), ReferenceImportBehaviour.IGNORE_MISSING);
                    } else if (cardinality == Range.Cardinality.ONE_TO_ONE) {
                        entityImportView.addOneToOneProperty(metaProperty.getName(), ReferenceImportBehaviour.IGNORE_MISSING);
                    }
                    break;
                default:
                    throw new IllegalStateException("unknown property type");
            }
        }
        return entityImportView;
    }

    protected class CreateAction extends BaseAction {

        public CreateAction() {
            super("create");
            this.primary = true;
            setShortcut(uiProperties.getTableInsertShortcut());
        }

        @Override
        public void actionPerform(Component component) {
            screens.create(EntityInspectorJmix.class, OpenMode.THIS_TAB,
                    new MapScreenOptions(
                            ParamsMap.of("metaClass", selectedMeta.getName())))
                    .show()
                    .addAfterCloseListener(afterCloseEvent -> {
                        entitiesDl.load();
                        entitiesTable.focus();
                    });
        }
    }

    protected class EditAction extends ItemTrackingAction {

        public EditAction() {
            super("edit");
        }

        @Override
        public void actionPerform(Component component) {
            Set selected = entitiesTable.getSelected();
            if (selected.size() != 1)
                return;

            Entity item = (Entity) selected.iterator().next();

            screens.create(EntityInspectorJmix.class, OpenMode.THIS_TAB,
                    new MapScreenOptions(
                            ParamsMap.of("item", item)))
                    .show()
                    .addAfterCloseListener(afterCloseEvent -> {
                        entitiesDl.load();
                        entitiesTable.focus();
                    });
        }
    }

    protected String getPropertyCaption(MetaClass metaClass, MetaProperty metaProperty) {
        return messageTools.getPropertyCaption(metaClass, metaProperty.getName());
    }

    protected boolean readPermitted(MetaClass metaClass) {
        return entityOpPermitted(metaClass, EntityOp.READ);
    }

    protected boolean entityOpPermitted(MetaClass metaClass, EntityOp entityOp) {
        return security.isEntityOpPermitted(metaClass, entityOp);
    }

    protected class ExportAction extends ItemTrackingAction {

        private ExportFormat exportFormat;

        public ExportAction(String id, ExportFormat exportFormat) {
            super(id);
            this.exportFormat = exportFormat;
        }

        @Override
        public void actionPerform(Component component) {
            Collection<Entity> selected = entitiesTable.getSelected();
            if (selected.isEmpty()
                    && entitiesTable.getItems() != null) {
                selected = entitiesTable.getItems().getItems();
            }

            //TODO export
            try {
                if (exportFormat == ZIP) {
//                    byte[] data = entityImportExportService.exportEntitiesToZIP(selected);
//                    String resourceName = selectedMeta.getJavaClass().getSimpleName() + ".zip";
//                    exportDisplay.show(new ByteArrayDataProvider(data), resourceName, ZIP);
                } else if (exportFormat == JSON) {
//                    byte[] data = entityImportExportService.exportEntitiesToJSON(selected)
//                            .getBytes(StandardCharsets.UTF_8);
//                    String resourceName = selectedMeta.getJavaClass().getSimpleName() + ".json";
//                    exportDisplay.show(new ByteArrayDataProvider(data), resourceName, JSON);
                }
            } catch (Exception e) {
                //TODO show notification
//                showNotification(messages.getMessage("exportFailed"), e.getMessage(), Frame.NotificationType.ERROR);
                log.error("Entities export failed", e);
            }
        }

        @Override
        public String getCaption() {
            return messages.getMessage(id);
        }
    }
}