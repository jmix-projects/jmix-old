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

package pagination_component;

import io.jmix.ui.component.Pagination;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.LoadDataBeforeShow;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import test_support.entity.sales.Customer;

@UiController
@UiDescriptor("pagination-component-test-screen.xml")
@LoadDataBeforeShow
public class PaginationComponentTestScreen extends Screen {

    @Autowired
    public Pagination pagination;

    @Autowired
    public Pagination paginationCustomMaxResults;

    @Autowired
    public Pagination paginationMaxResultsFalse;

    @Autowired
    public Pagination paginationMaxResultsTrue;

    @Autowired
    public CollectionLoader<Customer> customersLd;

    @Autowired
    public CollectionLoader<Customer> maxResultFalseCustomersLd;

    @Autowired
    public CollectionLoader<Customer> maxResultTrueCustomersLd;
}
