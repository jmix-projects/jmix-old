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

package io.jmix.ui.screen;

import io.jmix.core.JmixEntity;

/**
 * Interface for editor screen controllers.
 *
 * @param <T> type of entity
 */
public interface EditorScreen<T extends JmixEntity> extends ChangeTracker {
    /**
     * Name of action that commits changes.
     * <br> If the screen doesn't contain a component with {@link #WINDOW_COMMIT_AND_CLOSE} ID, this action also
     * closes the screen after commit.
     */
    String WINDOW_COMMIT = "windowCommit";

    /**
     * Name of action that commits changes and closes the screen.
     */
    String WINDOW_COMMIT_AND_CLOSE = "windowCommitAndClose";

    /**
     * Name of action that closes the screen.
     */
    String WINDOW_CLOSE = "windowClose";

    /**
     * Name of action that enables screen editing, i.e. sets the 'readOnly' mode to {@code false}.
     */
    String ENABLE_EDITING = "enableEditing";

    /**
     * Sets entity instance to editor.
     *
     * @param entity entity
     */
    void setEntityToEdit(T entity);

    /**
     * @return currently edited entity instance
     */
    T getEditedEntity();

    /**
     * @return true if the edited item has been pessimistically locked when the screen is opened
     */
    boolean isLocked();
}
