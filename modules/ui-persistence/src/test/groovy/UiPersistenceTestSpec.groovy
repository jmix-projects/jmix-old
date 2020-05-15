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

import io.jmix.core.BeanLocator
import io.jmix.core.JmixCoreConfiguration
import io.jmix.data.JmixDataConfiguration
import io.jmix.ui.JmixUiConfiguration
import io.jmix.ui.UiComponents
import io.jmix.ui.persistence.settings.ScreenSettingsJson
import io.jmix.ui.persistence.settings.ScreenSettingsManager
import io.jmix.ui.settings.ScreenSettings
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import test_support.JmixUiPersistenceTestConfiguration
import test_support.TestUiSettingsCache

import javax.inject.Inject

@ContextConfiguration(classes = [
        JmixCoreConfiguration,
        JmixUiConfiguration,
        JmixDataConfiguration,
        JmixUiPersistenceTestConfiguration])
class UiPersistenceTestSpec extends Specification {

    @Inject
    ScreenSettingsManager settingsManager

    @Inject
    UiComponents uiComponents

    @Inject
    TestUiSettingsCache settingsCache

    @Inject
    BeanLocator beanLocator

    ScreenSettings settings

    void setup() {
        reloadScreenSettings()
    }

    void cleanup() {
        settingsCache.clear()
    }

    protected reloadScreenSettings() {
        settings = beanLocator.getPrototype(ScreenSettingsJson.class, "screenId")
    }
}
