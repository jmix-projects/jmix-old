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

package io.jmix.samples.helloworld.role;

import io.jmix.samples.helloworld.entity.Customer;
import io.jmix.samples.helloworld.entity.Order;
import io.jmix.samples.helloworld.screen.customer.CustomerBrowse;
import io.jmix.samples.helloworld.screen.customer.CustomerEdit;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.model.RowLevelPolicyAction;
import io.jmix.security.role.annotation.*;
import io.jmix.securityui.role.annotation.ScreenPolicy;

import java.util.function.Predicate;

@Role(code = OrderViewRole.CODE, name = "Order view")
public interface OrderViewRole {

    String CODE = "orderView";

    @ScreenPolicy(screenIds = {"sample_Order.browse", "sample_Order.edit"})
    @EntityPolicy(entityClass = Order.class,
        actions = {EntityPolicyAction.READ})
    @EntityAttributePolicy(entityClass = Order.class,
        attributes = {"number", "date", "customer"},
        actions = {EntityAttributePolicyAction.UPDATE})
    @JpqlRowLevelPolicy(entityClass = Order.class,
        where = "{E}.number like 'A-%'")
    void order();

    @ScreenPolicy(screenClasses = {CustomerBrowse.class, CustomerEdit.class})
    @EntityPolicy(entityClass = Customer.class,
        actions = {EntityPolicyAction.ALL})
    @EntityPolicy(entityClass = Customer.class,
        actions = {EntityPolicyAction.READ},
            scope = "rest")
    @EntityAttributePolicy(entityClass = Customer.class,
        attributes = "*",
        actions = {EntityAttributePolicyAction.UPDATE})
    void customer();

    @PredicateRowLevelPolicy(entityClass = Order.class,
        actions = {RowLevelPolicyAction.READ})
    static Predicate<Order> readZeroOrdersOnly() {
        return order -> order.getNumber().startsWith("0");
    }
}
