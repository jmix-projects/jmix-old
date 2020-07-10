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

import io.jmix.core.*;
import io.jmix.core.metamodel.model.MetaClass;
import org.eclipse.persistence.indirection.IndirectList;

public class JmixListValueHolder extends JmixAbstractValueHolder {

    protected DataManager dataManager = AppBeans.get(DataManager.NAME);
    protected Metadata metadata = AppBeans.get(Metadata.NAME);
    protected MetaClass ownerClass;
    protected Object entityId;
    protected String propertyName;

    public JmixListValueHolder(String propertyName, MetaClass ownerClass, Object entityId) {
        this.propertyName = propertyName;
        this.ownerClass = ownerClass;
        this.entityId = entityId;
    }

    @Override
    public Object getValue() {
        if (!isInstantiated) {
            synchronized (this) {
                FetchPlanBuilder fetchPlanBuilder = AppBeans.getPrototype(FetchPlanBuilder.NAME, ownerClass.getJavaClass());
                LoadContext lc = new LoadContext(ownerClass);
                lc.setFetchPlan(fetchPlanBuilder
                        .build()
                        .addProperty(propertyName));
                lc.setId(entityId);

                JmixEntity result = dataManager.load(lc);

                this.value = ((IndirectList) result.__getEntityEntry().getAttributeValue(propertyName))
                        .getValueHolder()
                        .getValue();
                isInstantiated = true;
            }
        }
        return value;
    }

    @Override
    public Object clone() {
        return new JmixListValueHolder(propertyName, ownerClass, entityId);
    }
}
