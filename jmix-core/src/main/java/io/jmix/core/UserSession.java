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

package io.jmix.core;

import io.jmix.core.entity.User;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.TimeZone;
import java.util.UUID;

public class UserSession {

    private User user;

    public UserSession(User user) {
        this.user = user;
    }

    public UUID getId() {
        return null;
    }

    public User getUser() {
        return user;
    }

    public TimeZone getTimeZone() {
        return null;
    }

    public boolean isSystem() {
        return false;
    }

    public String getAddress() {
        return null;
    }

    public String getClientInfo() {
        return null;
    }

    public <T> T getAttribute(String attributeName) {
        return null;
    }

    public void setAttribute(String name, Serializable value) {

    }
}
