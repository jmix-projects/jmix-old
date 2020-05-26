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

package test_support.entity.lazyloading;

import io.jmix.data.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Table(name = "FP_MAIN_ENTITY")
@Entity(name = "fp_MainEntity")
public class FPMainEntity extends StandardEntity {
    private static final long serialVersionUID = 6457834500563208111L;

    @Column(name = "NAME")
    protected String name;

    @OneToMany(mappedBy = "mainEntity")
    protected List<FPManyEntity> refField;

    public List<FPManyEntity> getRefField() {
        return refField;
    }

    public void setRefField(List<FPManyEntity> refField) {
        this.refField = refField;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
