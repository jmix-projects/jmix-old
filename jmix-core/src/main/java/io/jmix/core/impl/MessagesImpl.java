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

package io.jmix.core.impl;

import io.jmix.core.MessageTools;
import io.jmix.core.Messages;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Locale;

// todo impl
@Component
public class MessagesImpl implements Messages {
    @Override
    public MessageTools getTools() {
        return null;
    }

    @Override
    public String getMainMessagePack() {
        return null;
    }

    @Override
    public String getMessage(Class caller, String key) {
        return null;
    }

    @Override
    public String formatMessage(Class caller, String key, Object... params) {
        return null;
    }

    @Override
    public String getMessage(Class caller, String key, Locale locale) {
        return null;
    }

    @Override
    public String formatMessage(Class caller, String key, Locale locale, Object... params) {
        return null;
    }

    @Override
    public String getMessage(Enum caller) {
        return null;
    }

    @Override
    public String getMessage(Enum caller, Locale locale) {
        return null;
    }

    @Override
    public String getMessage(String pack, String key) {
        return null;
    }

    @Override
    public String getMainMessage(String key) {
        return null;
    }

    @Override
    public String getMainMessage(String key, Locale locale) {
        return null;
    }

    @Override
    public String formatMessage(String pack, String key, Object... params) {
        return null;
    }

    @Override
    public String formatMainMessage(String key, Object... params) {
        return null;
    }

    @Override
    public String getMessage(String packs, String key, Locale locale) {
        return null;
    }

    @Nullable
    @Override
    public String findMessage(String packs, String key, @Nullable Locale locale) {
        return null;
    }

    @Override
    public String formatMessage(String pack, String key, Locale locale, Object... params) {
        return null;
    }

    @Override
    public int getCacheSize() {
        return 0;
    }

    @Override
    public void clearCache() {

    }
}
