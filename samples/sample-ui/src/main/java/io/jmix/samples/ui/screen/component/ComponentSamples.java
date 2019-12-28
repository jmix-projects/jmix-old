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

package io.jmix.samples.ui.screen.component;

import io.jmix.ui.UiComponents;
import io.jmix.ui.components.*;
import io.jmix.ui.screen.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@UiDescriptor("component-samples.xml")
@UiController("component-samples")
@LoadDataBeforeShow
public class ComponentSamples extends Screen {

    @Inject
    private CheckBoxGroup<String> checkBoxGroup;

    @Inject
    private TwinColumn<String> twinColumn;

    @Inject
    private OptionsList<List, String> optionsList;

    @Inject
    private OptionsGroup<List, String> optionsGroup;

    @Inject
    private VBoxLayout othersVBox;

    @Inject
    private UiComponents uiComponents;

//    private ListEditor<String> listEditor;

    @Subscribe
    private void onInit(InitEvent initEvent) {
        List<String> options = Arrays.asList("Value 1", "Value 2", "Value 3", "Value 4");
        checkBoxGroup.setOptionsList(options);
        twinColumn.setOptionsList(options);
        optionsList.setOptionsList(options);
        optionsGroup.setOptionsList(options);

//        listEditor = uiComponents.create(ListEditor.NAME);
//        listEditor.setOptionsList(options);
//        listEditor.setCaption("ListEditor");
//        othersVBox.add(listEditor);
    }

}
