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

import com.vaadin.event.MouseEvents;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.Orientation;
import io.jmix.ui.components.*;

import static io.jmix.core.commons.util.Preconditions.checkNotNullArgument;

/**
 * Convenient class for methods that converts values from Vaadin to CUBA instances and vice versa.
 */
public final class WebWrapperUtils {
    private WebWrapperUtils() {
    }

    public static ContentMode toContentMode(com.vaadin.shared.ui.ContentMode contentMode) {
        checkNotNullArgument(contentMode);

        switch (contentMode) {
            case TEXT:
                return ContentMode.TEXT;
            case PREFORMATTED:
                return ContentMode.PREFORMATTED;
            case HTML:
                return ContentMode.HTML;
            default:
                throw new IllegalArgumentException("Unknown content mode: " + contentMode);
        }
    }

    public static com.vaadin.shared.ui.ContentMode toVaadinContentMode(ContentMode contentMode) {
        checkNotNullArgument(contentMode);

        switch (contentMode) {
            case TEXT:
                return com.vaadin.shared.ui.ContentMode.TEXT;
            case PREFORMATTED:
                return com.vaadin.shared.ui.ContentMode.PREFORMATTED;
            case HTML:
                return com.vaadin.shared.ui.ContentMode.HTML;
            default:
                throw new IllegalArgumentException("Unknown content mode: " + contentMode);
        }
    }

    public static com.vaadin.ui.Alignment toVaadinAlignment(Component.Alignment alignment) {
        if (alignment == null) {
            return null;
        }

        switch (alignment) {
            case TOP_LEFT:
                return com.vaadin.ui.Alignment.TOP_LEFT;
            case TOP_CENTER:
                return com.vaadin.ui.Alignment.TOP_CENTER;
            case TOP_RIGHT:
                return com.vaadin.ui.Alignment.TOP_RIGHT;
            case MIDDLE_LEFT:
                return com.vaadin.ui.Alignment.MIDDLE_LEFT;
            case MIDDLE_CENTER:
                return com.vaadin.ui.Alignment.MIDDLE_CENTER;
            case MIDDLE_RIGHT:
                return com.vaadin.ui.Alignment.MIDDLE_RIGHT;
            case BOTTOM_LEFT:
                return com.vaadin.ui.Alignment.BOTTOM_LEFT;
            case BOTTOM_CENTER:
                return com.vaadin.ui.Alignment.BOTTOM_CENTER;
            case BOTTOM_RIGHT:
                return com.vaadin.ui.Alignment.BOTTOM_RIGHT;
            default:
                throw new UnsupportedOperationException("Unsupported Alignment");
        }
    }

    public static MouseEventDetails toMouseEventDetails(MouseEvents.ClickEvent event) {
        checkNotNullArgument(event);

        MouseEventDetails mouseEventDetails = new MouseEventDetails();
        mouseEventDetails.setButton(toMouseButton(event.getButton()));
        mouseEventDetails.setClientX(event.getClientX());
        mouseEventDetails.setClientY(event.getClientY());
        mouseEventDetails.setAltKey(event.isAltKey());
        mouseEventDetails.setCtrlKey(event.isCtrlKey());
        mouseEventDetails.setMetaKey(event.isMetaKey());
        mouseEventDetails.setShiftKey(event.isShiftKey());
        mouseEventDetails.setDoubleClick(event.isDoubleClick());
        mouseEventDetails.setRelativeX(event.getRelativeX());
        mouseEventDetails.setRelativeY(event.getRelativeY());

        return mouseEventDetails;
    }

    public static MouseEventDetails toMouseEventDetails(com.vaadin.shared.MouseEventDetails vMouseEventDetails) {
        checkNotNullArgument(vMouseEventDetails);

        MouseEventDetails mouseEventDetails = new MouseEventDetails();
        mouseEventDetails.setButton(toMouseButton(vMouseEventDetails.getButton()));
        mouseEventDetails.setClientX(vMouseEventDetails.getClientX());
        mouseEventDetails.setClientY(vMouseEventDetails.getClientY());
        mouseEventDetails.setAltKey(vMouseEventDetails.isAltKey());
        mouseEventDetails.setCtrlKey(vMouseEventDetails.isCtrlKey());
        mouseEventDetails.setMetaKey(vMouseEventDetails.isMetaKey());
        mouseEventDetails.setShiftKey(vMouseEventDetails.isShiftKey());
        mouseEventDetails.setDoubleClick(vMouseEventDetails.isDoubleClick());
        mouseEventDetails.setRelativeX(vMouseEventDetails.getRelativeX());
        mouseEventDetails.setRelativeY(vMouseEventDetails.getRelativeY());

        return mouseEventDetails;
    }

    public static MouseEventDetails.MouseButton toMouseButton(com.vaadin.shared.MouseEventDetails.MouseButton mouseButton) {
        if (mouseButton == null) {
            return null;
        }

        switch (mouseButton) {
            case LEFT:
                return MouseEventDetails.MouseButton.LEFT;
            case MIDDLE:
                return MouseEventDetails.MouseButton.MIDDLE;
            case RIGHT:
                return MouseEventDetails.MouseButton.RIGHT;
            default:
                throw new UnsupportedOperationException("Unsupported Vaadin MouseButton");
        }
    }

    public static Sizeable.Unit toVaadinUnit(SizeUnit sizeUnit) {
        checkNotNullArgument(sizeUnit);

        switch (sizeUnit) {
            case PIXELS:
                return Sizeable.Unit.PIXELS;
            case PERCENTAGE:
                return Sizeable.Unit.PERCENTAGE;
            default:
                throw new UnsupportedOperationException("Unsupported Size Unit");
        }
    }

    public static SizeUnit toSizeUnit(Sizeable.Unit units) {
        checkNotNullArgument(units);

        switch (units) {
            case PIXELS:
                return SizeUnit.PIXELS;
            case PERCENTAGE:
                return SizeUnit.PERCENTAGE;
            default:
                throw new UnsupportedOperationException("Unsupported Size Unit");
        }
    }

    public static HasOrientation.Orientation convertToOrientation(Orientation orientation) {
        checkNotNullArgument(orientation);

        switch (orientation) {
            case VERTICAL:
                return HasOrientation.Orientation.VERTICAL;
            case HORIZONTAL:
                return HasOrientation.Orientation.HORIZONTAL;
            default:
                throw new IllegalArgumentException("Can't be converted to HasOrientation.Orientation: " + orientation);
        }
    }

    public static Orientation convertToVaadinOrientation(HasOrientation.Orientation orientation) {
        checkNotNullArgument(orientation);

        switch (orientation) {
            case VERTICAL:
                return Orientation.VERTICAL;
            case HORIZONTAL:
                return Orientation.HORIZONTAL;
            default:
                throw new IllegalArgumentException("Can't be converted to Orientation: " + orientation);
        }
    }
}