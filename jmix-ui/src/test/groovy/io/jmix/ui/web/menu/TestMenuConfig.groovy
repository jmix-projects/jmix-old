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

package io.jmix.ui.web.menu

import com.haulmont.bali.util.Dom4j
import io.jmix.core.Messages
import io.jmix.ui.config.MenuConfig
import io.jmix.ui.config.MenuItem

class TestMenuConfig extends MenuConfig {

    void setMessages(Messages messages) {
        this.@messages = messages
    }

    void loadTestMenu(String menuConfig) {
        this.@rootItems.clear()

        loadMenuItems(Dom4j.readDocument(menuConfig).rootElement, null)

        this.@initialized = true
    }

    @Override
    List<MenuItem> getRootItems() {
        return this.@rootItems
    }
}