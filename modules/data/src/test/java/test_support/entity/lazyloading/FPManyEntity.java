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

import javax.persistence.*;

@Table(name = "FP_MANY_ENTITY")
@Entity(name = "fp_ManyEntity")
public class FPManyEntity extends StandardEntity {
    @Column(name = "NAME")
    protected String name;
    @Column(name = "SECOND_NAME")
    protected String secondName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAIN_ENTITY_ID")
    protected FPMainEntity mainEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ELT_MAIN_ENTITY_ID")
    protected ELTMainEntity eltMainEntity;

    public ELTMainEntity getEltMainEntity() {
        return eltMainEntity;
    }

    public void setEltMainEntity(ELTMainEntity eltMainEntity) {
        this.eltMainEntity = eltMainEntity;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public FPMainEntity getMainEntity() {
        return mainEntity;
    }

    public void setMainEntity(FPMainEntity mainEntity) {
        this.mainEntity = mainEntity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
