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

package io.jmix.ui.xml.layout.loaders;

import io.jmix.core.DataManager;
import io.jmix.core.FetchPlanRepository;
import io.jmix.core.LoadContext;
import io.jmix.core.QueryUtils;
import io.jmix.core.commons.util.ReflectionHelper;
import io.jmix.core.Entity;
import io.jmix.ui.GuiDevelopmentException;
import io.jmix.ui.components.Field;
import io.jmix.ui.components.SuggestionField;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public abstract class SuggestionFieldQueryLoader<T extends Field> extends AbstractFieldLoader<T> {

    protected void loadQuery(SuggestionField suggestionField, Element element) {
        Element queryElement = element.element("query");
        if (queryElement != null) {
            final boolean escapeValue;

            String stringQuery = queryElement.getStringValue();

            String searchFormat = queryElement.attributeValue("searchStringFormat");

            String view = queryElement.attributeValue("view");

            String escapeValueForLike = queryElement.attributeValue("escapeValueForLike");
            if (StringUtils.isNotEmpty(escapeValueForLike)) {
                escapeValue = Boolean.valueOf(escapeValueForLike);
            } else {
                escapeValue = false;
            }

            String entityClassName = queryElement.attributeValue("entityClass");
            if (StringUtils.isNotEmpty(entityClassName)) {
                DataManager dataManager = beanLocator.get(DataManager.NAME);
                suggestionField.setSearchExecutor((searchString, searchParams) -> {
                    Class<Entity> entityClass = ReflectionHelper.getClass(entityClassName);
                    if (escapeValue) {
                        searchString = QueryUtils.escapeForLike(searchString);
                    }
                    searchString = applySearchFormat(searchString, searchFormat);

                    LoadContext loadContext = new LoadContext(entityClass);
                    if (StringUtils.isNotEmpty(view)) {
                        loadContext.setFetchPlan(beanLocator.get(FetchPlanRepository.class).getFetchPlan(entityClass, view));
                    }
                    loadContext.setQuery(new LoadContext.Query(stringQuery).setParameter("searchString", searchString));

                    //noinspection unchecked
                    return dataManager.loadList(loadContext);
                });
            } else {
                throw new GuiDevelopmentException(String.format("Field 'entityClass' is empty in component %s.",
                        suggestionField.getId()), getContext());
            }
        }
    }

    protected String applySearchFormat(String searchString, String format) {
        if (StringUtils.isNotEmpty(format)) {
            // todo GStringTemplateEngine
//            GStringTemplateEngine engine = new GStringTemplateEngine();
//            StringWriter writer = new StringWriter();
//            try {
//                engine.createTemplate(format).make(ParamsMap.of("searchString", searchString)).writeTo(writer);
//                return writer.toString();
//            } catch (ClassNotFoundException | IOException e) {
//                throw new IllegalStateException(e);
//            }
        }
        return searchString;
    }
}
