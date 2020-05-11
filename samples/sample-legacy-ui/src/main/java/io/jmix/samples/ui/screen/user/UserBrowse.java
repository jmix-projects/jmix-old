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

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import io.jmix.security.entity.User;

import javax.inject.Inject;
import java.util.UUID;

public class UserBrowse extends AbstractLookup {

    @Inject
    private CollectionDatasource<User, UUID> userDs;

    @Inject
    private GroupDatasource<User, UUID> groupUserDs;

    @Inject
    private SampleConfig sampleConfig;

    @Override
    protected void init(InitEvent initEvent) {
        System.out.println(">>> " + sampleConfig.getSampleProp());

        User user1 = new User();
        user1.setEnabled(true);
        user1.setFirstName("John");
        user1.setLastName("Doe");

        User user2 = new User();
        user2.setEnabled(true);
        user2.setFirstName("Katherine");
        user2.setLastName("Potter");

        userDs.addItem(user1);
        userDs.addItem(user2);

        groupUserDs.addItem(user1);
        groupUserDs.addItem(user2);
    }
}
