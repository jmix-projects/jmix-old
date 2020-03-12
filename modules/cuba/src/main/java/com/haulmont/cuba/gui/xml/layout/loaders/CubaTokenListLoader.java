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

package com.haulmont.cuba.gui.xml.layout.loaders;

import com.haulmont.cuba.gui.components.DatasourceComponent;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.options.DatasourceOptions;
import com.haulmont.cuba.gui.xml.data.DatasourceLoaderHelper;
import io.jmix.ui.xml.layout.loaders.TokenListLoader;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class CubaTokenListLoader extends TokenListLoader {

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadData(io.jmix.ui.components.TokenList component, Element element) {
        super.loadData(component, element);

        DatasourceLoaderHelper.loadDatasourceIfValueSourceNull((DatasourceComponent) resultComponent, element, getContext(),
                (ComponentLoaderContext) getComponentContext());
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadOptionsContainer(io.jmix.ui.components.TokenList component, Element lookupElement) {
        super.loadOptionsContainer(component, lookupElement);

        String datasource = lookupElement.attributeValue("optionsDatasource");
        if (!StringUtils.isEmpty(datasource)) {
            Datasource ds = ((ComponentLoaderContext) getComponentContext()).getDsContext().get(datasource);
            component.setOptions(new DatasourceOptions((CollectionDatasource) ds));
        }
    }
}
