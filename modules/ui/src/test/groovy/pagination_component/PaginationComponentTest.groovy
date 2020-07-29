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

package pagination_component

import com.vaadin.data.provider.Query
import io.jmix.core.CoreConfiguration
import io.jmix.core.DataManager
import io.jmix.data.DataConfiguration
import io.jmix.ui.UiConfiguration
import io.jmix.ui.testassist.spec.ScreenSpecification
import io.jmix.ui.widget.JmixPagination
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import test_support.DataContextTestConfiguration
import test_support.entity.sales.Customer

import java.util.stream.Collectors

@ContextConfiguration(classes = [CoreConfiguration, UiConfiguration, DataConfiguration, DataContextTestConfiguration])
class PaginationComponentTest extends ScreenSpecification {

    @Autowired
    JdbcTemplate jdbc

    @Autowired
    DataManager dataManager

    @Override
    void setup() {
        exportScreensPackages(["pagination_component"])

        dataManager.save(metadata.create(Customer), metadata.create(Customer), metadata.create(Customer))
    }

    @Override
    void cleanup() {
        jdbc.update("delete from TEST_CUSTOMER")
    }

    def "Pagination click on next button"() {
        given: "We have 3 pages"
        showTestMainScreen()

        when: "Click on 'next' button"
        def screen = getScreens().create(PaginationComponentTestScreen)
        screen.show()

        def vPagination = screen.pagination.unwrap(JmixPagination)
        vPagination.nextButton.click()

        then: "All buttons should be visible"
        vPagination.firstButton.isVisible()
        vPagination.prevButton.isVisible()

        vPagination.lastButton.isVisible()
        vPagination.nextButton.isVisible()
    }

    def "Pagination click on last button"() {
        given: "We have 3 pages"
        showTestMainScreen()

        when: "Click on 'last' button"
        def screen = getScreens().create(PaginationComponentTestScreen)
        screen.show()

        def vPagination = screen.pagination.unwrap(JmixPagination)
        vPagination.lastButton.click()

        then: "Last and next buttons should be hidden"
        vPagination.firstButton.isVisible()
        vPagination.prevButton.isVisible()

        !vPagination.lastButton.isVisible()
        !vPagination.nextButton.isVisible()
    }

    def "Pagination click on first button"() {
        given: "We have 3 pages"
        showTestMainScreen()

        when: "Set the last page on Pagination"
        def screen = getScreens().create(PaginationComponentTestScreen)
        screen.show()

        def vPagination = screen.pagination.unwrap(JmixPagination)
        vPagination.lastButton.click() // set the last page

        then: "First and previous buttons should be visible"
        vPagination.firstButton.isVisible()
        vPagination.prevButton.isVisible()

        when: "Click on 'first' button"
        vPagination.firstButton.click() // set the first page

        then: "First and previous buttons should be hidden"
        !vPagination.firstButton.isVisible()
        !vPagination.prevButton.isVisible()
    }

    def "Pagination click on previous button"() {
        given: "We have 3 pages"
        showTestMainScreen()

        when: "Click on 'next' button, then click on 'previous'"
        def screen = getScreens().create(PaginationComponentTestScreen)
        screen.show()

        def vPagination = screen.pagination.unwrap(JmixPagination)
        vPagination.nextButton.click()
        vPagination.prevButton.click() // return to the first page

        then: "First and previous buttons should be hidden"
        !vPagination.firstButton.isVisible()
        !vPagination.prevButton.isVisible()
    }

    def "Pagination with max result options"() {
        showTestMainScreen()

        when: "Load options with the order: 12, 9, 23, -6, 41, 0"
        def screen = getScreens().create(PaginationComponentTestScreen)
        screen.show()

        def vPagination = screen.paginationMaxResults.unwrap(JmixPagination)
        def maxResults = vPagination.maxResultComboBox.getDataProvider()
                .fetch(new Query<Integer, ?>())
                .collect(Collectors.toList())

        def expectedMaxResults = [9, 12, 23, 41]

        then: "Component should skip values less than or equal to 0"
        maxResults == expectedMaxResults
    }

    def "TablePagination in empty table with MetaClass"() {
        showTestMainScreen()

        when: "Load empty table with MetaClass"
        def screen = getScreens().create(PaginationEmptyTableTestScreen)
        screen.show()

        then: "No exception should be thrown and Pagination should be hidden"
        noExceptionThrown()

        def vPagination = screen.customerTable.getPagination().unwrap(JmixPagination)
        !vPagination.maxResultComboBox.isEnabled()
    }
}
