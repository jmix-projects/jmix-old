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

package com.haulmont.cuba.gui.xml.data;

import com.haulmont.cuba.gui.components.DatasourceComponent;
import com.haulmont.cuba.gui.components.OptionsField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.loaders.ComponentLoaderContext;
import io.jmix.ui.GuiDevelopmentException;
import io.jmix.ui.xml.layout.ComponentLoader;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

/**
 * Provides helper methods to load datasource and options datasource. Is used only in legacy component loaders.
 */
public class DatasourceLoaderHelper {

    /**
     * Load and set datasource to the component.
     *
     * @param component        to set datasource
     * @param element          component descriptor
     * @param context          loader context
     * @param componentContext component loader context
     */
    public static void loadDatasource(DatasourceComponent component, Element element, ComponentLoader.Context context,
                                      ComponentLoaderContext componentContext) {
        String datasource = element.attributeValue("datasource");
        if (!StringUtils.isEmpty(datasource)) {
            if (componentContext.getDsContext() == null) {
                throw new IllegalStateException("'datasource' attribute can be used only in screens with 'dsContext' element. " +
                        "In a screen with 'data' element use 'dataContainer' attribute.");
            }
            Datasource ds = componentContext.getDsContext().get(datasource);
            if (ds == null) {
                throw new GuiDevelopmentException(String.format("Datasource '%s' is not defined", datasource),
                        context, "Component ID", component.getId());
            }
            String property = element.attributeValue("property");
            if (StringUtils.isEmpty(property)) {
                throw new GuiDevelopmentException(
                        String.format("Can't set datasource '%s' for component '%s' because 'property' " +
                                "attribute is not defined", datasource, component.getId()), context);
            }

            component.setDatasource(ds, property);
        }
    }

    /**
     * Load and set options datasource to the field.
     *
     * @param field            field to set options datasource
     * @param element          field descriptor
     * @param componentContext component loader context
     */
    public static void loadOptionsDatasource(OptionsField field, Element element, ComponentLoaderContext componentContext) {
        String datasource = element.attributeValue("optionsDatasource");
        if (!StringUtils.isEmpty(datasource)) {
            Datasource ds = componentContext.getDsContext().get(datasource);
            field.setOptionsDatasource((CollectionDatasource) ds);
        }
    }
}
