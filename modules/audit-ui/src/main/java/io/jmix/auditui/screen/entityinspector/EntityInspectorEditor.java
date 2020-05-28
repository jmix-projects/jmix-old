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
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.core.metamodel.model.Range;
import io.jmix.core.metamodel.model.utils.InstanceUtils;
import io.jmix.core.security.EntityAttrAccess;
import io.jmix.core.security.EntityOp;
import io.jmix.core.security.Security;
import io.jmix.ui.*;
import io.jmix.ui.action.AbstractAction;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.action.ItemTrackingAction;
import io.jmix.ui.action.list.AddAction;
import io.jmix.ui.action.list.ExcludeAction;
import io.jmix.ui.action.list.RemoveAction;
import io.jmix.ui.component.*;
import io.jmix.ui.component.data.ValueSource;
import io.jmix.ui.component.data.table.ContainerTableItems;
import io.jmix.ui.component.data.value.ContainerValueSource;
import io.jmix.ui.model.*;
import io.jmix.ui.screen.*;
import io.jmix.ui.sys.PersistenceHelper;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.*;

import static io.jmix.ui.component.Window.COMMIT_ACTION_ID;

//@UiController("entityInspector.edit")
//@UiDescriptor("entity-inspector-edit.xml")
public class EntityInspectorEditor extends Screen {

    public static final int CAPTION_MAX_LENGTH = 100;
    public static final int MAX_TEXT_LENGTH = 50;

    public static final Screens.LaunchMode OPEN_TYPE = OpenMode.THIS_TAB;
    public static final int MAX_TEXTFIELD_STRING_LENGTH = 255;

    @Autowired
    protected Metadata metadata;
    @Autowired
    protected MetadataTools metadataTools;
    @Autowired
    protected InstanceNameProvider instanceNameProvider;
    @Autowired
    protected MessageTools messageTools;
    @Autowired
    protected Messages messages;
    @Autowired
    protected FetchPlanRepository fetchPlanRepository;
    @Autowired
    protected Security security;
    @Autowired
    protected DataManager dataManager;
    @Autowired
    protected EntityStates entityStates;
    @Autowired
    protected UiProperties uiProperties;
    @Autowired
    protected UiComponentsGenerator uiComponentsGenerator;

    @Autowired
    protected BoxLayout buttonsBox;
    @Autowired
    protected BoxLayout contentPane;
    @Autowired
    protected BoxLayout runtimePane;
    @Autowired
    protected TabSheet tablesTabSheet;

    @Autowired
    protected UiComponents uiComponents;
    @Autowired
    protected DataComponents dataComponents;
    @Autowired
    protected Notifications notifications;
    @Autowired
    protected Screens screens;
//    @Autowired
    private DataContext dataContext;
//    @Autowired
//    protected Configuration configuration;
//    @Autowired
//    protected ThemeConstants themeConstants;

    @WindowParam(name = "item")
    protected Entity item;

    @WindowParam(name = "parent")
    protected Entity parent;

    @WindowParam(name = "parentProperty")
    protected String parentProperty;

    @WindowParam(name = "parentDs")
    protected InstanceContainer parentDc;

    @WindowParam(name = "datasource")
    protected InstanceLoader dataLoader;

    @WindowParam(name = "dataContainer")
    protected InstanceContainer dataContainer;

    protected MetaClass meta;
    //    protected DsContextImpl dsContext;
    protected Map<String, DataLoader> dataLoaders;

    protected Boolean isNew;
    protected Boolean autocommit;
    protected Boolean showSystemFields;
    protected Collection<Table> tables;

    protected Collection<Field> reserveLineSeparatorFields;
    //    protected RuntimePropsDatasource rDS;
    protected CollectionLoader categoriesDl;

    protected ButtonsPanel buttonsPanel;
    protected Button commitButton;
    protected Button cancelButton;
    protected Form focusFieldGroup;
    protected String focusFieldId;

    public EntityInspectorEditor() {
        dataLoaders = new HashMap<>();
        tables = new LinkedList<>();
        isNew = true;
        autocommit = true;
        showSystemFields = false;
    }

    @Subscribe
    protected void onInit(InitEvent initEvent) {
        Map<String, Object> params = new HashMap<>();
        ScreenOptions options = initEvent.getOptions();
        if (options instanceof MapScreenOptions) {
            params = ((MapScreenOptions) options).getParams();
        }

        dataContext = dataComponents.createDataContext();

        item = (Entity) params.get("item");
        isNew = item == null || entityStates.isNew(item);
        meta = item != null ? metadata.getClass(item) : metadata.getSession().getClass((String) params.get("metaClass"));
        autocommit = params.get("autocommit") != null ? (Boolean) params.get("autocommit") : true;
        showSystemFields = params.get("showSystemFields") != null ? (Boolean) params.get("showSystemFields") : false;

        if (meta == null) {
            throw new IllegalStateException("Entity or entity's MetaClass must be specified");
        }

        getWindow().setCaption(meta.getName());
        initShortcuts();

        FetchPlan fetchPlan = createView(meta);

        //TODO DsContext
//        dsContext = new DsContextImpl(dataManager);
//        dsContext.setFrameContext(getDsContext().getFrameContext());
//        setDsContext(dsContext);

        //TODO replace to entity
        boolean createRequest = item == null;
        if (createRequest) {
            item = metadata.create(meta);
            setParentField(item, parentProperty, parent);
            //TODO handle dynamic attributes
//            if (item instanceof BaseGenericIdEntity) {
//                ((BaseGenericIdEntity) item).setDynamicAttributes(new HashMap<>());
//            }
        } else {
            //edit request
            Object itemId = Id.of(item).getValue();
            if (!isNew) {
                item = loadSingleItem(meta, itemId, fetchPlan);
            }
            if (item == null) {
                throw new EntityAccessException(meta, itemId);
            }
        }
        createEmbeddedFields(meta, item);

        //TODO dependencies to dynamic attributes
        boolean categorizedEntity = /*item instanceof Categorized;*/false;

        if (dataLoader == null) {
            dataContainer = parentDc != null ? dataComponents.createInstanceContainer(item.getClass(), parentDc, parentProperty)
                    : dataComponents.createInstanceContainer(item.getClass());
            dataContainer.setItem(item);
            dataLoader = dataComponents.createInstanceLoader();
            dataLoader.setFetchPlan(fetchPlan);
            dataLoader.setContainer(dataContainer);
            dataContext.merge(item);
        }
        //TODO handle dsContext
//        dsContext.register(dataLoader);
        createPropertyDatasources(dataContainer);
        //TODO init runtime properties
//        if (categorizedEntity) {
//            initRuntimePropertiesDatasources(fetchPlan);
//        }

        dataLoader.load();

        reserveLineSeparatorFields = new LinkedList<>();
        createDataComponents(meta, item);
        //TODO init runtime properties
//        if (categorizedEntity) {
//            createRuntimeDataComponents();
//        }

        dataContainer.setItem(item);

        //TODO init runtime properties
//        if (categorizedEntity) {
//            rDS.refresh();
//        }

        createCommitButtons();
        getWindow().setCaption(meta.getName());

        if (focusFieldGroup != null && focusFieldId != null) {
            //TODO focus field
//            focusFieldGroup.focusField(focusFieldId);
        }
    }

    public Entity getItem() {
        return dataLoader.getContainer().getItem();
    }

    protected void initShortcuts() {
        Action commitAction = new BaseAction("commitAndClose")
                .withCaption(messages.getMessage("actions.OkClose"))
                .withShortcut(uiProperties.getCommitShortcut())
                .withHandler(e ->
                        commitAndClose()
                );
        getWindow().addAction(commitAction);
    }

    protected void setParentField(Entity item, String parentProperty, Entity parent) {
        if (parentProperty != null && parent != null && item != null) {
            //TODO set parent property
//            item.setValue(parentProperty, parent);
        }
    }

//    protected void createRuntimeDataComponents() {
//        if (rDS != null && categoriesDl != null) {
//            Map<String, Object> params = new HashMap<>();
//            params.put("runtimeDs", rDS.getId());
//            params.put("categoriesDs", categoriesDl.getId());
//            params.put("fieldWidth", themeConstants.get("cuba.gui.EntityInspectorEditor.field.width"));
//            params.put("borderVisible", Boolean.TRUE);
//
//            RuntimePropertiesFrame runtimePropertiesFrame = (RuntimePropertiesFrame) openFrame(runtimePane, "runtimePropertiesFrame", params);
//            runtimePropertiesFrame.setFrame(this.getFrame());
//            runtimePropertiesFrame.setMessagesPack("com.haulmont.cuba.gui.app.core.entityinspector");
//            runtimePropertiesFrame.setCategoryFieldVisible(false);
//
//            runtimePropertiesFrame.setHeightAuto();
//            runtimePropertiesFrame.setWidthFull();
//
//            runtimePane.add(runtimePropertiesFrame);
//        }
//    }

//    protected void initRuntimePropertiesDatasources(View view) {
//        rDS = new RuntimePropsDatasourceImpl(dsContext, dataManager, "rDS", dataLoader.getId(), null);
//        MetaClass categoriesMeta = metadata.getSession().getClass(Category.class);
//        categoriesDl = new CollectionDatasourceImpl();
//        ViewProperty categoryProperty = view.getProperty("category");
//        if (categoryProperty == null) {
//            throw new IllegalArgumentException("Category property not found. Not a categorized entity?");
//        }
//        categoriesDl.setup(dsContext, dataManager, "categoriesDs", categoriesMeta, categoryProperty.getView());
//        categoriesDl.setQuery(String.format("select c from sys$Category c where c.entityType='%s'", meta.getName()));
//        categoriesDl.refresh();
//        dsContext.register(rDS);
//        dsContext.register(categoriesDl);
//    }

    /**
     * Recursively instantiates the embedded properties.
     * E.g. embedded properties of the embedded property will also be instantiated.
     *
     * @param metaClass meta class of the entity
     * @param item      entity instance
     */
    protected void createEmbeddedFields(MetaClass metaClass, Entity item) {
        for (MetaProperty metaProperty : metaClass.getProperties()) {
            if (isEmbedded(metaProperty)) {
                MetaClass embeddedMetaClass = metaProperty.getRange().asClass();
                Entity embedded = EntityValues.getValue(item, metaProperty.getName());
                if (embedded == null) {
                    embedded = metadata.create(embeddedMetaClass);
                    EntityValues.setValue(item, metaProperty.getName(), embedded);
                }
                createEmbeddedFields(embeddedMetaClass, embedded);
            }
        }
    }

    /**
     * Returns metaProperty of the referred entity annotated with either nullIndicatorAttributeName or
     * nullIndicatorColumnName property.
     *
     * @param embeddedMetaProperty embedded property of the current entity
     * @return property of the referred entity
     */
    protected MetaProperty getNullIndicatorProperty(MetaProperty embeddedMetaProperty) {
        // Unsupported for EclipseLink ORM
        return null;
    }

    /**
     * Checks if the property is embedded
     *
     * @param metaProperty meta property
     * @return true if embedded, false otherwise
     */
    protected boolean isEmbedded(MetaProperty metaProperty) {
        return metaProperty.getAnnotatedElement().isAnnotationPresent(javax.persistence.Embedded.class)
                || metaProperty.getAnnotatedElement().isAnnotationPresent(javax.persistence.EmbeddedId.class);
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
     * Creates components representing item data
     * (fieldGroup, fieldGroups for embedded properties, tables for the referred entities)
     *
     * @param metaClass item meta class
     */
    protected void createDataComponents(MetaClass metaClass, Entity item) {
        Form form = uiComponents.create(Form.class);
        //TODO border
//        form.setBorderVisible(true);

        contentPane.add(form);
//        form.setFrame(frame);
        MetaProperty primaryKeyProperty = metadataTools.getPrimaryKeyProperty(metaClass);

        List<Component> customFields = new ArrayList<>();
        for (MetaProperty metaProperty : metaClass.getProperties()) {
            boolean isRequired = isRequired(metaProperty);
            boolean isReadonly = metaProperty.isReadOnly();
            switch (metaProperty.getType()) {
                case DATATYPE:
                case ENUM:
                    boolean includeId = primaryKeyProperty.equals(metaProperty)
                            && String.class.equals(metaProperty.getJavaType());
                    //skip system properties
                    if (metadataTools.isSystem(metaProperty) && !showSystemFields && !includeId) {
                        continue;
                    }
                    if (metaProperty.getType() != MetaProperty.Type.ENUM
                            && (isByteArray(metaProperty) || isUuid(metaProperty))) {
                        continue;
                    }

                    if (includeId && !isNew) {
                        isReadonly = true;
                    }

                    Range range = metaProperty.getRange();
                    if (range.isDatatype() && range.asDatatype().getJavaClass().equals(Boolean.class)) {
                        addBooleanCustomField(metaClass, metaProperty, item, form, isRequired, isReadonly);
                        break;
                    }

                    addField(dataContainer, metaClass, metaProperty, item, form, isRequired, false, isReadonly, customFields);
                    break;
                case COMPOSITION:
                case ASSOCIATION:
                    if (metaProperty.getRange().getCardinality().isMany()) {
                        addTable(metaClass, metaProperty);
                    } else {
                        if (isEmbedded(metaProperty)) {
                            Entity propertyValue = EntityValues.getValue(item, metaProperty.getName());
                            dataContext.merge(propertyValue);
                            addEmbeddedFieldGroup(metaProperty, "", propertyValue,
                                    (!metaProperty.getAnnotatedElement().isAnnotationPresent(javax.persistence.EmbeddedId.class)
                                            || entityStates.isNew(item)));
                        } else {
                            addField(dataContainer, metaClass, metaProperty, item, form, isRequired, true, isReadonly, customFields);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        createCustomFields(form, customFields);
    }

    /**
     * Creates field group for the embedded property
     *
     * @param embeddedMetaProperty meta property of the embedded property
     * @param embeddedItem         current value of the embedded property
     */
    protected void addEmbeddedFieldGroup(MetaProperty embeddedMetaProperty, String fqnPrefix, Entity embeddedItem, boolean editable) {
        String fqn = fqnPrefix.isEmpty() ? embeddedMetaProperty.getName()
                : fqnPrefix + "." + embeddedMetaProperty.getName();
        DataLoader embedDs = dataLoaders.get(fqn);
        if (embedDs == null) {
            throw new IllegalStateException(String.format("Datasource %s for property %s not found", fqn,
                    embeddedMetaProperty.getName()));
        }
        Form fieldGroup = uiComponents.create(Form.class);
        fieldGroup.setCaption(getPropertyCaption(embedDs.getContainer().getEntityMetaClass(), embeddedMetaProperty));

        contentPane.add(fieldGroup);

        fieldGroup.setEditable(editable);

        MetaClass embeddableMetaClass = embeddedMetaProperty.getRange().asClass();
        Collection<Component> customFields = new LinkedList<>();
        MetaProperty nullIndicatorProperty = getNullIndicatorProperty(embeddedMetaProperty);

        List<String> dateTimeFields = new ArrayList<>();

        for (MetaProperty metaProperty : embeddableMetaClass.getProperties()) {
            boolean isRequired = isRequired(metaProperty) || metaProperty.equals(nullIndicatorProperty);
            boolean isReadonly = metaProperty.isReadOnly();

            if (metaProperty.getType() == MetaProperty.Type.DATATYPE) {
                if (metaProperty.getRange().asDatatype().getJavaClass().equals(Date.class)) {
                    dateTimeFields.add(metaProperty.getName());
                }
            }

            switch (metaProperty.getType()) {
                case DATATYPE:
                case ENUM:
                    //skip system properties
                    if (metadataTools.isSystem(metaProperty) && !showSystemFields) {
                        continue;
                    }
                    if (metaProperty.getType() != MetaProperty.Type.ENUM
                            && (isByteArray(metaProperty) || isUuid(metaProperty))) {
                        continue;
                    }
                    addField(embedDs.getContainer(), embeddableMetaClass, metaProperty, embeddedItem, fieldGroup, isRequired, false, isReadonly, customFields);
                    break;

                case COMPOSITION:
                case ASSOCIATION:
                    if (metaProperty.getRange().getCardinality().isMany()) {
                        throw new IllegalStateException("tables for the embeddable entities are not supported");
                    } else {
                        if (isEmbedded(metaProperty)) {
                            Entity propertyValue = EntityValues.getValue(embeddedItem, metaProperty.getName());
                            addEmbeddedFieldGroup(metaProperty, fqn, propertyValue, editable);
                        } else {
                            addField(embedDs.getContainer(), embeddableMetaClass, metaProperty, embeddedItem, fieldGroup, isRequired, true, isReadonly, customFields);
                        }
                    }
                    break;

                default:
                    break;
            }
        }

        customFields.forEach(field -> fieldGroup.add(field));

        for (String dateTimeField : dateTimeFields) {
            Component field = fieldGroup.getComponent(dateTimeField);
            if (field != null) {
                ((DateField) field).setResolution(DateField.Resolution.SEC);
            }
        }
    }

    protected boolean isByteArray(MetaProperty metaProperty) {
        return metaProperty.getRange().asDatatype().getJavaClass().equals(byte[].class);
    }

    protected boolean isUuid(MetaProperty metaProperty) {
        return metaProperty.getRange().asDatatype().getJavaClass().equals(UUID.class);
    }

    protected boolean isRequired(MetaProperty metaProperty) {
        if (metaProperty.isMandatory())
            return true;

        ManyToOne many2One = metaProperty.getAnnotatedElement().getAnnotation(ManyToOne.class);
        if (many2One != null && !many2One.optional())
            return true;

        OneToOne one2one = metaProperty.getAnnotatedElement().getAnnotation(OneToOne.class);
        return one2one != null && !one2one.optional();
    }

    /**
     * Creates and registers in dsContext property datasource for each of the entity non-datatype
     * and non-enum property
     *
     * @param masterDs master datasource
     */
    protected void createPropertyDatasources(InstanceContainer masterDs) {
        for (MetaProperty metaProperty : meta.getProperties()) {
            Range range = metaProperty.getRange();
            if (range.isClass()) {
                MetaClass propertyClass = range.asClass();
                switch (metaProperty.getType()) {
                    case COMPOSITION:
                    case ASSOCIATION:
                        DataLoader propertyDs;
                        if (range.getCardinality().isMany()) {
                            CollectionLoader loader = dataComponents.createCollectionLoader();
                            loader.setLoadDelegate(loadContext -> EntityValues.getValue(masterDs.getItem(), metaProperty.getName()));
                            CollectionContainer container = dataComponents.createCollectionContainer(
                                    propertyClass.getJavaClass(), masterDs, metaProperty.getName());
                            loader.setContainer(container);
                            propertyDs = loader;
                        } else {
                            InstanceLoader loader = dataComponents.createInstanceLoader();
                            loader.setLoadDelegate(o -> EntityValues.getValue(masterDs.getItem(), metaProperty.getName()));
                            InstanceContainer container = dataComponents.createInstanceContainer(
                                    propertyClass.getJavaClass(), masterDs, metaProperty.getName());
                            loader.setContainer(container);
                            propertyDs = loader;
                        }
                        if (isEmbedded(metaProperty)) {
                            createNestedEmbeddedDatasources(range.asClass(), metaProperty.getName(), propertyDs);
                        }
                        dataLoaders.put(metaProperty.getName(), propertyDs);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    protected void createNestedEmbeddedDatasources(MetaClass metaClass, String fqnPrefix, DataLoader masterDs) {
        for (MetaProperty metaProperty : metaClass.getProperties()) {
            if (MetaProperty.Type.ASSOCIATION == metaProperty.getType()
                    || MetaProperty.Type.COMPOSITION == metaProperty.getType()) {
                if (isEmbedded(metaProperty)) {
                    String fqn = fqnPrefix + "." + metaProperty.getName();
                    MetaClass propertyMetaClass = metaProperty.getRange().asClass();
                    InstanceLoader loader = dataComponents.createInstanceLoader();
                    InstanceContainer container = dataComponents.createInstanceContainer(propertyMetaClass.getJavaClass(), masterDs.getContainer(), metaProperty.getName());
                    loader.setContainer(container);
                    createNestedEmbeddedDatasources(propertyMetaClass, fqn, loader);
                    dataLoaders.put(fqn, loader);
                }
            }
        }
    }

    protected void createCommitButtons() {
        buttonsPanel = uiComponents.create(ButtonsPanel.class);
        commitButton = uiComponents.create(Button.class);
        commitButton.setIcon("icons/ok.png");
        commitButton.setCaption(messages.getMessage(EntityInspectorEditor.class, "commit"));
        commitButton.setAction(new CommitAction());
        cancelButton = uiComponents.create(Button.class);
        cancelButton.setIcon("icons/cancel.png");
        cancelButton.setCaption(messages.getMessage(EntityInspectorEditor.class, "cancel"));
        cancelButton.setAction(new CancelAction());
        buttonsPanel.add(commitButton);
        buttonsPanel.add(cancelButton);
        buttonsBox.add(buttonsPanel);
    }

    /**
     * Adds field to the specified field group.
     * If the field should be custom, adds it to the specified customFields collection
     * which can be used later to create fieldGenerators
     *
     * @param metaProperty meta property of the item's property which field is creating
     * @param item         entity instance containing given property
     * @param form         field group to which created field will be added
     * @param customFields if the field is custom it will be added to this collection
     * @param required     true if the field is required
     * @param custom       true if the field is custom
     */
    protected void addField(InstanceContainer container, MetaClass metaClass, MetaProperty metaProperty, Entity item,
                            Form form, boolean required, boolean custom, boolean readOnly,
                            Collection<Component> customFields) {
        metaClass = container.getEntityMetaClass();
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

        field.setCaption(getPropertyCaption(metaClass, metaProperty));
        field.setRequired(required);

        if (metaProperty.getRange().isClass() && !metadataTools.isEmbedded(metaProperty)) {
            field.setEditable(metadataTools.isOwningSide(metaProperty) && !readOnly);
        } else {
            field.setEditable(!readOnly);
        }

        field.setWidth("400px");

        if (requireTextArea(metaProperty, item)) {
            Element root = DocumentHelper.createElement("textArea");
            root.addAttribute("rows", "3");
            //TODO Text area
//            field.setXmlDescriptor(root);
        }

        if (focusFieldId == null && !readOnly) {
            focusFieldId = field.getId();
            focusFieldGroup = form;
        }

        if (required) {
            field.setRequiredMessage(messageTools.getDefaultRequiredMessage(metaClass, metaProperty.getName()));
        }
        form.add(field);
        if (custom)
            customFields.add(field);
    }


    /**
     * Adds LookupField with boolean values instead of CheckBox that can't display null value.
     *
     * @param metaClass    meta property of the item's property which field is creating
     * @param metaProperty meta property of the item's property which field is creating
     * @param item         entity instance containing given property
     * @param form         field group to which created field will be added
     * @param required     true if the field is required
     * @param readOnly     false if field should be editable
     */
    protected void addBooleanCustomField(MetaClass metaClass, MetaProperty metaProperty, Entity item,
                                         Form form, boolean required, boolean readOnly) {
        if (!attrViewPermitted(metaClass, metaProperty)) {
            return;
        }

        LookupField field = uiComponents.create(LookupField.NAME);
        String caption = getPropertyCaption(dataContainer.getEntityMetaClass(), metaProperty);
        field.setCaption(caption);
        field.setEditable(!readOnly);
        field.setRequired(required);
        //TODO add ds
//        field.setDatasource(dataLoader, metaProperty.getName());
        field.setOptionsMap(ParamsMap.of(
                messages.getMessage("trueString"), Boolean.TRUE,
                messages.getMessage("falseString"), Boolean.FALSE));
        field.setTextInputAllowed(false);

        if (!PersistenceHelper.isNew(item)) {
            MetaPropertyPath metaPropertyPath = metaClass.getPropertyPath(metaProperty.getName());
            Object value = InstanceUtils.getValueEx(item, metaPropertyPath.getPath());
            field.setValue(value);
        }

        field.setWidth("400px");
        form.add(field);
    }

    /**
     * @param metaProperty meta property
     * @param item         entity containing property of the given meta property
     * @return true if property require text area component; that is if it either too long or contains line separators
     */
    protected boolean requireTextArea(MetaProperty metaProperty, Entity item) {
        if (!String.class.equals(metaProperty.getJavaType())) {
            return false;
        }

        Integer textLength = (Integer) metaProperty.getAnnotations().get("length");
        boolean isLong = textLength == null || textLength > MAX_TEXTFIELD_STRING_LENGTH;

        Object value = EntityValues.getValue(item, metaProperty.getName());
        boolean isContainsSeparator = value != null && containsSeparator((String) value);

        return isLong || isContainsSeparator;
    }

    protected boolean containsSeparator(String s) {
        return s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0;
    }

    /**
     * Checks if specified property is a reference to entity's parent entity.
     * Parent entity can be specified during creating of this screen.
     *
     * @param metaProperty meta property
     * @return true if property references to a parent entity
     */
    protected boolean isParentProperty(MetaProperty metaProperty) {
        return parentProperty != null && metaProperty.getName().equals(parentProperty);
    }

    /**
     * Creates custom fields and adds them to the form
     */
    protected void createCustomFields(Form form, Collection<Component> customFields) {
        //TODO check custom fields
//        for (Component field : customFields) {
//            //custom field generator creates an pickerField
//
//                MetaProperty metaProperty = datasource.getMetaClass().getPropertyNN(propertyId);
//                MetaClass propertyMeta = metaProperty.getRange().asClass();
//
//                PickerField pickerField = uiComponents.create(PickerField.class);
//                String caption = getPropertyCaption(datasource.getMetaClass(), metaProperty);
//                pickerField.setCaption(caption);
//                pickerField.setMetaClass(propertyMeta);
//                pickerField.setWidth("400px");
//
//                PickerField.LookupAction lookupAction = pickerField.addLookupAction();
//                //forwards lookup to the EntityInspectorBrowse window
//                lookupAction.setLookupScreen(EntityInspectorBrowse.SCREEN_NAME);
//                lookupAction.setLookupScreenOpenType(OPEN_TYPE);
//                lookupAction.setLookupScreenParams(ParamsMap.of("entity", propertyMeta.getName()));
//
//                pickerField.addClearAction();
//                //don't lets user to change parent
//                if (isParentProperty(metaProperty)) {
//                    //set parent item if it has been retrieved
//                    if (parent != null) {
//                        if (parent.toString() == null) {
//                            initNamePatternFields(parent);
//                        }
//                        pickerField.setValue(parent);
//                    }
//                    pickerField.setEditable(false);
//                }
//                pickerField.setDatasource(datasource, propertyId);
//                form.add(pickerField);
//        }
    }

    /**
     * Tries to initialize entity fields included in entity name pattern by default values
     *
     * @param entity instance
     */
    protected void initNamePatternFields(Entity entity) {
        Collection<MetaProperty> properties = metadataTools.getInstanceNameRelatedProperties(metadata.getClass(entity));
        for (MetaProperty property : properties) {
            if (EntityValues.getValue(entity, property.getName()) == null
                    && property.getType() == MetaProperty.Type.DATATYPE) {
                try {
                    EntityValues.setValue(entity, property.getName(), property.getJavaType().newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException("Unable to set value of name pattern field", e);
                }
            }
        }
    }

    protected String getPropertyCaption(MetaClass metaClass, MetaProperty metaProperty) {
        String caption = messageTools.getPropertyCaption(metaClass, metaProperty.getName());
        if (caption.length() < CAPTION_MAX_LENGTH)
            return caption;
        else
            return caption.substring(0, CAPTION_MAX_LENGTH);
    }

    /**
     * Creates a table for the entities in ONE_TO_MANY or MANY_TO_MANY relation with the current one
     */
    protected void addTable(MetaClass metaClass, MetaProperty childMeta) {
        MetaClass meta = childMeta.getRange().asClass();

        //don't show empty table if the user don't have permissions on the attribute or the entity
        if (!attrViewPermitted(metaClass, childMeta.getName()) ||
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
        CollectionLoader propertyDs = (CollectionLoader) dataLoaders.get(childMeta.getName());

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

        //set datasource so we could create a buttons panel
        table.setItems(new ContainerTableItems(propertyDs.getContainer()));

        //refresh ds to read ds size
        propertyDs.load();
        ButtonsPanel propertyButtonsPanel = createButtonsPanel(childMeta, propertyDs, table);
        table.setButtonsPanel(propertyButtonsPanel);

        //TODO supports paging
//        if (propertyDs instanceof CollectionDatasource.SupportsPaging) {
//            RowsCount rowsCount = uiComponents.create(RowsCount.class);
//            rowsCount.setDatasource(propertyDs);
//            table.setRowsCount(rowsCount);
//        }

        table.setWidth("100%");

        //TODO theme constants
//        vbox.setHeight(themeConstants.get("cuba.gui.EntityInspectorEditor.tableContainer.height"));
        vbox.add(table);
        vbox.expand(table);
        vbox.setMargin(true);

        //TODO tabsheet
        TabSheet.Tab tab = tablesTabSheet.addTab(childMeta.toString(), vbox);
        tab.setCaption(getPropertyCaption(metaClass, childMeta));
        tables.add(table);
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
                                              CollectionLoader propertyDs, Table table) {
        MetaClass propertyMetaClass = metaProperty.getRange().asClass();
        ButtonsPanel propertyButtonsPanel = uiComponents.create(ButtonsPanel.class);

        Button createButton = uiComponents.create(Button.class);
        CreateAction createAction = new CreateAction(metaProperty, propertyDs.getContainer(), propertyMetaClass);
        createButton.setAction(createAction);
        table.addAction(createAction);
        createButton.setCaption(messages.getMessage(EntityInspectorEditor.class, "create"));
        createButton.setIcon("icons/create.png");

        Button addButton = uiComponents.create(Button.class);
        AddAction addAction = createAddAction(metaProperty, propertyDs.getContainer(), table, propertyMetaClass);
        table.addAction(addAction);
        addButton.setAction(addAction);
        addButton.setCaption(messages.getMessage(EntityInspectorEditor.class, "add"));
        addButton.setIcon("icons/add.png");

        Button editButton = uiComponents.create(Button.class);
        EditAction editAction = new EditAction(metaProperty, table, propertyDs.getContainer());
        editButton.setAction(editAction);
        editButton.setCaption(messages.getMessage(EntityInspectorEditor.class, "edit"));
        editButton.setIcon("icons/edit.png");
        table.addAction(editAction);
        table.setItemClickAction(editAction);
        table.setEnterPressAction(editAction);

        Action removeAction = createRemoveAction(metaProperty, table);
        Button removeButton = uiComponents.create(Button.class);
        removeButton.setAction(removeAction);
        table.addAction(removeAction);
        removeButton.setCaption(messages.getMessage(EntityInspectorEditor.class, "remove"));
        removeButton.setIcon("icons/remove.png");

        propertyButtonsPanel.add(createButton);
        propertyButtonsPanel.add(addButton);
        propertyButtonsPanel.add(editButton);
        propertyButtonsPanel.add(removeButton);
        return propertyButtonsPanel;
    }

    protected AddAction createAddAction(MetaProperty metaProperty, CollectionContainer propertyDs,
                                        Table table, MetaClass propertyMetaClass) {
        //TODO handler
        Window.Lookup.Handler addHandler = createAddHandler(metaProperty, propertyDs);
        AddAction addAction = new AddAction() {
            //TODO handle security
            @Override
            protected boolean isPermitted() {
                return true;
            }
        };
        addAction.setTarget(table);
        addAction.setOpenMode(OpenMode.THIS_TAB);
        addAction.setScreenClass(EntityInspectorBrowser.class);
        HashMap<String, Object> params = new HashMap<>();
        params.put("entity", propertyMetaClass.getName());
        MetaProperty inverseProperty = metaProperty.getInverse();
        if (inverseProperty != null)
            params.put("parentProperty", inverseProperty.getName());
        //TODO handleParams
//        addAction.setWindowParams(params);
        addAction.setShortcut(uiProperties.getTableAddShortcut());
        return addAction;
    }

    @SuppressWarnings("unchecked")
    //TODO replace lookup handler
    protected Window.Lookup.Handler createAddHandler(final MetaProperty metaProperty, final CollectionContainer propertyDs) {
        Window.Lookup.Handler result = items -> {
            for (Object item : items) {
                Entity entity = (Entity) item;
                if (!propertyDs.getItems().contains(entity)) {
                    MetaProperty inverseProperty = metaProperty.getInverse();
                    if (inverseProperty != null) {
                        if (!inverseProperty.getRange().getCardinality().isMany()) {
                            //set currently editing item to the child's parent property
                            EntityValues.setValue(entity, inverseProperty.getName(), dataContainer.getItem());
                            propertyDs.getMutableItems().add(entity);
                        } else {
                            Collection properties = EntityValues.getValue(entity, inverseProperty.getName());
                            if (properties != null) {
                                properties.add(dataContainer.getItem());
                                propertyDs.getMutableItems().add(entity);
                            }
                        }
                    }
                }

                propertyDs.getMutableItems().add(entity);
            }
        };

        return result;
    }

    public void commitAndClose() {
        try {
            //TODO handle commit
//            validate();
            dataContext.commit();
            close(WINDOW_COMMIT_AND_CLOSE_ACTION);
        } catch (ValidationException e) {
            notifications.create(Notifications.NotificationType.TRAY)
                    .withCaption("Validation error")
                    .withDescription(e.getMessage())
                    .show();
        }
    }

    /**
     * Creates either Remove or Exclude action depending on property type
     */
    protected Action.HasTarget createRemoveAction(MetaProperty metaProperty, Table table) {
        Action.HasTarget result;
        switch (metaProperty.getType()) {
            case COMPOSITION:
                result = new RemoveAction() {
                    //TODO handle security
                    @Override
                    protected boolean isPermitted() {
                        return true;
                    }
                };
                result.setTarget(table);
                break;
            case ASSOCIATION:
                result = new ExcludeAction() {
                    //TODO handle security
                    @Override
                    protected boolean isPermitted() {
                        return true;
                    }
                };
                result.setTarget(table);
                result.setShortcut(uiProperties.getTableRemoveShortcut());
                break;
            default:
                throw new IllegalArgumentException("property must contain an entity");
        }

        return result;
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

    protected class CommitAction extends AbstractAction {

        protected CommitAction() {
            super("commit", Status.PRIMARY);
        }

        @Override
        public void actionPerform(Component component) {
            commitAndClose();
        }
    }

    protected class CancelAction extends AbstractAction {

        protected CancelAction() {
            super("cancel");
        }

        @Override
        public void actionPerform(Component component) {
            close(WINDOW_COMMIT_AND_CLOSE_ACTION);
        }
    }

    /**
     * Opens entity inspector's editor to create entity
     */
    protected class CreateAction extends AbstractAction {

        private CollectionContainer entitiesDs;
        private MetaClass entityMeta;
        protected MetaProperty metaProperty;

        protected CreateAction(MetaProperty metaProperty, CollectionContainer entitiesDs, MetaClass entityMeta) {
            super("create");
            this.entitiesDs = entitiesDs;
            this.entityMeta = entityMeta;
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
                editorParams.put("parentDs", entitiesDs);
            }

            screens.create(EntityInspectorEditor.class, OPEN_TYPE, new MapScreenOptions(editorParams))
                    .show()
                    .addAfterCloseListener(afterCloseEvent -> {
                        if (COMMIT_ACTION_ID.equals(((StandardCloseAction) afterCloseEvent.getCloseAction()).getActionId())
                                && metaProperty.getType() == MetaProperty.Type.ASSOCIATION) {
                            EntityInspectorEditor screen = (EntityInspectorEditor) afterCloseEvent.getScreen();
                            entitiesDs.getMutableItems().add(screen.getItem());
                        }
                    });

        }
    }

    protected boolean attrViewPermitted(MetaClass metaClass, String property) {
        return attrPermitted(metaClass, property, EntityAttrAccess.VIEW);
    }

    protected class EditAction extends ItemTrackingAction {

        private Table entitiesTable;
        private CollectionContainer entitiesDs;
        private MetaProperty metaProperty;

        protected EditAction(MetaProperty metaProperty, Table entitiesTable, CollectionContainer entitiesDs) {
            super(entitiesTable, "edit");
            this.entitiesTable = entitiesTable;
            this.entitiesDs = entitiesDs;
            this.metaProperty = metaProperty;
        }

        @Override
        public void actionPerform(Component component) {
            Set selected = entitiesTable.getSelected();

            if (selected.size() != 1) {
                return;
            }

            Entity editItem = (Entity) selected.toArray()[0];
            Map<String, Object> editorParams = new HashMap<>();
            editorParams.put("metaClass", metadata.getClass(editItem));
            editorParams.put("item", editItem);
            editorParams.put("parent", item);
            editorParams.put("autocommit", Boolean.FALSE);
            MetaProperty inverseProperty = metaProperty.getInverse();
            if (inverseProperty != null) {
                editorParams.put("parentProperty", inverseProperty.getName());
            }
            if (metaProperty.getType() == MetaProperty.Type.COMPOSITION) {
                editorParams.put("parentDs", entitiesDs);
            }

            screens.create(EntityInspectorEditor.class, OPEN_TYPE, new MapScreenOptions(editorParams))
                    .show()
                    .addAfterCloseListener(afterCloseEvent -> {
                        EntityInspectorEditor screen = (EntityInspectorEditor) afterCloseEvent.getScreen();
                        entitiesDs.replaceItem(screen.getItem());
                    });

            //TODO replace item

//            Window window = openWindow("entityInspector.edit", OPEN_TYPE, editorParams);
//            window.addCloseListener(actionId -> entitiesDs.replaceItem());
        }
    }

    protected boolean attrViewPermitted(MetaClass metaClass, MetaProperty metaProperty) {
        return attrPermitted(metaClass, metaProperty.getName(), EntityAttrAccess.VIEW);
    }

    protected boolean attrPermitted(MetaClass metaClass, String property, EntityAttrAccess entityAttrAccess) {
        return security.isEntityAttrPermitted(metaClass, property, entityAttrAccess);
    }

    protected boolean entityOpPermitted(MetaClass metaClass, EntityOp entityOp) {
        return security.isEntityOpPermitted(metaClass, entityOp);
    }
}