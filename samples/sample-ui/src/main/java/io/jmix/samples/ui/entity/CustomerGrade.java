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

package io.jmix.samples.ui.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

public enum CustomerGrade implements EnumClass<Integer> {

    PREMIUM(10),
    HIGH(20),
    STANDARD(30);

    private Integer id;

    CustomerGrade(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public static CustomerGrade fromId(Integer id) {
        for (CustomerGrade grade : CustomerGrade.values()) {
            if (grade.getId().equals(id))
                return grade;
        }
        return null;
    }
}
