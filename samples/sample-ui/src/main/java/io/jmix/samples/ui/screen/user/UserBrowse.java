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

package io.jmix.samples.ui.screen.user;

import com.google.common.collect.Lists;
import io.jmix.security.entity.User;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.screen.*;

import javax.inject.Inject;

@UiController("sec_User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
//@LoadDataBeforeShow
public class UserBrowse extends StandardLookup<User> {

    @Inject
    private CollectionContainer<User> usersDc;

    @Subscribe
    private void onInit(InitEvent event) {
        User user1 = new User();
        user1.setActive(true);
        user1.setFirstName("asd");
        user1.setLastName("asxcvcx");

        User user2 = new User();
        user2.setActive(true);
        user2.setFirstName("asd");
        user2.setLastName("asxcvcx");

        usersDc.setItems(Lists.newArrayList(user1, user2));
    }

}
