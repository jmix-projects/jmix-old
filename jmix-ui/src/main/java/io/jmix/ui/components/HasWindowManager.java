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

package io.jmix.ui.components;

import io.jmix.core.AppBeans;
import io.jmix.ui.*;
import io.jmix.ui.components.compatibility.WindowManager;
import io.jmix.ui.screen.FrameOwner;
import io.jmix.ui.screen.ScreenContext;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * The interface introduced only for compatibility with legacy code.
 */
@Deprecated
public interface HasWindowManager {

    /**
     * It is recommended to use {@link Screens} instead, it can be obtained from {@link ScreenContext}
     * of {@link FrameOwner}.
     */
    WindowManager getWindowManager();

    /**
     * Open a simple screen. <br> It is recommended to use {@link ScreenBuilders} bean instead.
     *
     * @param windowAlias screen ID as defined in {@code screens.xml}
     * @param openType    how to open the screen
     * @param params      parameters to pass to {@code init()} method of the screen's controller
     * @return created window
     */
    default AbstractWindow openWindow(String windowAlias, WindowManager.OpenType openType, Map<String, Object> params) {
        WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);
        WindowInfo windowInfo = windowConfig.getWindowInfo(windowAlias);
        return (AbstractWindow) getWindowManager().openWindow(windowInfo, openType, params);
    }

    /**
     * Open a simple screen. <br> It is recommended to use {@link ScreenBuilders} bean instead.
     *
     * @param windowAlias screen ID as defined in {@code screens.xml}
     * @param openType    how to open the screen
     * @return created window
     */
    default AbstractWindow openWindow(String windowAlias, WindowManager.OpenType openType) {
        WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);
        WindowInfo windowInfo = windowConfig.getWindowInfo(windowAlias);
        return (AbstractWindow) getWindowManager().openWindow(windowInfo, openType);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Open a web page in browser. <br> It is recommended to use {@link WebBrowserTools} bean instead.
     *
     * @param url    URL of the page
     * @param params optional parameters.
     *               <br>The following parameters are recognized by Web client:
     *               - {@code target} - String value used as the target name in a
     *               window.open call in the client. This means that special values such as
     *               "_blank", "_self", "_top", "_parent" have special meaning. If not specified, "_blank" is used. <br>
     *               - {@code width} - Integer value specifying the width of the browser window in pixels<br>
     *               - {@code height} - Integer value specifying the height of the browser window in pixels<br>
     *               - {@code border} - String value specifying the border style of the window of the browser window.
     *               Possible values are "DEFAULT", "MINIMAL", "NONE".<br>
     *               <p>
     *               Desktop client doesn't support any parameters and just ignores them.
     */
    default void showWebPage(String url, @Nullable Map<String, Object> params) {
        getWindowManager().showWebPage(url, params);
    }
}
