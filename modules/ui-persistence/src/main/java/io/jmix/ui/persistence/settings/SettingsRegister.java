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

import io.jmix.ui.components.Component;
import io.jmix.ui.persistence.settings.component.AbstractSettings;
import io.jmix.ui.persistence.settings.component.register.SettingsRegistration;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Component(SettingsRegister.NAME)
public class SettingsRegister implements InitializingBean {

    public static final String NAME = "jmix_ui_persistence_SettingsRegister";

    @Inject
    protected List<SettingsRegistration> settings;

    protected Map<Class<? extends Component>, String> classes = new ConcurrentHashMap<>();
    protected Map<String, Class<? extends AbstractSettings>> names = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() {
        for (SettingsRegistration setting : settings) {
            register(setting.getComponentName(), setting.getComponentClass(), setting.getSettingsClass());
        }
    }

    /**
     *
     * @param componentClass
     * @param json
     * @return
     */
    public AbstractSettings create(Class<? extends Component> componentClass, JSONObject json) {
        String name = classes.get(componentClass);
        if (name != null) {
            return create(name, json);
        }

        throw new IllegalStateException(String.format("Can't find settings class for '%s'", componentClass));
    }

    /**
     *
     * @param name
     * @param json
     * @return
     */
    public AbstractSettings create(String name, JSONObject json) {
        Class<? extends AbstractSettings> settingsClass = names.get(name);

        if (settingsClass == null) {
            throw new IllegalStateException(String.format("Can't find component settings class for '%s'", name));
        }

        Constructor<? extends AbstractSettings> constructor;
        try {
            constructor = settingsClass.getConstructor(JSONObject.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format("Unable to get constructor for '%s' component settings", name), e);
        }

        try {
            return constructor.newInstance(json);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(String.format("Error creating the '%s' component settings instance", name), e);
        }
    }

    /**
     *
     * @param settingsClass
     * @param json
     * @return
     */
    public AbstractSettings createSettings(Class<? extends AbstractSettings> settingsClass, JSONObject json) {
        Constructor<? extends AbstractSettings> constructor;
        try {
            constructor = settingsClass.getConstructor(JSONObject.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format("Unable to get constructor for '%s'", settingsClass), e);
        }

        try {
            return constructor.newInstance(json);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(String.format("Error creating the '%s' instance", settingsClass), e);
        }
    }

    protected void register(String componentName,
                         Class<? extends Component> componentClass,
                         Class<? extends AbstractSettings> settingsClass) {
        names.put(componentName, settingsClass);
        classes.put(componentClass, componentName);
    }
}
