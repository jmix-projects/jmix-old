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
package io.jmix.ui.presentations;

import io.jmix.ui.presentations.model.Presentation;
import io.jmix.ui.components.Component;
import org.dom4j.Element;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import java.util.*;

@org.springframework.stereotype.Component(Presentations.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class EmptyPresentationsImpl implements Presentations {

    public EmptyPresentationsImpl(Component c) {
        // do nothing
    }

    @Override
    public Presentation getCurrent() {
        return null;
    }

    @Override
    public void setCurrent(Presentation p) {
        // do nothing
    }

    @Override
    public Element getSettings(Presentation p) {
        return null;
    }

    @Override
    public void setSettings(Presentation p, Element e) {
        // do nothing
    }

    @Override
    public Presentation getPresentation(Object id) {
        return null;
    }

    @Override
    public String getCaption(Object id) {
        return null;
    }

    @Override
    public Collection<Object> getPresentationIds() {
        return Collections.emptyList();
    }

    @Override
    public Presentation getDefault() {
        return null;
    }

    @Override
    public void setDefault(Presentation p) {
        // do nothing
    }

    @Override
    public void add(Presentation p) {
        // do nothing
    }

    @Override
    public void remove(Presentation p) {
        // do nothing
    }

    @Override
    public void modify(Presentation p) {
        // do nothing
    }

    @Override
    public boolean isAutoSave(Presentation p) {
        return false;
    }

    @Override
    public boolean isGlobal(Presentation p) {
        return false;
    }

    @Override
    public void commit() {
        // do nothing
    }

    @Override
    public Presentation getPresentationByName(String name) {
        return null;
    }

    @Override
    public void addListener(PresentationsChangeListener listener) {
        // do nothing
    }

    @Override
    public void removeListener(PresentationsChangeListener listener) {
        // do nothing
    }
}
