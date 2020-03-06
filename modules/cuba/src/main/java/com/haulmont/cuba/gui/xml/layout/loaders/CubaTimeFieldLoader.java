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

import com.haulmont.cuba.web.components.TimeField;
import io.jmix.ui.components.Field;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class CubaTimeFieldLoader extends AbstractFieldLoader<TimeField> {

    @Override
    public void createComponent() {
        resultComponent = factory.create(io.jmix.ui.components.TimeField.NAME);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
        super.loadComponent();

        loadResolution(resultComponent, element);
        loadTimeFormat(resultComponent, element);
        loadShowSeconds(resultComponent, element);
        loadTimeMode(resultComponent, element);

        loadDatatype(resultComponent, element);

        loadTabIndex(resultComponent, element);
        loadBuffered(resultComponent, element);
    }

    protected void loadResolution(io.jmix.ui.components.TimeField resultComponent, Element element) {
        String resolution = element.attributeValue("resolution");
        if (StringUtils.isNotEmpty(resolution)) {
            io.jmix.ui.components.TimeField.Resolution res = io.jmix.ui.components.TimeField.Resolution.valueOf(resolution);
            resultComponent.setResolution(res);
        }
    }

    protected void loadTimeFormat(io.jmix.ui.components.TimeField resultComponent, Element element) {
        String timeFormat = element.attributeValue("timeFormat");
        if (StringUtils.isNotEmpty(timeFormat)) {
            timeFormat = loadResourceString(timeFormat);
            resultComponent.setFormat(timeFormat);
        }
    }

    protected void loadShowSeconds(io.jmix.ui.components.TimeField resultComponent, Element element) {
        String showSeconds = element.attributeValue("showSeconds");
        if (StringUtils.isNotEmpty(showSeconds)) {
            resultComponent.setShowSeconds(Boolean.parseBoolean(showSeconds));
        }
    }

    protected void loadTimeMode(io.jmix.ui.components.TimeField resultComponent, Element element) {
        String timeMode = element.attributeValue("timeMode");
        if (StringUtils.isNotEmpty(timeMode)) {
            resultComponent.setTimeMode(io.jmix.ui.components.TimeField.TimeMode.valueOf(timeMode));
        }
    }

    @Override
    protected void loadValidators(Field component, Element element) {
        // don't load any validators
    }
}
