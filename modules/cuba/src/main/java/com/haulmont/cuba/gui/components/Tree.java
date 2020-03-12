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

import com.haulmont.cuba.gui.components.data.tree.DatasourceTreeItems;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import io.jmix.core.entity.Entity;
import io.jmix.ui.components.data.TreeItems;

@SuppressWarnings("rawtypes")
@Deprecated
public interface Tree<E extends Entity> extends io.jmix.ui.components.Tree<E> {

    @Deprecated
    default void setDatasource(HierarchicalDatasource datasource) {
        //noinspection unchecked
        setItems(datasource != null
                ? new DatasourceTreeItems(datasource)
                : null);
    }

    @Deprecated
    default HierarchicalDatasource getDatasource() {
        TreeItems<E> treeItems = getItems();
        return treeItems != null
                ? ((DatasourceTreeItems) treeItems).getDatasource()
                : null;
    }
}
