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

package io.jmix.ui.persistence.settings.facet;

import com.google.common.base.Strings;
import io.jmix.core.commons.util.Preconditions;
import io.jmix.ui.components.Component;
import io.jmix.ui.components.Frame;
import io.jmix.ui.xml.FacetProvider;
import io.jmix.ui.xml.layout.ComponentLoader;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Component(ScreenSettingsFacetProvider.NAME)
public class ScreenSettingsFacetProvider implements FacetProvider<ScreenSettingsFacet> {

    public static final String NAME = "jmix_ui_persistence_ScreenSettingsFacetProvider";

    @Override
    public Class<ScreenSettingsFacet> getFacetClass() {
        return ScreenSettingsFacet.class;
    }

    @Override
    public ScreenSettingsFacet create() {
        return new WebScreenSettingsFacet();
    }

    @Override
    public String getFacetTag() {
        return "screenSettings";
    }

    @Override
    public void loadFromXml(ScreenSettingsFacet facet, Element element, ComponentLoader.ComponentContext context) {
        loadId(element).ifPresent(facet::setId);

        loadIncludeAll(element).ifPresent(facet::setIncludeAll);

        if (facet.isIncludeAll()) {
            List<Component> excludes = loadComponents(context, element, "exclude");
            facet.addExcludeComponents(excludes.toArray(new Component[0]));
        } else {
            List<Component> includes = loadComponents(context, element, "include");
            facet.addIncludeComponents(includes.toArray(new Component[0]));
        }
    }

    protected Optional<String> loadId(Element element) {
        String id = element.attributeValue("id");

        return Optional.ofNullable(id);
    }

    protected Optional<Boolean> loadIncludeAll(Element element) {
        String includeAll = element.attributeValue("includeAll");
        if (!Strings.isNullOrEmpty(includeAll)) {
            return Optional.of(Boolean.parseBoolean(includeAll));
        }

        return Optional.empty();
    }

    @SuppressWarnings("ConstantConditions")
    protected List<Component> loadComponents(ComponentLoader.ComponentContext context, Element root, String type) {
        List<Element> components = root.elements(type);
        Frame frame = context.getFrame();

        List<Component> result = new ArrayList<>(components.size());
        for (Element element : components) {

            String id = element.attributeValue("componentId");
            Preconditions.checkNotNullArgument(id, String.format("ScreenSettings `%s` component does not define an id", type));

            Component component = frame.getComponent(id);
            Preconditions.checkNotNullArgument(component, String.format("There is no component with '%s' id", id));

            result.add(component);
        }

        return result;
    }
}
