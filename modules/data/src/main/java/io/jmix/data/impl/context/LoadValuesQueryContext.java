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

package io.jmix.data.impl.context;

import io.jmix.core.context.AccessContext;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaPropertyPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LoadValuesQueryContext implements AccessContext {
    protected final String queryString;

    protected boolean permitted = true;
    protected List<Integer> deniedSelectedIndexes;

    public LoadValuesQueryContext(String queryString) {
        this.queryString = queryString;
    }

    public Collection<MetaClass> getEntityClasses() {
        return null;

    }

    public Collection<MetaPropertyPath> getSelectedPropertyPaths() {
        return null;

    }

    public Collection<MetaPropertyPath> getAllPropertyPaths() {
        return null;

    }

    public void setDenied() {
        permitted = false;
    }

    public boolean isPermitted() {
        return permitted;
    }

    public List<Integer> getDeniedSelectedIndexes() {
        return deniedSelectedIndexes;
    }

    public int getSelectedIndex(MetaPropertyPath propertyPath) {
        return 0;
    }

    public void addDeniedSelectedIndex(int index) {
        if (deniedSelectedIndexes == null) {
            deniedSelectedIndexes = new ArrayList<>();
        }
        deniedSelectedIndexes.add(index);
    }

    //        queryParser.getQueryPaths().stream()
//                .filter(path -> !path.isSelectedPath())
//                .forEach(path -> {
//                    MetaClass metaClass = metadata.getClass(path.getEntityName());
//                    MetaPropertyPath propertyPath = metaClass.getPropertyPath(path.getPropertyPath());
//                    if (propertyPath == null) {
//                        throw new IllegalStateException(String.format("query path '%s' is unresolved", path.getFullPath()));
//                    }


//    List<Integer> indexes = new ArrayList<>();

//    int index = 0;
//        for (
//    QueryParser.QueryPath path : queryParser.getQueryPaths()) {
//        if (path.isSelectedPath()) {
//            MetaClass metaClass = metadata.getClass(path.getEntityName());
//            if (!Objects.equals(path.getPropertyPath(), path.getVariableName())
//                    && !isEntityAttrViewPermitted(metaClass.getPropertyPath(path.getPropertyPath()))) {
//                indexes.add(index);
//            }
//            index++;
//        }
//    }
}
