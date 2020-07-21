/*
 * Copyright (c) 2008-2016 Haulmont.
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
 *
 */
package io.jmix.uiexport.exporter.excel;

import io.jmix.core.Id;
import io.jmix.core.JmixEntity;
import io.jmix.core.entity.EntityValues;
import io.jmix.core.metamodel.datatype.Datatype;
import io.jmix.core.metamodel.datatype.Datatypes;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.core.metamodel.model.Range;
import io.jmix.ui.Notifications;
import io.jmix.ui.component.Table;
import io.jmix.ui.component.*;
import io.jmix.ui.component.data.GroupTableItems;
import io.jmix.ui.component.data.TableItems;
import io.jmix.ui.component.data.TreeTableItems;
import io.jmix.ui.download.ByteArrayDataProvider;
import io.jmix.ui.download.DownloadFormat;
import io.jmix.ui.download.Downloader;
import io.jmix.ui.gui.data.GroupInfo;
import io.jmix.ui.icon.JmixIcon;
import io.jmix.uiexport.exporter.AbstractTableExporter;
import io.jmix.uiexport.exporter.ExportMode;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Element;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Use this class to export {@link Table} into Excel format
 * and show using {@link Downloader}.
 * <br>Just create an instance of this class and invoke one of <code>exportTable</code> methods.
 */
@Component(ExcelExporter.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ExcelExporter extends AbstractTableExporter<ExcelExporter> {

    public static final String NAME = "export_ExcelExporter";

    public static enum ExportFormat {
        XLS,
        XLSX
    }

    protected static final int COL_WIDTH_MAGIC = 48;

    private static final int SPACE_COUNT = 10;

    public static final int MAX_ROW_COUNT = 65535;

    protected Workbook wb;

    protected Font boldFont;
    protected Font stdFont;
    protected Sheet sheet;

    protected CellStyle timeFormatCellStyle;
    protected CellStyle dateFormatCellStyle;
    protected CellStyle dateTimeFormatCellStyle;
    protected CellStyle integerFormatCellStyle;
    protected CellStyle doubleFormatCellStyle;

    protected ExcelAutoColumnSizer[] sizers;

    protected boolean exportAggregation = true;

    protected List<String> filterDescription;

    protected Boolean exportExpanded;

    protected ExportFormat exportFormat = ExportFormat.XLSX;

    protected boolean isRowNumberExceeded = false;

    protected void createWorkbookWithSheet() {
        switch (exportFormat) {
            case XLS:
                wb = new HSSFWorkbook();
                break;
            case XLSX:
                wb = new XSSFWorkbook();
                break;
            default:
                throw new IllegalStateException("Unknown export format " + exportFormat);
        }

        sheet = wb.createSheet("Export");
    }

    protected void createFonts() {
        stdFont = wb.createFont();
        boldFont = wb.createFont();
        boldFont.setBold(true);
    }

    protected void createAutoColumnSizers(int count) {
        sizers = new ExcelAutoColumnSizer[count];
    }

    @Override
    public void download(Downloader downloader, Table table, ExportMode exportMode) {
        if (downloader == null) {
            throw new IllegalArgumentException("ExportDisplay is null");
        }

        List<Table.Column> columns = table.getColumns();

        createWorkbookWithSheet();
        createFonts();
        createFormats();

        int r = 0;
        if (filterDescription != null) {
            for (r = 0; r < filterDescription.size(); r++) {
                String line = filterDescription.get(r);
                Row row = sheet.createRow(r);
                if (r == 0) {
                    RichTextString richTextFilterName = createStringCellValue(line);
                    richTextFilterName.applyFont(boldFont);
                    row.createCell(0).setCellValue(richTextFilterName);
                } else {
                    row.createCell(0).setCellValue(line);
                }
            }
            r++;
        }
        Row row = sheet.createRow(r);
        createAutoColumnSizers(columns.size());

        float maxHeight = sheet.getDefaultRowHeightInPoints();

        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        for (Table.Column column : columns) {
            String caption = column.getCaption();

            int countOfReturnSymbols = StringUtils.countMatches(caption, "\n");
            if (countOfReturnSymbols > 0) {
                maxHeight = Math.max(maxHeight, (countOfReturnSymbols + 1) * sheet.getDefaultRowHeightInPoints());
                headerCellStyle.setWrapText(true);
            }
        }
        row.setHeightInPoints(maxHeight);

        for (int c = 0; c < columns.size(); c++) {
            Table.Column column = columns.get(c);
            String caption = column.getCaption();

            Cell cell = row.createCell(c);
            RichTextString richTextString = createStringCellValue(caption);
            richTextString.applyFont(boldFont);
            cell.setCellValue(richTextString);

            ExcelAutoColumnSizer sizer = new ExcelAutoColumnSizer();
            sizer.notifyCellValue(caption, boldFont);
            sizers[c] = sizer;

            cell.setCellStyle(headerCellStyle);
        }

        TableItems<JmixEntity> tableItems = table.getItems();

        if (exportMode == ExportMode.SELECTED && table.getSelected().size() > 0) {
            Set<JmixEntity> selected = table.getSelected();

            List<JmixEntity> ordered = tableItems.getItemIds().stream()
                    .map(tableItems::getItem)
                    .filter(selected::contains)
                    .collect(Collectors.toList());
            for (JmixEntity item : ordered) {
                if (checkIsRowNumberExceed(r)) {
                    break;
                }

                createRow(table, columns, 0, ++r, Id.of(item).getValue());
            }
        } else {
            if (table.isAggregatable() && exportAggregation
                    && hasAggregatableColumn(table)) {
                if (table.getAggregationStyle() == Table.AggregationStyle.TOP) {
                    r = createAggregatableRow(table, columns, ++r, 1);
                }
            }
            if (table instanceof TreeTable) {
                TreeTable treeTable = (TreeTable) table;
                TreeTableItems treeTableSource = (TreeTableItems) treeTable.getItems();
                for (Object itemId : treeTableSource.getRootItemIds()) {
                    if (checkIsRowNumberExceed(r)) {
                        break;
                    }

                    r = createHierarchicalRow(treeTable, columns, exportExpanded, r, itemId);
                }
            } else if (table instanceof GroupTable && tableItems instanceof GroupTableItems
                    && ((GroupTableItems) tableItems).hasGroups()) {
                GroupTableItems groupTableSource = (GroupTableItems) tableItems;

                for (Object item : groupTableSource.rootGroups()) {
                    if (checkIsRowNumberExceed(r)) {
                        break;
                    }

                    r = createGroupRow((GroupTable) table, columns, ++r, (GroupInfo) item, 0);
                }
            } else {
                for (Object itemId : tableItems.getItemIds()) {
                    if (checkIsRowNumberExceed(r)) {
                        break;
                    }

                    createRow(table, columns, 0, ++r, itemId);
                }
            }
            if (table.isAggregatable() && exportAggregation
                    && hasAggregatableColumn(table)) {
                if (table.getAggregationStyle() == Table.AggregationStyle.BOTTOM) {
                    r = createAggregatableRow(table, columns, ++r, 1);
                }
            }
        }

        for (int c = 0; c < columns.size(); c++) {
            sheet.setColumnWidth(c, sizers[c].getWidth() * COL_WIDTH_MAGIC);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            wb.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write document", e);
        }

        if (isXlsMaxRowNumberExceeded()) {
            Notifications notifications = ComponentsHelper.getScreenContext(table).getNotifications();

            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(messages.getMessage("actions.warningExport.title"))
                    .withDescription(messages.getMessage("actions.warningExport.message"))
                    .show();
        }

        ByteArrayDataProvider dataProvider = new ByteArrayDataProvider(out.toByteArray());
        switch (exportFormat) {
            case XLSX:
                downloader.download(dataProvider, getFileName(table) + ".xlsx", DownloadFormat.XLSX);
                break;
            case XLS:
                downloader.download(dataProvider, getFileName(table) + ".xls", DownloadFormat.XLS);
                break;
        }
    }

    protected void createFormats() {
        timeFormatCellStyle = wb.createCellStyle();
        String timeFormat = getMessage("excelExporter.timeFormat");
        timeFormatCellStyle.setDataFormat(getBuiltinFormat(timeFormat));

        dateFormatCellStyle = wb.createCellStyle();
        String dateFormat = getMessage("excelExporter.dateFormat");
        dateFormatCellStyle.setDataFormat(getBuiltinFormat(dateFormat));

        dateTimeFormatCellStyle = wb.createCellStyle();
        String dateTimeFormat = getMessage("excelExporter.dateTimeFormat");
        dateTimeFormatCellStyle.setDataFormat(getBuiltinFormat(dateTimeFormat));

        integerFormatCellStyle = wb.createCellStyle();
        String integerFormat = getMessage("excelExporter.integerFormat");
        integerFormatCellStyle.setDataFormat(getBuiltinFormat(integerFormat));

        DataFormat format = wb.createDataFormat();
        doubleFormatCellStyle = wb.createCellStyle();
        String doubleFormat = getMessage("excelExporter.doubleFormat");
        doubleFormatCellStyle.setDataFormat(format.getFormat(doubleFormat));
    }

    private short getBuiltinFormat(String format) {
        return (short) BuiltinFormats.getBuiltinFormat(format);
    }

    private String getMessage(String id) {
        return messages.getMessage(id);
    }

    protected int createHierarchicalRow(TreeTable table, List<Table.Column> columns,
                                        Boolean exportExpanded, int rowNumber, Object itemId) {
        TreeTableItems treeTableSource = (TreeTableItems) table.getItems();
        createRow(table, columns, 0, ++rowNumber, itemId);
        if (BooleanUtils.isTrue(exportExpanded) && !table.isExpanded(itemId) && !treeTableSource.getChildren(itemId).isEmpty()) {
            return rowNumber;
        } else {
            Collection children = treeTableSource.getChildren(itemId);
            if (children != null && !children.isEmpty()) {
                for (Object id : children) {
                    if (BooleanUtils.isTrue(exportExpanded) && !table.isExpanded(id) && !treeTableSource.getChildren(id).isEmpty()) {
                        createRow(table, columns, 0, ++rowNumber, id);
                        continue;
                    }
                    rowNumber = createHierarchicalRow(table, columns, exportExpanded, rowNumber, id);
                }
            }
        }
        return rowNumber;
    }

    protected int createAggregatableRow(Table table, List<Table.Column> columns, int rowNumber,
                                        int aggregatableRow) {
        Row row = sheet.createRow(rowNumber);
        Map<Object, Object> results = table.getAggregationResults();

        int i = 0;
        for (Table.Column column : columns) {
            AggregationInfo agr = column.getAggregation();
            if (agr != null) {
                Object key = agr.getPropertyPath() != null ? agr.getPropertyPath() : column.getId();
                Object aggregationResult = results.get(key);
                if (aggregationResult != null) {
                    Cell cell = row.createCell(i);
                    formatValueCell(cell, aggregationResult, null, i, rowNumber, 0, null);
                }
            }
            i++;
        }
        return rowNumber;
    }

    protected int createGroupRow(GroupTable table, List<Table.Column> columns, int rowNumber,
                                 GroupInfo groupInfo, int groupNumber) {
        GroupTableItems<JmixEntity> groupTableSource = (GroupTableItems) table.getItems();

        Row row = sheet.createRow(rowNumber);
        Map<Object, Object> aggregations = table.isAggregatable()
                ? table.getAggregationResults(groupInfo)
                : Collections.emptyMap();

        int i = 0;
        int initialGroupNumber = groupNumber;
        for (Table.Column column : columns) {
            if (i == initialGroupNumber) {
                Cell cell = row.createCell(i);
                Object val = groupInfo.getValue();

                if (val == null) {
                    val = messages.getMessage(getClass(), "excelExporter.empty");
                }

                Collection children = groupTableSource.getGroupItemIds(groupInfo);
                if (children.isEmpty()) {
                    return rowNumber;
                }

                Integer groupChildCount = null;
                if (table.isShowItemsCountForGroup()) {
                    groupChildCount = children.size();
                }

                Object captionValue = val;

                Element xmlDescriptor = column.getXmlDescriptor();
                if (xmlDescriptor != null && StringUtils.isNotEmpty(xmlDescriptor.attributeValue("captionProperty"))) {
                    String captionProperty = xmlDescriptor.attributeValue("captionProperty");

                    Object itemId = children.iterator().next();
                    JmixEntity item = groupTableSource.getItemNN(itemId);
                    captionValue = EntityValues.getValue(item, captionProperty);
                }

                GroupTable.GroupCellValueFormatter<JmixEntity> groupCellValueFormatter =
                        table.getGroupCellValueFormatter();

                if (groupCellValueFormatter != null) {
                    // disable separate "(N)" printing
                    groupChildCount = null;

                    List<JmixEntity> groupItems = ((Collection<Object>) groupTableSource.getGroupItemIds(groupInfo)).stream()
                            .map(groupTableSource::getItem)
                            .collect(Collectors.toList());

                    GroupTable.GroupCellContext<JmixEntity> cellContext = new GroupTable.GroupCellContext<>(
                            groupInfo, captionValue, metadataTools.format(captionValue), groupItems
                    );

                    captionValue = groupCellValueFormatter.format(cellContext);
                }

                MetaPropertyPath columnId = (MetaPropertyPath) column.getId();
                formatValueCell(cell, captionValue, columnId, groupNumber++, rowNumber, 0, groupChildCount);
            } else {
                AggregationInfo agr = column.getAggregation();
                if (agr != null) {
                    Object key = agr.getPropertyPath() != null ? agr.getPropertyPath() : column.getId();
                    Object aggregationResult = aggregations.get(key);
                    if (aggregationResult != null) {
                        Cell cell = row.createCell(i);
                        formatValueCell(cell, aggregationResult, null, i, rowNumber, 0, null);
                    }
                }
            }

            i++;
        }

        int oldRowNumber = rowNumber;
        List<GroupInfo> children = groupTableSource.getChildren(groupInfo);
        if (children.size() > 0) {
            for (GroupInfo child : children) {
                rowNumber = createGroupRow(table, columns, ++rowNumber, child, groupNumber);
            }
        } else {
            Collection<?> itemIds = groupTableSource.getGroupItemIds(groupInfo);
            for (Object itemId : itemIds) {
                createRow(table, columns, groupNumber, ++rowNumber, itemId);
            }
        }

        if (checkIsRowNumberExceed(rowNumber)) {
            sheet.groupRow(oldRowNumber + 1, MAX_ROW_COUNT);
        } else {
            sheet.groupRow(oldRowNumber + 1, rowNumber);
        }

        return rowNumber;
    }

    protected void createRow(Table table, List<Table.Column> columns, int startColumn, int rowNumber, Object itemId) {
        if (startColumn >= columns.size()) {
            return;
        }

        if (rowNumber > MAX_ROW_COUNT) {
            return;
        }

        Row row = sheet.createRow(rowNumber);
        JmixEntity instance = (JmixEntity) table.getItems().getItem(itemId);

        int level = 0;
        if (table instanceof TreeTable) {
            level = ((TreeTable) table).getLevel(itemId);
        }

        for (int c = startColumn; c < columns.size(); c++) {
            Cell cell = row.createCell(c);

            Table.Column column = columns.get(c);
            MetaPropertyPath propertyPath = null;
            if (column.getId() instanceof MetaPropertyPath) {
                propertyPath = (MetaPropertyPath) column.getId();
            }

            Object cellValue = getColumnValue(table, column, instance);

            formatValueCell(cell, cellValue, propertyPath, c, rowNumber, level, null);
        }
    }

    protected String createSpaceString(int level) {
        if (level == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level * SPACE_COUNT; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    protected void formatValueCell(Cell cell, @Nullable Object cellValue, @Nullable MetaPropertyPath metaPropertyPath,
                                   int sizersIndex, int notificationRequired, int level, @Nullable Integer groupChildCount) {

        if (cellValue == null) {
            if (metaPropertyPath != null
                    && metaPropertyPath.getRange().isDatatype()) {
                Class javaClass = metaPropertyPath.getRange().asDatatype().getJavaClass();
                if (Boolean.class.equals(javaClass)) {
                    cellValue = false;
                }
            } else {
                return;
            }
        }

        String childCountValue = "";
        if (groupChildCount != null) {
            childCountValue = " (" + groupChildCount + ")";
        }

        if (cellValue instanceof Number) {
            Number n = (Number) cellValue;
            Datatype datatype = null;
            if (metaPropertyPath != null) {
                Range range = metaPropertyPath.getMetaProperty().getRange();
                if (range.isDatatype()) {
                    datatype = range.asDatatype();
                }
            }

            datatype = datatype == null ? Datatypes.getNN(n.getClass()) : datatype;
            String str;
            // level is used for TreeTable, so level with 0 doesn't create spacing
            // and we should skip it
            if (sizersIndex == 0 && level > 0) {
                str = createSpaceString(level) + datatype.format(n);
                cell.setCellValue(str);
            } else {
                try {
                    str = datatype.format(n);
                    Number result = (Number) datatype.parse(str);
                    if (result != null) {
                        if (n instanceof Integer || n instanceof Long || n instanceof Byte || n instanceof Short) {
                            cell.setCellValue(result.longValue());
                            cell.setCellStyle(integerFormatCellStyle);
                        } else {
                            cell.setCellValue(result.doubleValue());
                            cell.setCellStyle(doubleFormatCellStyle);
                        }
                    }
                } catch (ParseException e) {
                    throw new RuntimeException("Unable to parse numeric value", e);
                }
                cell.setCellType(CellType.NUMERIC);
            }
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof Date) {
            Class javaClass = null;
            if (metaPropertyPath != null) {
                MetaProperty metaProperty = metaPropertyPath.getMetaProperty();
                if (metaProperty.getRange().isDatatype()) {
                    javaClass = metaProperty.getRange().asDatatype().getJavaClass();
                }
            }
            Date date = (Date) cellValue;

            cell.setCellValue(date);

            if (Objects.equals(java.sql.Time.class, javaClass)) {
                cell.setCellStyle(timeFormatCellStyle);
            } else if (Objects.equals(java.sql.Date.class, javaClass)) {
                cell.setCellStyle(dateFormatCellStyle);
            } else {
                cell.setCellStyle(dateTimeFormatCellStyle);
            }
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                String str = Datatypes.getNN(Date.class).format(date);
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof Boolean) {
            String str = "";
            if (sizersIndex == 0) {
                str += createSpaceString(level);
            }
            str += ((Boolean) cellValue) ? getMessage("excelExporter.true") : getMessage("excelExporter.false");
            cell.setCellValue(createStringCellValue(str));
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof Enum) {
            String message = (sizersIndex == 0 ? createSpaceString(level) : "") +
                    messages.getMessage((Enum) cellValue);

            cell.setCellValue(message + childCountValue);
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(message, stdFont);
            }
        } else if (cellValue instanceof JmixEntity) {
            JmixEntity entityVal = (JmixEntity) cellValue;
            String instanceName = metadataTools.getInstanceName(entityVal);
            String str = sizersIndex == 0 ? createSpaceString(level) + instanceName : instanceName;
            str = str + childCountValue;
            cell.setCellValue(createStringCellValue(str));
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else if (cellValue instanceof Collection) {
            String str = "";
            cell.setCellValue(createStringCellValue(str));
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        } else {
            String strValue = cellValue == null ? "" : cellValue.toString();
            String str = sizersIndex == 0 ? createSpaceString(level) + strValue : strValue;
            str = str + childCountValue;
            cell.setCellValue(createStringCellValue(str));
            if (sizers[sizersIndex].isNotificationRequired(notificationRequired)) {
                sizers[sizersIndex].notifyCellValue(str, stdFont);
            }
        }
    }

    private RichTextString createStringCellValue(String str) {
        switch (exportFormat) {
            case XLSX:
                return new XSSFRichTextString(str);
            case XLS:
                return new HSSFRichTextString(str);
        }
        throw new IllegalStateException("Unknown export format " + exportFormat);

    }

    protected boolean checkIsRowNumberExceed(int r) {
        return isRowNumberExceeded = exportFormat == ExportFormat.XLS && r >= MAX_ROW_COUNT;
    }

    /**
     * @return true if exported table contains more than 65536 records
     */
    public boolean isXlsMaxRowNumberExceeded() {
        return isRowNumberExceeded;
    }

    public void setExportAggregation(boolean exportAggregation) {
        this.exportAggregation = exportAggregation;
    }

    public boolean getExportAggregation() {
        return exportAggregation;
    }

    /**
     * Checks that at least one column in table is aggregatable.
     *
     * @param table table
     * @return true if at least one column is aggregatable
     */
    protected boolean hasAggregatableColumn(Table table) {
        List<Table.Column> columns = table.getColumns();
        for (Table.Column column : columns) {
            if (column.getAggregation() != null) {
                return true;
            }
        }
        return false;
    }

    public List<String> getFilterDescription() {
        return filterDescription;
    }

    public void setFilterDescription(List<String> filterDescription) {
        this.filterDescription = filterDescription;
    }

    public ExcelExporter withFilterDescription(List<String> filterDescription) {
        setFilterDescription(filterDescription);
        return this;
    }

    public Boolean getExportExpanded() {
        return exportExpanded;
    }

    public void setExportExpanded(Boolean exportExpanded) {
        this.exportExpanded = exportExpanded;
    }

    public ExcelExporter withExportExpanded(Boolean exportExpanded) {
        setExportExpanded(exportExpanded);
        return this;
    }

    @Override
    public String getCaption() {
        return getMessage("excelExporter.caption");
    }

    @Override
    public String getIcon() {
        return JmixIcon.EXCEL_ACTION.source();
    }
}