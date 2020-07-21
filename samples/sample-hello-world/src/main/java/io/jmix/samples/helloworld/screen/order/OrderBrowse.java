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

package io.jmix.samples.helloworld.screen.order;

import com.google.gson.GsonBuilder;
import io.jmix.samples.helloworld.entity.Order;
import io.jmix.ui.Actions;
import io.jmix.ui.component.PopupButton;
import io.jmix.ui.component.Table;
import io.jmix.ui.screen.*;
import io.jmix.uiexport.action.ExportAction;
import io.jmix.uiexport.exporter.excel.ExcelExporter;
import io.jmix.uiexport.exporter.json.JsonExporter;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("sample_Order.browse")
@UiDescriptor("order-browse.xml")
@LookupComponent("ordersTable")
@LoadDataBeforeShow
public class OrderBrowse extends StandardLookup<Order> {

    @Autowired
    protected Table<Order> ordersTable;

    @Autowired
    protected Actions actions;

    @Autowired
    protected PopupButton exportBtn;

    @Subscribe
    protected void onInit(InitEvent initEvent) {
        ExportAction excelAction = actions.create(ExportAction.class, "excelExport");
        excelAction.withExporter(ExcelExporter.class);
        ordersTable.addAction(excelAction);
        exportBtn.addAction(excelAction);

        ExportAction jsonAction = actions.create(ExportAction.class, "jsonExport");
        jsonAction.withExporter(JsonExporter.class)
                .withGsonConfigurer(GsonBuilder::setPrettyPrinting);
        ordersTable.addAction(jsonAction);
        exportBtn.addAction(jsonAction);
    }
}