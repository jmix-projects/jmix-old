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

package io.jmix.ui.persistence.settings.component;

import io.jmix.ui.settings.component.TableSettings;

public class TableSettingsImpl implements TableSettings {

    protected String id;
    protected Boolean textSelection;

    public TableSettingsImpl() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Boolean getTextSelection() {
        return textSelection;
    }

    @Override
    public void setTextSelection(Boolean textPresentation) {
        this.textSelection = textPresentation;
    }
}
