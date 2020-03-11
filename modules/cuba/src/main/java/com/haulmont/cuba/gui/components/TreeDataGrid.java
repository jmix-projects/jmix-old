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

package com.haulmont.cuba.gui.components;

import com.haulmont.cuba.gui.components.data.datagrid.DatasourceTreeDataGridItems;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import io.jmix.core.entity.Entity;
import io.jmix.ui.components.data.DataGridItems;

@SuppressWarnings("rawtypes")
@Deprecated
public interface TreeDataGrid<E extends Entity> extends io.jmix.ui.components.TreeDataGrid<E> {

    @Deprecated
    default HierarchicalDatasource getDatasource() {
        DataGridItems<E> dataGridItems = getItems();
        return dataGridItems instanceof DatasourceTreeDataGridItems
                ? (HierarchicalDatasource) ((DatasourceTreeDataGridItems) dataGridItems).getDatasource()
                : null;
    }

    @Deprecated
    default void setDatasource(HierarchicalDatasource datasource) {
        setDatasource((CollectionDatasource) datasource);
    }

    @SuppressWarnings("unchecked")
    default void setDatasource(CollectionDatasource datasource) {
        if (datasource == null) {
            setItems(null);
        } else {
            if (!(datasource instanceof HierarchicalDatasource)) {
                throw new IllegalArgumentException("TreeDataGrid supports only HierarchicalDatasource");
            }

            setItems(new DatasourceTreeDataGridItems((HierarchicalDatasource) datasource));
        }
    }
}
