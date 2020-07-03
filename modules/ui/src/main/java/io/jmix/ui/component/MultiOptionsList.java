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

package io.jmix.ui.component;

import io.jmix.core.common.event.Subscription;

import java.util.Collection;
import java.util.EventObject;
import java.util.function.Consumer;

/**
 * List select component. Allows to select multiple values.
 *
 * @param <V> value and options type for the component
 */
public interface MultiOptionsList<V> extends OptionsField<Collection<V>, V>, Component.Focusable {
    String NAME = "multiOptionsList";

    /**
     * Adds a listener that is fired when user double-clicks on a list item.
     *
     * @param listener a listener to add
     */
    Subscription addDoubleClickListener(Consumer<DoubleClickEvent<V>> listener);

    /**
     * The event sent when the user double-clicks mouse on a list item.
     *
     * @param <V> item type
     */
    class DoubleClickEvent<V> extends EventObject {
        protected V item;

        public DoubleClickEvent(MultiOptionsList source, V item) {
            super(source);
            this.item = item;
        }

        @Override
        public MultiOptionsList<V> getSource() {
            return (MultiOptionsList<V>) super.getSource();
        }

        public V getItem() {
            return item;
        }
    }
}