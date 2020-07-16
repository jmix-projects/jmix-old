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

package io.jmix.ui.component.impl;

import io.jmix.ui.component.MultiSelectList;
import io.jmix.ui.widget.listselect.JmixMultiListSelect;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebMultiSelectList<V> extends WebAbstractSelectList<Collection<V>, V, JmixMultiListSelect<V>>
        implements MultiSelectList<V> {

    protected JmixMultiListSelect<V> createComponent() {
        return new JmixMultiListSelect<>();
    }

    @Override
    protected Collection<V> convertToModel(Set<V> componentRawValue) {
        Stream<V> items = optionsBinding == null ? Stream.empty()
                : optionsBinding.getSource().getOptions().filter(componentRawValue::contains);

        if (valueBinding != null) {
            Class<Collection<V>> targetType = valueBinding.getSource().getType();

            if (List.class.isAssignableFrom(targetType)) {
                return items.collect(Collectors.toList());
            }

            if (Set.class.isAssignableFrom(targetType)) {
                return items.collect(Collectors.toCollection(LinkedHashSet::new));
            }
        }

        return items.collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    protected boolean fieldValueEquals(Collection<V> value, Collection<V> oldValue) {
        return equalCollections(value, oldValue);
    }

    protected boolean equalCollections(Collection<V> a, Collection<V> b) {
        if (CollectionUtils.isEmpty(a) && CollectionUtils.isEmpty(b)) {
            return true;
        }

        if ((CollectionUtils.isEmpty(a) && CollectionUtils.isNotEmpty(b))
                || (CollectionUtils.isNotEmpty(a) && CollectionUtils.isEmpty(b))) {
            return false;
        }

        return CollectionUtils.isEqualCollection(a, b);
    }

    @Override
    protected Set<V> convertToPresentation(Collection<V> modelValue) {
        if (modelValue instanceof List) {
            return new LinkedHashSet<>(modelValue);
        }

        return modelValue == null ?
                new LinkedHashSet<>() : new LinkedHashSet<>(modelValue);
    }

    @Override
    public void setValue(Collection<V> value) {
        Collection<V> oldValue = getOldValue(value);

        oldValue = new ArrayList<>(oldValue != null
                ? oldValue
                : Collections.emptyList());

        setValueToPresentation(convertToPresentation(value));

        this.internalValue = value;

        fireValueChange(oldValue, value);
    }

    protected Collection<V> getOldValue(Collection<V> newValue) {
        return equalCollections(newValue, internalValue)
                ? component.getValue()
                : internalValue;
    }

    @Override
    protected Collection<V> getCollectionValue() {
        return getValue();
    }

    protected void fireValueChange(Collection<V> oldValue, Collection<V> value) {
        if (!fieldValueEquals(oldValue, value)) {
            ValueChangeEvent<Collection<V>> event =
                    new ValueChangeEvent<>(this, oldValue, value, false);
            publish(ValueChangeEvent.class, event);
        }
    }
}
