/*
 * Copyright 2019 Haulmont.
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

package io.jmix.security.test;

import io.jmix.core.Metadata;
import io.jmix.core.MetadataTools;
import io.jmix.core.commons.db.QueryRunner;
import io.jmix.core.entity.Entity;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.data.Persistence;

import javax.inject.Inject;
import java.sql.SQLException;

public class TestSupport {

    @Inject
    private Metadata metadata;

    @Inject
    private MetadataTools metadataTools;

    @Inject
    private Persistence persistence;

    public void deleteRecord(Entity... entities) {
        if (entities == null)
            return;
        for (Entity entity : entities) {
            if (entity == null)
                continue;

            MetaClass metaClass = metadata.getClassNN(entity.getClass());

            String table = metadataTools.getDatabaseTable(metaClass);
            String primaryKey = metadataTools.getPrimaryKeyName(metaClass);
            if (table == null || primaryKey == null)
                throw new RuntimeException("Unable to determine table or primary key name for " + entity);

            deleteRecord(table, primaryKey, entity.getId());
        }
    }

    public void deleteRecord(String table, String primaryKeyCol, Object... ids) {
        for (Object id : ids) {
            String sql = "delete from " + table + " where " + primaryKeyCol + " = '" + id.toString() + "'";
            QueryRunner runner = new QueryRunner(persistence.getDataSource());
            try {
                runner.update(sql);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
