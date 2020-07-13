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

package io.jmix.data.impl.lazyloading;

import io.jmix.core.AppBeans;
import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.core.Metadata;
import io.jmix.core.metamodel.model.MetaClass;

public class JmixSingleValueHolder extends JmixAbstractValueHolder {
    protected Object entityId;
    protected String propertyName;
    protected Class valueClass;

    public JmixSingleValueHolder(String propertyName, Class valueClass, Object entityId) {
        this.propertyName = propertyName;
        this.valueClass = valueClass;
        this.entityId = entityId;
    }

    @Override
    public Object getValue() {
        if (!isInstantiated) {
            synchronized (this) {
                DataManager dataManager = AppBeans.get(DataManager.NAME);
                Metadata metadata = AppBeans.get(Metadata.NAME);
                MetaClass metaClass = metadata.getClass(valueClass);
                LoadContext lc = new LoadContext(metaClass);
                lc.setQueryString(String.format("select e from %s e where e.%s.id = :entityId",
                        metaClass.getName(), propertyName));
                lc.getQuery().setParameter("entityId", entityId);
                value = dataManager.load(lc);
                isInstantiated = true;
            }
        }
        return value;
    }

    @Override
    public Object clone() {
        return new JmixSingleValueHolder(propertyName, valueClass, entityId);
    }
}
