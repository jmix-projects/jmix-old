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

import com.haulmont.cuba.gui.components.data.table.DatasourceTreeTableItems;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import io.jmix.core.entity.Entity;
import io.jmix.ui.components.data.TableItems;

import javax.annotation.Nullable;

@Deprecated
@SuppressWarnings("rawtypes")
public interface TreeTable<E extends Entity> extends io.jmix.ui.components.TreeTable<E> {

    @Deprecated
    @Nullable
    default HierarchicalDatasource getDatasource() {
        TableItems<E> tableItems = getItems();
        if (tableItems == null) {
            return null;
        }

        if (tableItems instanceof DatasourceTreeTableItems) {
            DatasourceTreeTableItems adapter = (DatasourceTreeTableItems) tableItems;
            return (HierarchicalDatasource) adapter.getDatasource();
        }

        return null;
    }

    @Deprecated
    default void setDatasource(CollectionDatasource datasource) {
        if (datasource == null) {
            setItems(null);
        } else {
            if (!(datasource instanceof HierarchicalDatasource)) {
                throw new IllegalArgumentException("TreeTable supports only HierarchicalDatasource");
            }

            setItems(new DatasourceTreeTableItems((HierarchicalDatasource) datasource));
        }
    }

    @Deprecated
    default void setDatasource(HierarchicalDatasource datasource) {
        setDatasource((CollectionDatasource) datasource);
    }
}
