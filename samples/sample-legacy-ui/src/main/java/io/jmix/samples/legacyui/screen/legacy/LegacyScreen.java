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

package io.jmix.samples.legacyui.screen.legacy;

import com.haulmont.cuba.gui.backgroundwork.BackgroundWorkProgressWindow;
import com.haulmont.cuba.gui.backgroundwork.BackgroundWorkWindow;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.data.Datasource;
import io.jmix.samples.legacyui.entity.SampleUser;
import io.jmix.ui.executor.BackgroundTask;
import io.jmix.ui.executor.TaskLifeCycle;
import io.jmix.ui.screen.Screen;
import org.springframework.beans.factory.annotation.Autowired;

public class LegacyScreen extends AbstractWindow {

    @Autowired
    protected Datasource<SampleUser> userDs;

    @Override
    protected void afterShow(AfterShowEvent event) {
        SampleUser user = new SampleUser();
        user.setName("Test User");

        userDs.setItem(user);
    }

    public void runBackgroundTask() {
        BackgroundWorkWindow.show(createBackgroundTask(10, 1, 5000),
                "Running background task", "Please wait while task is running",
                true
        );
    }

    public void runBackgroundTaskWithProgress() {
        int total = 10;
        BackgroundTask<Integer, Void> task = createBackgroundTask(10, total, 1000);

        BackgroundWorkProgressWindow.show(task,
                "Running background task", "Please wait while task is running",
                total, true, true
        );
    }

    private BackgroundTask<Integer, Void> createBackgroundTask(int timeoutSeconds, int total, int updateIntervalMillis) {
        return new BackgroundTask<Integer, Void>(timeoutSeconds, this) {
                @Override
                public Void run(TaskLifeCycle<Integer> taskLifeCycle) throws Exception {
                    for (int i = 0; i < total; i++) {
                        Thread.sleep(updateIntervalMillis);
                        taskLifeCycle.publish(i);
                    }
                    return null;
                }
            };
    }
}
