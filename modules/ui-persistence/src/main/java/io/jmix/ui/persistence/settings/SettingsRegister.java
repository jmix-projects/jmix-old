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

package io.jmix.ui.persistence.settings;

import io.jmix.core.commons.util.Preconditions;
import io.jmix.ui.components.Component;
import io.jmix.ui.settings.component.registration.SettingsRegistration;
import io.jmix.ui.settings.component.ComponentSettings;
import org.springframework.beans.factory.InitializingBean;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Component(SettingsRegister.NAME)
public class SettingsRegister implements InitializingBean {

    public static final String NAME = "jmix_ui_persistence_SettingsRegister";

    @Inject
    protected List<SettingsRegistration> settings;

    protected Map<Class<? extends Component>, String> classes = new ConcurrentHashMap<>();
    protected Map<String, Class<? extends ComponentSettings>> names = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() {
        for (SettingsRegistration setting : settings) {
            register(setting.getComponentName(), setting.getComponentClass(), setting.getSettingsClass());
        }
    }

    public Class<? extends ComponentSettings> getSettingsClass(Class<? extends Component> componentClass) {
        Preconditions.checkNotNullArgument(componentClass);

        String name = classes.get(componentClass);
        if (name != null) {
            return getSettingsClass(name);
        }

        throw new IllegalStateException(String.format("Can't find settings class for '%s'", componentClass));
    }

    public Class<? extends ComponentSettings> getSettingsClass(String componentName) {
        Preconditions.checkNotNullArgument(componentName);

        Class<? extends ComponentSettings> settingsClass = names.get(componentName);

        if (settingsClass == null) {
            throw new IllegalStateException(String.format("Can't find component settings class for '%s'", componentName));
        }

        return settingsClass;
    }

    protected void register(String componentName,
                            Class<? extends Component> componentClass,
                            Class<? extends ComponentSettings> settingsClass) {
        names.put(componentName, settingsClass);
        classes.put(componentClass, componentName);
    }
}
