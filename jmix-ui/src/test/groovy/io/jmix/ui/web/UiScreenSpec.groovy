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

package io.jmix.ui.web

import io.jmix.ui.sys.UiControllersConfiguration

@SuppressWarnings(["GroovyAccessibility", "GroovyAssignabilityCheck"])
class UiScreenSpec extends WebSpec {

    void setup() {
        def clientCacheManager = cont.getBean(ClientCacheManager)

// TODO VM
//        TestServiceProxy.mock(UserManagementService, Mock(UserManagementService) {
//            getSubstitutedUsers(_) >> Collections.emptyList()
//        })
    }

    protected void exportScreensPackages(List<String> packages) {
        def windowConfig = cont.getBean(WindowConfig)

        def configuration = new UiControllersConfiguration()
        def injector = cont.getApplicationContext().getAutowireCapableBeanFactory()
        injector.autowireBean(configuration)
        configuration.basePackages = packages

        windowConfig.configurations = [configuration]
        windowConfig.initialized = false
    }

    protected void resetScreensConfig() {
        def windowConfig = cont.getBean(WindowConfig)
        windowConfig.configurations = []
        windowConfig.initialized = false
    }

    void cleanup() {

        resetScreensConfig()

// TODO VM
//        def userSessionSource = (TestUserSessionSource) cont.getBean(UserSessionSource.class)
//        userSessionSource.setSession(null)
    }
}