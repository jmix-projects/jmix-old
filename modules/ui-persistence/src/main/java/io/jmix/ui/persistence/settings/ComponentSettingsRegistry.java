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
import io.jmix.ui.settings.component.TableSettings;
import io.jmix.ui.settings.component.worker.ComponentSettingsWorker;
import io.jmix.ui.settings.component.ComponentSettings;
import org.springframework.beans.factory.InitializingBean;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collects {@link ComponentSettingsWorker} and provides information for which component registered settings class.
 */
@org.springframework.stereotype.Component(ComponentSettingsRegistry.NAME)
public class ComponentSettingsRegistry implements InitializingBean {

    public static final String NAME = "jmix_ComponentSettingsRegistry";

    @Inject
    protected List<ComponentSettingsWorker> settings;

    protected Map<Class<? extends Component>, Class<? extends ComponentSettings>> classes = new ConcurrentHashMap<>();
    protected Map<Class<? extends ComponentSettings>, Class<? extends ComponentSettingsWorker>> settingsBeans = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() {
        for (ComponentSettingsWorker worker : settings) {
            register(worker);
        }
    }

    /**
     * @param componentClass component class (e.g. WebTable)
     * @return component settings class, otherwise it will throws an exception
     */
    public Class<? extends ComponentSettings> getSettingsClass(Class<? extends Component> componentClass) {
        Preconditions.checkNotNullArgument(componentClass);

        Class<? extends ComponentSettings> settingClass = classes.get(componentClass);
        if (settingClass != null) {
            return settingClass;
        }

        throw new IllegalStateException(String.format("Can't find settings class for '%s'", componentClass));
    }

    /**
     * @param settingsClass settings class (e.g. {@link TableSettings})
     * @return worker class if registered, otherwise in throws an exception
     */
    public Class<? extends ComponentSettingsWorker> getWorkerClass(Class<? extends ComponentSettings> settingsClass) {
        Preconditions.checkNotNullArgument(settingsClass);

        Class<? extends ComponentSettingsWorker> workerClass = settingsBeans.get(settingsClass);
        if (workerClass != null) {
            return workerClass;
        }

        throw new IllegalStateException(String.format("Cannot find worker class for '%s'", settingsClass));
    }

    /**
     * @param componentClass component class (e.g. WebTable)
     * @return true if settings is registered for component class
     */
    public boolean isSettingsRegisteredFor(Class<? extends Component> componentClass) {
        Class<? extends ComponentSettings> settingsClass = classes.get(componentClass);
        return settingsClass != null;
    }

    protected void register(ComponentSettingsWorker worker) {
        Preconditions.checkNotNullArgument(worker.getComponentClass(),
                "Component class cannot be null in '%s'", worker.getClass());
        Preconditions.checkNotNullArgument(worker.getSettingsClass(),
                "Settings class cannot be null in '%s'", worker.getClass());

        classes.put(worker.getComponentClass(), worker.getSettingsClass());
        settingsBeans.put(worker.getSettingsClass(), worker.getClass());
    }
}
