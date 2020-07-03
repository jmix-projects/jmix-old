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

import com.vaadin.data.provider.ListDataProvider;
import io.jmix.core.common.event.Subscription;
import io.jmix.core.MetadataTools;
import io.jmix.ui.component.MultiOptionsList;
import io.jmix.ui.component.data.DataAwareComponentsTools;
import io.jmix.ui.component.data.Options;
import io.jmix.ui.component.data.ValueSource;
import io.jmix.ui.component.data.meta.EntityValueSource;
import io.jmix.ui.component.data.meta.OptionsBinding;
import io.jmix.ui.component.data.options.OptionsBinder;
import io.jmix.ui.widget.listselect.JmixMultiListSelect;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebMultiOptionsList<V> extends WebV8AbstractField<JmixMultiListSelect<V>, Set<V>, Collection<V>>
        implements MultiOptionsList<V>, InitializingBean {

    protected MetadataTools metadataTools;

    protected OptionsBinding<V> optionsBinding;

    protected Function<? super V, String> optionCaptionProvider;

    public WebMultiOptionsList() {
        component = createComponent();
    }

    protected JmixMultiListSelect<V> createComponent() {
        return new JmixMultiListSelect<>();
    }

    @Override
    public void afterPropertiesSet() {
        initComponent(component);
    }

    protected void initComponent(JmixMultiListSelect<V> component) {
        component.setDataProvider(new ListDataProvider<>(Collections.emptyList()));
        component.setItemCaptionGenerator(this::generateItemCaption);
        component.setRequiredError(null);

        component.setDoubleClickHandler(this::onDoubleClick);

        attachValueChangeListener(component);
    }

    protected String generateDefaultItemCaption(V item) {
        if (valueBinding != null && valueBinding.getSource() instanceof EntityValueSource) {
            EntityValueSource entityValueSource = (EntityValueSource) valueBinding.getSource();
            return metadataTools.format(item, entityValueSource.getMetaPropertyPath().getMetaProperty());
        }

        return metadataTools.format(item);
    }

    protected String generateItemCaption(V item) {
        if (item == null) {
            return null;
        }

        if (optionCaptionProvider != null) {
            return optionCaptionProvider.apply(item);
        }

        return generateDefaultItemCaption(item);
    }

    @Autowired
    public void setMetadataTools(MetadataTools metadataTools) {
        this.metadataTools = metadataTools;
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
    public Options<V> getOptions() {
        return optionsBinding != null ? optionsBinding.getSource() : null;
    }

    @Override
    public void setOptions(Options<V> options) {
        if (this.optionsBinding != null) {
            this.optionsBinding.unbind();
            this.optionsBinding = null;
        }

        if (options != null) {
            OptionsBinder optionsBinder = beanLocator.get(OptionsBinder.NAME, OptionsBinder.class);
            this.optionsBinding = optionsBinder.bind(options, this, this::setItemsToPresentation);
            this.optionsBinding.activate();
        }
    }

    @Override
    protected void valueBindingConnected(ValueSource<Collection<V>> valueSource) {
        super.valueBindingConnected(valueSource);

        if (valueSource instanceof EntityValueSource) {
            DataAwareComponentsTools dataAwareComponentsTools = beanLocator.get(DataAwareComponentsTools.class);
            dataAwareComponentsTools.setupOptions(this, (EntityValueSource) valueSource);
        }
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

    protected void fireValueChange(Collection<V> oldValue, Collection<V> value) {
        if (!fieldValueEquals(oldValue, value)) {
            ValueChangeEvent<Collection<V>> event =
                    new ValueChangeEvent<>(this, oldValue, value, false);
            publish(ValueChangeEvent.class, event);
        }
    }

    protected void setItemsToPresentation(Stream<V> options) {
        component.setItems(options);

        // set value to Vaadin component as it removes value after setItems
        if (CollectionUtils.isNotEmpty(getValue())) {
            List<V> optionsList = getOptions().getOptions().collect(Collectors.toList());

            Set<V> missedValues = getValue().stream()
                    .filter(optionsList::contains)
                    .collect(Collectors.toSet());

            component.setValue(missedValues);
        }
    }

    @Override
    public void setOptionCaptionProvider(Function<? super V, String> optionCaptionProvider) {
        this.optionCaptionProvider = optionCaptionProvider;

        component.markAsDirty();
    }

    @Override
    public Function<? super V, String> getOptionCaptionProvider() {
        return optionCaptionProvider;
    }

    @Override
    public void focus() {
        component.focus();
    }

    @Override
    public int getTabIndex() {
        return component.getTabIndex();
    }

    @Override
    public void setTabIndex(int tabIndex) {
        component.setTabIndex(tabIndex);
    }

    protected void onDoubleClick(V item) {
        if (hasSubscriptions(DoubleClickEvent.class)) {
            DoubleClickEvent<V> event = new DoubleClickEvent<>(this, item);
            publish(DoubleClickEvent.class, event);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Subscription addDoubleClickListener(Consumer<DoubleClickEvent<V>> listener) {
        return getEventHub().subscribe(DoubleClickEvent.class, (Consumer) listener);
    }
}
