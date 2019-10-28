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

package io.jmix.ui.web.clipboardtrigger

import com.haulmont.cuba.gui.components.ClipboardTrigger
import com.haulmont.cuba.gui.screen.OpenMode
import com.haulmont.cuba.web.app.main.MainScreen
import spec.cuba.web.UiScreenSpec
import spec.cuba.web.clipboardtrigger.screens.ScreenWithClipboardTrigger

@SuppressWarnings(["GroovyAccessibility", "GroovyAssignabilityCheck"])
class ClipboardTriggerFacetTest extends UiScreenSpec {

    def setup() {
        exportScreensPackages(['spec.cuba.web.clipboardtrigger.screens', 'com.haulmont.cuba.web.app.main'])
    }

    def "open screen with ClipboardTrigger"() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create(MainScreen, OpenMode.ROOT)
        screens.show(mainWindow)

        when:

        def screen = screens.create(ScreenWithClipboardTrigger)
        screen.show()

        then:

        screen.window.getFacet('copyTrigger') instanceof ClipboardTrigger
        screen.window.facets.count() == 1

        screen.copyTrigger != null
        screen.copyTrigger.button != null
        screen.copyTrigger.input != null
    }
}