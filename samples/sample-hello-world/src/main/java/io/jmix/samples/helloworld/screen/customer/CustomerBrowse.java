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

package io.jmix.samples.helloworld.screen.customer;

import io.jmix.core.LoadContext;
import io.jmix.samples.helloworld.data.CustomersRepository;
import io.jmix.samples.helloworld.data.api.ScreenDataRepository;
import io.jmix.samples.helloworld.entity.Customer;
import io.jmix.ui.Notifications;
import io.jmix.ui.screen.*;

import javax.inject.Inject;
import java.util.List;

@UiController("sample_Customer.browse")
@UiDescriptor("customer-browse.xml")
@LookupComponent("customersTable")
@LoadDataBeforeShow
public class CustomerBrowse extends StandardLookup<Customer> {

    @Inject
    CustomersRepository customersRepository;

    Notifications notifications;

    @Install(to = "customersDl", target = Target.DATA_LOADER)
    public List<Customer> loadData(LoadContext<Customer> loadContext) {
        List<Customer> customers = customersRepository.loadEntitiesList(loadContext);
        customers.addAll(customersRepository.findAllCustomers());
        return customers;
    }



}