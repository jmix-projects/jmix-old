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

import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.loaders.ComponentLoaderContext;
import io.jmix.ui.GuiDevelopmentException;
import io.jmix.ui.xml.layout.ComponentLoader;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import javax.annotation.Nullable;

/**
 * Provides helper methods to load datasource and options datasource. Is used only in legacy component loaders.
 */
@SuppressWarnings("rawtypes")
public final class DatasourceLoaderHelper {

    /**
     * Load and set datasource to the component.
     *
     * @param componentId      component id
     * @param element          component descriptor
     * @param context          loader context
     * @param componentContext component loader context
     */
    @Nullable
    public static Datasource loadDatasource(String componentId,
                                            Element element,
                                            ComponentLoader.Context context,
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
                        context, "Component ID", componentId);
            }
            return ds;
        }

        return null;
    }

    /**
     * Loads property for datasource. Throws GuiDevelopmentException if property is empty.
     *
     * @param componentId component id
     * @param element     component element
     * @param context     loader context
     * @return property for datasource
     */
    public static String loadProperty(String componentId, Element element, ComponentLoader.Context context) {
        String property = element.attributeValue("property");
        if (StringUtils.isEmpty(property)) {
            throw new GuiDevelopmentException(
                    String.format("'property' attribute for '%s' component is not defined", componentId),
                    context);
        }

        return property;
    }

    /**
     * Load and set options datasource to the field.
     *
     * @param element          field descriptor
     * @param componentContext component loader context
     */
    @Nullable
    public static CollectionDatasource loadOptionsDatasource(Element element, ComponentLoaderContext componentContext) {
        String datasource = element.attributeValue("optionsDatasource");
        if (!StringUtils.isEmpty(datasource)) {
            return (CollectionDatasource) componentContext.getDsContext().get(datasource);
        }

        return null;
    }

    /**
     * Loads table datasource from rows element.
     *
     * @param element       table descriptor
     * @param rowsElement   rows element descriptor
     * @param context       loader context
     * @param loaderContext component loader context
     * @return collection datasource or throws an exception
     */
    public static CollectionDatasource loadTableDatasource(Element element,
                                                           Element rowsElement,
                                                           ComponentLoader.Context context,
                                                           ComponentLoaderContext loaderContext) {
        String datasourceId = rowsElement.attributeValue("datasource");
        if (StringUtils.isBlank(datasourceId)) {
            throw new GuiDevelopmentException("Table 'rows' element doesn't have 'datasource' attribute",
                    context, "Table ID", element.attributeValue("id"));
        }

        return loadListComponentDatasource(datasourceId, context, loaderContext);
    }

    /**
     * Loads ListComponent datasource.
     *
     * @param datasourceId  datasource id
     * @param context       loader context
     * @param loaderContext component loader context
     * @return collection datasource or throws an exception
     */
    public static CollectionDatasource loadListComponentDatasource(String datasourceId,
                                                                   ComponentLoader.Context context,
                                                                   ComponentLoaderContext loaderContext) {
        Datasource datasource = loaderContext.getDsContext().get(datasourceId);
        if (datasource == null) {
            throw new GuiDevelopmentException("Can't find datasource by name: " + datasourceId, context);
        }

        if (!(datasource instanceof CollectionDatasource)) {
            throw new GuiDevelopmentException("Not a CollectionDatasource: " + datasourceId, context);
        }

        return (CollectionDatasource) datasource;
    }
}
