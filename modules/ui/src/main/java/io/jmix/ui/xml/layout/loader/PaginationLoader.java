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

package io.jmix.ui.xml.layout.loader;

import io.jmix.ui.component.Frame;
import io.jmix.ui.component.Pagination;
import io.jmix.ui.model.BaseCollectionLoader;
import io.jmix.ui.model.ScreenData;
import io.jmix.ui.screen.UiControllerUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.Optional;

public class PaginationLoader extends AbstractComponentLoader<Pagination> {

    @Override
    public void createComponent() {
        resultComponent = factory.create(Pagination.NAME);
    }

    @Override
    public void loadComponent() {
        loadId(resultComponent, element);
        loadWidth(resultComponent, element);
        loadHeight(resultComponent, element);
        loadStyleName(resultComponent, element);
        loadVisible(resultComponent, element);
        loadAlign(resultComponent, element);
        loadCss(resultComponent, element);

        loadShowMaxResults(element)
                .ifPresent(resultComponent::setShowMaxResults);
        loadButtonsAlignment(element)
                .ifPresent(resultComponent::setButtonsAlignment);

        loadAutoLoad(element)
                .ifPresent(resultComponent::setAutoLoad);
        loadLoader(element)
                .ifPresent(resultComponent::setLoaderTarget);
    }

    protected Optional<BaseCollectionLoader> loadLoader(Element element) {
        String loaderId = element.attributeValue("dataLoader");
        if (StringUtils.isNotBlank(loaderId)) {
            Frame frame = getComponentContext().getFrame();
            ScreenData screenData = UiControllerUtils.getScreenData(frame.getFrameOwner());
            BaseCollectionLoader loader = screenData.getLoader(loaderId);
            return Optional.of(loader);
        }

        return Optional.empty();
    }

    protected Optional<Boolean> loadAutoLoad(Element element) {
        String autoLoad = element.attributeValue("autoLoad");
        if (StringUtils.isNotEmpty(autoLoad)) {
            return Optional.of(Boolean.parseBoolean(autoLoad));
        }
        return Optional.empty();
    }

    protected Optional<Pagination.ButtonsAlignment> loadButtonsAlignment(Element element) {
        String alignment = element.attributeValue("buttonsAlignment");
        if (StringUtils.isNotEmpty(alignment)) {
            return Optional.of(Pagination.ButtonsAlignment.valueOf(alignment));
        }
        return Optional.empty();
    }

    protected Optional<Boolean> loadShowMaxResults(Element element) {
        String maxResults = element.attributeValue("showMaxResults");
        if (StringUtils.isNotEmpty(maxResults)) {
            return Optional.of(Boolean.parseBoolean(maxResults));
        }
        return Optional.empty();
    }
}
