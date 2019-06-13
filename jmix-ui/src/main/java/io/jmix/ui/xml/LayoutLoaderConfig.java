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
package io.jmix.ui.xml;

import io.jmix.ui.components.Button;
import io.jmix.ui.components.Label;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component(LayoutLoaderConfig.NAME)
public class LayoutLoaderConfig {

    public static final String NAME = "jmix_LayoutLoaderConfig";

    protected Map<String, Class<? extends ComponentLoader>> loaders = new ConcurrentHashMap<>();

    protected Class<? extends WindowLoader> windowLoader = WindowLoader.class;

    public LayoutLoaderConfig() {
        initStandardLoaders();
    }

    protected void initStandardLoaders() {
        loaders.put(Label.NAME, LabelLoader.class);
        loaders.put(Button.NAME, ButtonLoader.class);
    }

    public void registerLoader(String tagName, Class<? extends ComponentLoader> aClass) {
        loaders.put(tagName, aClass);
    }

    public Class<? extends ComponentLoader> getWindowLoader() {
        return windowLoader;
    }

    public Class<? extends ComponentLoader> getFragmentLoader() {
        // return fragmentLoader;
        throw new UnsupportedOperationException(); // todo fragments
    }

    public Class<? extends ComponentLoader> getLoader(String name) {
        return loaders.get(name);
    }

    public void registerWindowLoader(Class<? extends WindowLoader> loader) {
        windowLoader = loader;
    }

    // todo fragments
    /*public void registerFragmentLoader(Class<? extends FragmentLoader> loader) {
        fragmentLoader = loader;
    }*/

    protected void register(String tagName, Class<? extends ComponentLoader> loaderClass) {
        loaders.put(tagName, loaderClass);
    }
}