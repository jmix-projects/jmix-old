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

import com.haulmont.cuba.client.ClientUserSession
import com.haulmont.cuba.client.testsupport.TestUserSessionSource
import io.jmix.core.app.PersistenceManagerService
import com.haulmont.cuba.security.global.UserSession
import io.jmix.ui.app
import com.haulmont.cuba.web.Connection
import com.haulmont.cuba.web.container.CubaTestContainer
import com.haulmont.cuba.web.testsupport.TestContainer
import com.haulmont.cuba.web.testsupport.proxy.TestServiceProxy
import com.haulmont.cuba.web.testsupport.ui.TestConnectorTracker
import com.haulmont.cuba.web.testsupport.ui.TestPersistenceManagerService
import com.haulmont.cuba.web.testsupport.ui.TestVaadinRequest
import com.haulmont.cuba.web.testsupport.ui.TestVaadinSession
import com.vaadin.server.VaadinSession
import com.vaadin.server.WebBrowser
import com.vaadin.ui.UI
import io.jmix.core.*
import io.jmix.ui.appUI
import io.jmix.ui.DefaultApp
import io.jmix.ui.UiComponents
import io.jmix.ui.model.DataComponents
import io.jmix.ui.sys.AppCookies
import io.jmix.ui.sys.ConnectionImpl
import io.jmix.ui.theme.ThemeConstants
import org.apache.commons.lang3.reflect.FieldUtils
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class WebSpec extends Specification {

    @Shared @ClassRule
    TestContainer cont = CubaTestContainer.Common.INSTANCE

    Metadata metadata
    MetadataTools metadataTools
    ViewRepository viewRepository
    EntityStates entityStates
    DataManager dataManager
    DataComponents dataComponents
    UiComponents uiComponents

    TestUserSessionSource sessionSource

    AppUI vaadinUi

    @SuppressWarnings("GroovyAccessibility")
    void setup() {
        metadata = cont.getBean(Metadata)
        metadataTools = cont.getBean(MetadataTools)
        viewRepository = cont.getBean(ViewRepository)
        entityStates = cont.getBean(EntityStates)
        dataManager = cont.getBean(DataManager)
        dataComponents = cont.getBean(DataComponents)
        uiComponents = cont.getBean(UiComponents)

        sessionSource = cont.getBean(UserSessionSource) as TestUserSessionSource

        def serverSession = sessionSource.createTestSession()
        session.setAuthenticated(false)

        sessionSource.setSession(session)

        // all the rest is required for web components

        def injectFactory = cont.getApplicationContext().getAutowireCapableBeanFactory()

        def app = new DefaultApp()
        app.themeConstants = new ThemeConstants([:])
        app.cookies = new AppCookies()

        def connection = new ConnectionImpl()
        injectFactory.autowireBean(connection)

        app.connection = connection

        def vaadinSession = new TestVaadinSession(new WebBrowser(), Locale.ENGLISH)

        vaadinSession.setAttribute(App.class, app)
        vaadinSession.setAttribute(App.NAME, app)
        vaadinSession.setAttribute(Connection.class, connection)
        vaadinSession.setAttribute(Connection.NAME, connection)
        vaadinSession.setAttribute(UserSession.class, sessionSource.getSession())

        VaadinSession.setCurrent(vaadinSession)

        injectFactory.autowireBean(app)

        vaadinUi = new AppUI()
        injectFactory.autowireBean(vaadinUi)

        def connectorTracker = new TestConnectorTracker(vaadinUi)
        FieldUtils.getDeclaredField(UI.class, "connectorTracker", true)
            .set(vaadinUi, connectorTracker)
        FieldUtils.getDeclaredField(UI.class, "session", true)
            .set(vaadinUi, vaadinSession)

        UI.setCurrent(vaadinUi)

        def vaadinRequest = new TestVaadinRequest()
        vaadinUi.getPage().init(vaadinRequest)
        vaadinUi.init(vaadinRequest)
    }

    void cleanup() {
        TestServiceProxy.clear()

        UI.setCurrent(null)

        sessionSource.setSession(null)
    }
}
