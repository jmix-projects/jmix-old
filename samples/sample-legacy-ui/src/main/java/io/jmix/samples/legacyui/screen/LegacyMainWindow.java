/*
 * Copyright 2020 Haulmont.
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

package io.jmix.samples.legacyui.screen;

import com.haulmont.cuba.gui.components.AbstractMainWindow;
import io.jmix.ui.component.SplitPanel;

import javax.inject.Inject;
import java.util.Map;

public class LegacyMainWindow extends AbstractMainWindow {

    @Inject
    protected SplitPanel foldersSplit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        foldersSplit.remove(workArea);

        int foldersSplitIndex = indexOf(foldersSplit);

        remove(foldersSplit);
        add(workArea, foldersSplitIndex);

        expand(workArea);

    }
}
