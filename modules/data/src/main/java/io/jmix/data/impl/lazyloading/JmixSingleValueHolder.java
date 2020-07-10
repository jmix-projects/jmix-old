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
import io.jmix.core.metamodel.model.MetaProperty;

public class JmixSingleValueHolder extends JmixAbstractValueHolder {

    protected DataManager dataManager = AppBeans.get(DataManager.NAME);
    protected Metadata metadata = AppBeans.get(Metadata.NAME);
    protected Object entityId;
    protected MetaProperty property;

    public JmixSingleValueHolder(MetaProperty property, Object entityId) {
        this.property = property;
        this.entityId = entityId;
    }

    @Override
    public Object getValue() {
        if (!isInstantiated) {
            synchronized (this) {
                MetaProperty inverseProperty = property.getInverse();
                MetaClass metaClass = inverseProperty.getDomain();
                LoadContext lc = new LoadContext(metaClass);
                lc.setQueryString(String.format("select e from %s e where e.%s.id = :entityId",
                        metaClass.getName(), inverseProperty.getName()));
                lc.getQuery().setParameter("entityId", entityId);
                value = dataManager.load(lc);
                isInstantiated = true;
            }
        }
        return value;
    }

    @Override
    public Object clone() {
        return new JmixSingleValueHolder(property, entityId);
    }
}
