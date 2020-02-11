/*
 * Copyright (c) 2008-2018 Haulmont.
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

package spec.haulmont.cuba.web.components

import com.haulmont.cuba.gui.components.BulkEditor
import com.haulmont.cuba.gui.components.FieldGroup
import io.jmix.ui.Facets
import io.jmix.ui.components.*
import io.jmix.ui.components.impl.JavaScriptComponent
import io.jmix.ui.components.mainwindow.*
import spec.haulmont.cuba.web.WebSpec
import spock.lang.Ignore
import spock.lang.Unroll

import javax.inject.Inject

@SuppressWarnings("GroovyAccessibility")
class CreateComponentTest extends WebSpec {

    @Inject
    Facets facets

    @Ignore
    @Unroll
    def "create standard UI component: '#name' with UiComponents"() {
        expect:
        uiComponents.create(name) != null

        where:
        name << [
                RootWindow.NAME,
                TabWindow.NAME,
                DialogWindow.NAME,
                Fragment.NAME,

                HBoxLayout.NAME,
                VBoxLayout.NAME,
                GridLayout.NAME,
                ScrollBoxLayout.NAME,
                HtmlBoxLayout.NAME,
                FlowBoxLayout.NAME,
                CssLayout.NAME,

                Button.NAME,
                LinkButton.NAME,
                Label.NAME,
                Link.NAME,
                CheckBox.NAME,
                GroupBoxLayout.NAME,
                SourceCodeEditor.NAME,
                TextField.NAME,
                PasswordField.NAME,

                ResizableTextArea.NAME,
                TextArea.NAME,
                RichTextArea.NAME,
                MaskedField.NAME,

                Table.NAME,
                TreeTable.NAME,
                GroupTable.NAME,
                DataGrid.NAME,
                TreeDataGrid.NAME,
                DateField.NAME,
                TimeField.NAME,
                LookupField.NAME,
                SearchField.NAME,
                PickerField.NAME,
                SuggestionField.NAME,
                SuggestionPickerField.NAME,
                ColorPicker.NAME,
                LookupPickerField.NAME,
                SearchPickerField.NAME,
                OptionsGroup.NAME,
                CheckBoxGroup.NAME,
                RadioButtonGroup.NAME,
                OptionsList.NAME,
                FileUploadField.NAME,
                FileMultiUploadField.NAME,
                CurrencyField.NAME,
                SplitPanel.NAME,
                Tree.NAME,
                TabSheet.NAME,
                Accordion.NAME,
                Calendar.NAME,
                Embedded.NAME,
                Image.NAME,
                BrowserFrame.NAME,
                // Filter.NAME, todo filter
                ButtonsPanel.NAME,
                PopupButton.NAME,
                PopupView.NAME,

                FieldGroup.NAME,
                TokenList.NAME,
                TwinColumn.NAME,
                ProgressBar.NAME,
                RowsCount.NAME,
                RelatedEntities.NAME,
                BulkEditor.NAME,
                DatePicker.NAME,
                ListEditor.NAME,
                CapsLockIndicator.NAME,
                Form.NAME,

                EntityLinkField.NAME,

                AppMenu.NAME,
                AppWorkArea.NAME,
                LogoutButton.NAME,
                NewWindowButton.NAME,
                UserIndicator.NAME,
                UserActionsButton.NAME,
                FoldersPane.NAME,
                // FtsField.NAME, todo
                TimeZoneIndicator.NAME,
                SideMenu.NAME,

                JavaScriptComponent.NAME
        ]
    }

    @Ignore
    @Unroll
    def "create standard facet: '#facet' with Facets"() {
        given:

        expect:

        facets.create(facet) != null

        where:

        facet << [
                Timer.class,
                ClipboardTrigger.class
        ]
    }
}