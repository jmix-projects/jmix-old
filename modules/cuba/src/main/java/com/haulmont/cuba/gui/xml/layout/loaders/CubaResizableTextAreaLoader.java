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
import com.haulmont.cuba.gui.xml.data.DatasourceLoaderHelper;
import com.haulmont.cuba.web.components.ResizableTextArea;
import io.jmix.ui.components.TextArea;
import io.jmix.ui.xml.layout.loaders.ResizableTextAreaLoader;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CubaResizableTextAreaLoader extends ResizableTextAreaLoader {

    private static final Logger log = LoggerFactory.getLogger(ResizableTextAreaLoader.class);

    @Override
    public void createComponent() {
        if (element.getName().equals(ResizableTextArea.NAME)) {
            resultComponent = factory.create(ResizableTextArea.NAME);
        }

        if (element.getName().equals(TextArea.NAME)) {
            if (isResizable() || hasResizableDirection()) {
                resultComponent = factory.create(ResizableTextArea.NAME);
                log.warn("The 'resizableTextArea' element must be used in order to create a resizable text area " +
                        "instead of 'textArea'");
            } else {
                resultComponent = factory.create(TextArea.NAME);
            }
        }

        loadId(resultComponent, element);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadData(TextArea component, Element element) {
        super.loadData(component, element);

        if (resultComponent.getValueSource() == null) {
            DatasourceLoaderHelper.loadDatasource((DatasourceComponent) resultComponent, element, getContext(),
                    (ComponentLoaderContext) getComponentContext());
        }
    }
}
