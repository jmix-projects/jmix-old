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

import com.haulmont.cuba.gui.components.OptionsField;
import com.haulmont.cuba.gui.xml.data.DatasourceLoaderHelper;
import io.jmix.ui.components.LookupField;
import io.jmix.ui.xml.layout.loaders.SearchFieldLoader;
import org.dom4j.Element;

public class CubaSearchFieldLoader extends SearchFieldLoader {

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadData(LookupField component, Element element) {
        super.loadData(component, element);

        DatasourceLoaderHelper.loadDatasourceAndOptions((OptionsField) resultComponent, element, getContext(),
                (ComponentLoaderContext) getComponentContext());
    }
}
