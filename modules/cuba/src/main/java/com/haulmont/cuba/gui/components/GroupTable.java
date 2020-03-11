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

import com.haulmont.cuba.gui.components.data.table.DatasourceGroupTableItems;
import com.haulmont.cuba.gui.components.data.table.DatasourceTableItems;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import io.jmix.core.entity.Entity;
import io.jmix.ui.components.data.TableItems;

@Deprecated
@SuppressWarnings("rawtypes")
public interface GroupTable<E extends Entity> extends io.jmix.ui.components.GroupTable<E> {

    @Deprecated
    default GroupDatasource getDatasource() {
        TableItems<E> tableItems = getItems();
        if (tableItems == null) {
            return null;
        }

        if (tableItems instanceof DatasourceTableItems) {
            DatasourceTableItems adapter = (DatasourceTableItems) tableItems;
            return (GroupDatasource) adapter.getDatasource();
        }

        return null;
    }

    @Deprecated
    default void setDatasource(CollectionDatasource datasource) {
        if (datasource == null) {
            setItems(null);
        } else {
            if (!(datasource instanceof GroupDatasource)) {
                throw new IllegalArgumentException("GroupTable supports only GroupDatasource");
            }

            setItems(new DatasourceGroupTableItems((GroupDatasource) datasource));
        }
    }
}
