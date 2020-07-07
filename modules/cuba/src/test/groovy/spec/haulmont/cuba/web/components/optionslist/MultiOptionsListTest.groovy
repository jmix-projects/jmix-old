/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package spec.haulmont.cuba.web.components.optionslist

import com.haulmont.cuba.core.model.sales.OrderLine
import com.haulmont.cuba.core.model.sales.Product
import io.jmix.ui.component.MultiOptionsList
import io.jmix.ui.screen.OpenMode
import spec.haulmont.cuba.web.UiScreenSpec
import spec.haulmont.cuba.web.components.optionslist.screens.OptionsListTestScreen

import java.util.function.Consumer

class MultiOptionsListTest extends UiScreenSpec {

    @SuppressWarnings(['GroovyAssignabilityCheck', 'GroovyAccessibility'])
    void setup() {
        exportScreensPackages(['spec.haulmont.cuba.web.components.optionslist.screens', 'com.haulmont.cuba.web.app.main'])
    }

    def 'List value is propagated to ValueSource from multiselect OptionsList'() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create('main', OpenMode.ROOT)
        screens.show(mainWindow)

        def screen = screens.create(OptionsListTestScreen)
        screen.show()

        def optionsList = screen.optionsList as MultiOptionsList<OrderLine>
        def orderLine = screen.allOrderLinesDc.getItems().get(0)
        def orderLinesDc = screen.orderLinesDc

        when: 'List value is set to OptionsList'
        optionsList.setValue([orderLine])

        then: 'ValueSource is updated'
        orderLinesDc.items.size() == 1 && orderLinesDc.items.contains(orderLine)
    }

    def 'List value is propagated to MultiOptionsList from ValueSource'() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create('main', OpenMode.ROOT)
        screens.show(mainWindow)

        def screen = screens.create(OptionsListTestScreen)
        screen.show()

        def optionsList = screen.optionsList as MultiOptionsList<OrderLine>
        def orderLine = screen.allOrderLinesDc.getItems().get(0)

        when: 'List value is set to ValueSource'
        screen.orderLinesDc.mutableItems.add(orderLine)

        then: 'OptionsList is updated'
        optionsList.value.size() == 1 && optionsList.value.contains(orderLine)
    }

    def 'Set value is propagated to ValueSource from MultiOptionsList'() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create('main', OpenMode.ROOT)
        screens.show(mainWindow)

        def screen = screens.create(OptionsListTestScreen)
        screen.show()

        def optionsList = screen.setOptionsList as MultiOptionsList<Product>
        def product = screen.allProductsDc.items.get(0)
        def catalog = screen.catalogDc.item

        when: 'Set value is set to OptionsList'
        optionsList.setValue(Collections.singleton(product))

        then: 'ValueSource is updated'
        catalog.products.size() == 1 && catalog.products.contains(product)
    }

    def 'ValueChangeEvent is fired exactly once for MultiOptionsList'() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create('main', OpenMode.ROOT)
        screens.show(mainWindow)

        def screen = screens.create(OptionsListTestScreen)
        screen.show()

        def optionsList = screen.optionsList as MultiOptionsList<OrderLine>
        def requiredOptionsList = screen.requiredOptionsList as MultiOptionsList<OrderLine>

        def valueChangeListener = Mock(Consumer)
        def requiredValueChangeListener = Mock(Consumer)

        optionsList.addValueChangeListener(valueChangeListener)
        requiredOptionsList.addValueChangeListener(requiredValueChangeListener)

        def order = screen.orderDc.item
        def orderLine = screen.orderLineDc.item

        def olOption = screen.allOrderLinesDc.items.get(0)
        def secondOlOption = screen.allOrderLinesDc.items.get(1)

        when: 'A value is set to MultiOptionsList'
        optionsList.setValue([olOption])

        then: 'ValueChangeEvent is fired once'
        1 * valueChangeListener.accept(_)
        1 * requiredValueChangeListener.accept(_)

        when: 'ValueSource is changed'
        screen.orderLinesDc.mutableItems.add(secondOlOption)

        then: 'ValueChangeEvent is fired once'
        1 * valueChangeListener.accept(_)
        1 * requiredValueChangeListener.accept(_)

        when: 'Entity property value is set to null'
        order.orderLines = null
        orderLine.product = null

        then: 'ValueChangeEvent is fired once'
        1 * valueChangeListener.accept(_)
        1 * requiredValueChangeListener.accept(_)
    }
}
