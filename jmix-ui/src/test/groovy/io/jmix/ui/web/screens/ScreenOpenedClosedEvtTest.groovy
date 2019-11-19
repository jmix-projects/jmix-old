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

package io.jmix.ui.web.screens

import io.jmix.ui.screen.FrameOwner
import io.jmix.ui.screen.OpenMode
import spec.cuba.web.UiScreenSpec
import spec.cuba.web.menu.commandtargets.TestWebBean

class ScreenOpenedClosedEvtTest extends UiScreenSpec {

    def 'ScreenOpenedEvent is fired'() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create("mainWindow", OpenMode.ROOT)
        screens.show(mainWindow)

        def testWebBean = cont.getBean(TestWebBean)

        when: 'Screen is opened'
        screens.create('sec$User.browse', OpenMode.NEW_TAB)
                .show()

        then: 'ScreenOpenedEvent is fired'
        testWebBean.screenOpenedEventHandled.get()
    }

    def 'ScreenClosedEvent is fired'() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create("mainWindow", OpenMode.ROOT)
        screens.show(mainWindow)

        def testWebBean = cont.getBean(TestWebBean)

        when: 'Screen is opened and then closed'
        def screen = screens.create('sec$User.browse', OpenMode.NEW_TAB)
        screen.show()
        screen.close(FrameOwner.WINDOW_CLOSE_ACTION)

        then: 'ScreenClosedEvent is fired'
        testWebBean.screenClosedEventHandled.get()
    }
}
