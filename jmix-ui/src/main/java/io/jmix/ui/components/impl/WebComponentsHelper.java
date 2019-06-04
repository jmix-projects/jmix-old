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

package io.jmix.ui.components.impl;

import com.vaadin.ui.*;
import io.jmix.ui.components.ComponentContainer;
import io.jmix.ui.components.KeyCombination;
import io.jmix.ui.components.ShortcutTriggeredEvent;

import javax.annotation.Nullable;
import java.util.Collection;

public class WebComponentsHelper {
    /**
     * Tests if component visible and its container visible.
     *
     * @param child component
     * @return component visibility
     */
    public static boolean isComponentVisible(Component child) {
        if (child.getParent() instanceof TabSheet) {
            TabSheet tabSheet = (TabSheet) child.getParent();
            TabSheet.Tab tab = tabSheet.getTab(child);
            if (!tab.isVisible()) {
                return false;
            }
        }

        // todo implement
        /*if (child.getParent() instanceof CubaGroupBox) {
            // ignore groupbox content container visibility
            return isComponentVisible(child.getParent());
        }
*/
        return child.isVisible() && (child.getParent() == null || isComponentVisible(child.getParent()));
    }

    /**
     * Tests if component enabled and visible and its container enabled.
     *
     * @param child component
     * @return component enabled state
     */
    public static boolean isComponentEnabled(Component child) {
        if (child.getParent() instanceof TabSheet) {
            TabSheet tabSheet = (TabSheet) child.getParent();
            TabSheet.Tab tab = tabSheet.getTab(child);
            if (!tab.isEnabled()) {
                return false;
            }
        }

        return child.isEnabled() && (child.getParent() == null || isComponentEnabled(child.getParent())) &&
                isComponentVisible(child);
    }

    public static boolean isComponentExpanded(io.jmix.ui.components.Component component) {
        Component vComponent = component.unwrapComposition(Component.class);
        if (vComponent.getParent() instanceof AbstractOrderedLayout) {
            AbstractOrderedLayout layout = (AbstractOrderedLayout) vComponent.getParent();
            return (int)layout.getExpandRatio(vComponent) == 1;
        }

        return false;
    }

    public static ShortcutTriggeredEvent getShortcutEvent(io.jmix.ui.components.Component source,
                                                          Component target) {
        Component vaadinSource = getVaadinSource(source);

        if (vaadinSource == target) {
            return new ShortcutTriggeredEvent(source, source);
        }

        if (source instanceof ComponentContainer) {
            ComponentContainer container = (ComponentContainer) source;
            io.jmix.ui.components.Component childComponent =
                    findChildComponent(container, target);
            return new ShortcutTriggeredEvent(source, childComponent);
        }

        return new ShortcutTriggeredEvent(source, null);
    }

    protected static Component getVaadinSource(io.jmix.ui.components.Component source) {
        Component component = source.unwrapComposition(Component.class);
        if (component instanceof AbstractSingleComponentContainer) {
            return ((AbstractSingleComponentContainer) component).getContent();
        }

//        todo implement
//        if (component instanceof CubaScrollBoxLayout) {
//            return ((CubaScrollBoxLayout) component).getComponent(0);
//        }

        return component;
    }

    @Nullable
    protected static io.jmix.ui.components.Component findChildComponent(ComponentContainer container,
                                                                                   Component target) {
        Component vaadinSource = getVaadinSource(container);
        Collection<io.jmix.ui.components.Component> components = container.getOwnComponents();

        return findChildComponent(components, vaadinSource, target);
    }

    // todo implement
    /*@Nullable
    protected static io.jmix.ui.components.Component findChildComponent(FieldGroup fieldGroup,
                                                                                   Component target) {
        Component vaadinSource = fieldGroup.unwrap(CubaFieldGroupLayout.class);
        Collection<io.jmix.ui.components.Component> components = fieldGroup.getFields().stream()
                .map(FieldGroup.FieldConfig::getComponentNN)
                .collect(Collectors.toList());

        return findChildComponent(components, vaadinSource, target);
    }*/

    protected static io.jmix.ui.components.Component findChildComponent(
            Collection<io.jmix.ui.components.Component> components,
            Component vaadinSource, Component target) {
        Component targetComponent = getDirectChildComponent(target, vaadinSource);

        for (io.jmix.ui.components.Component component : components) {
            Component unwrapped = component.unwrapComposition(Component.class);
            if (unwrapped == targetComponent) {
                io.jmix.ui.components.Component child = null;

                if (component instanceof ComponentContainer) {
                    child = findChildComponent((ComponentContainer) component, target);
                }

                // todo
                /*if (component instanceof HasButtonsPanel) {
                    ButtonsPanel buttonsPanel = ((HasButtonsPanel) component).getButtonsPanel();
                    if (getVaadinSource(buttonsPanel) == target) {
                        return buttonsPanel;
                    } else {
                        child = findChildComponent(buttonsPanel, target);
                    }
                }

                if (component instanceof FieldGroup) {
                    FieldGroup fieldGroup = (FieldGroup) component;
                    child = findChildComponent(fieldGroup, target);
                }*/

                return child != null ? child : component;
            }
        }
        return null;
    }

    /**
     * @return the direct child component of the layout which contains the component involved to event
     */
    protected static Component getDirectChildComponent(Component targetComponent, Component vaadinSource) {
        while (targetComponent != null
                && targetComponent.getParent() != vaadinSource) {
            targetComponent = targetComponent.getParent();
        }

        return targetComponent;
    }

    public static void setClickShortcut(Button button, String shortcut) {
        KeyCombination closeCombination = KeyCombination.create(shortcut);
        int[] closeModifiers = KeyCombination.Modifier.codes(closeCombination.getModifiers());
        int closeCode = closeCombination.getKey().getCode();

        button.setClickShortcut(closeCode, closeModifiers);
    }
}