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

package io.jmix.ui.components.calendar;

import io.jmix.core.commons.events.EventHub;
import io.jmix.core.commons.events.Subscription;
import io.jmix.core.entity.Entity;
import io.jmix.core.entity.EntityAccessor;
import io.jmix.core.entity.EntityPropertyChangeListener;
import io.jmix.ui.components.data.calendar.EntityCalendarEventProvider;
import org.apache.commons.lang3.BooleanUtils;

import java.util.function.Consumer;

public class EntityCalendarEvent<E extends Entity, V> implements CalendarEvent<V> {

    protected final E entity;
    protected final EntityCalendarEventProvider provider;

    protected EventHub events = new EventHub();

    public EntityCalendarEvent(E entity, EntityCalendarEventProvider provider) {
        this.entity = entity;
        this.provider = provider;

        // todo bad practice, use datasource listener instead
        EntityAccessor.addPropertyChangeListener(this.entity, this::onPropertyChanged);
    }

    protected void onPropertyChanged(EntityPropertyChangeListener.PropertyChangeEvent event) {
        events.publish(EventChangeEvent.class, new EventChangeEvent<>(this));
    }

    public E getEntity() {
        return entity;
    }

    @Override
    public V getStart() {
        if (provider.getStartDateProperty() != null) {
            return EntityAccessor.getEntityValue(entity, provider.getStartDateProperty());
        } else {
            return null;
        }
    }

    @Override
    public void setStart(V start) {
        EntityAccessor.setEntityValue(entity, provider.getStartDateProperty(), start);
    }

    @Override
    public V getEnd() {
        if (provider.getEndDateProperty() != null) {
            return EntityAccessor.getEntityValue(entity, provider.getEndDateProperty());
        } else {
            return null;
        }
    }

    @Override
    public void setEnd(V end) {
        EntityAccessor.setEntityValue(entity, provider.getEndDateProperty(), end);
    }

    @Override
    public String getCaption() {
        if (provider.getCaptionProperty() != null) {
            return EntityAccessor.getEntityValue(entity, provider.getCaptionProperty());
        } else {
            return null;
        }
    }

    @Override
    public void setCaption(String caption) {
        EntityAccessor.setEntityValue(entity, provider.getCaptionProperty(), caption);
    }

    @Override
    public void setDescription(String description) {
        EntityAccessor.setEntityValue(entity, provider.getDescriptionProperty(), description);
    }

    @Override
    public String getDescription() {
        if (provider.getDescriptionProperty() != null) {
            return EntityAccessor.getEntityValue(entity, provider.getDescriptionProperty());
        } else {
            return null;
        }
    }

    @Override
    public String getStyleName() {
        if (provider.getStyleNameProperty() != null) {
            return EntityAccessor.getEntityValue(entity, provider.getStyleNameProperty());
        } else {
            return null;
        }
    }

    @Override
    public void setStyleName(String styleName) {
        EntityAccessor.setEntityValue(entity, provider.getStyleNameProperty(), styleName);
    }

    @Override
    public boolean isAllDay() {
        if (provider.getIsAllDayProperty() != null) {
            return BooleanUtils.isTrue(EntityAccessor.getEntityValue(entity, provider.getIsAllDayProperty()));
        } else {
            return false;
        }
    }

    @Override
    public void setAllDay(boolean isAllDay) {
        EntityAccessor.setEntityValue(entity, provider.getIsAllDayProperty(), isAllDay);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Subscription addEventChangeListener(Consumer<EventChangeEvent<V>> listener) {
        return events.subscribe(EventChangeEvent.class, (Consumer) listener);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void removeEventChangeListener(Consumer<EventChangeEvent<V>> listener) {
        events.unsubscribe(EventChangeEvent.class, (Consumer) listener);
    }
}
