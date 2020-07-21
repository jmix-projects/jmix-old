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

package io.jmix.uiexport.exporter;

import io.jmix.core.JmixEntity;
import io.jmix.ui.component.Table;
import io.jmix.ui.component.formatter.Formatter;

import java.util.Map;

public class ExportContext {

    protected String fileName;

    protected Table<JmixEntity> table;

    protected Map<String, Formatter> formatters;

    public ExportContext(Table<JmixEntity> table) {
        this.table = table;
    }

    public Table<JmixEntity> getTable() {
        return table;
    }

    public void setTable(Table<JmixEntity> table) {
        this.table = table;
    }

    public Map<String, Formatter> getFormatters() {
        return formatters;
    }

    public void setFormatters(Map<String, Formatter> formatters) {
        this.formatters = formatters;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
