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

package test_support.app.entity.model_objects;

import io.jmix.core.EntityEntry;
import io.jmix.core.JmixEntity;
import io.jmix.core.entity.ModelObjectEntityEntry;
import io.jmix.core.metamodel.annotation.ModelObject;
import io.jmix.core.metamodel.annotation.ModelProperty;

@ModelObject(name = "test_CustomerObject", annotatedPropertiesOnly = true)
public class CustomerObject implements JmixEntity {

    @ModelProperty(mandatory = true)
    private String name;

    private Object anObject;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getAnObject() {
        return anObject;
    }

    public void setAnObject(Object anObject) {
        this.anObject = anObject;
    }

    // TODO Replace with enhancing - begin

    private EntityEntry _jmixEntityEntry = new ModelObjectEntityEntry(this);

    @Override
    public EntityEntry __getEntityEntry() {
        return _jmixEntityEntry;
    }

    @Override
    public void __copyEntityEntry() {
        ModelObjectEntityEntry newEntry = new ModelObjectEntityEntry(this);
        newEntry.copy(_jmixEntityEntry);
        _jmixEntityEntry = newEntry;
    }

    // TODO Replace with enhancing - end
}
