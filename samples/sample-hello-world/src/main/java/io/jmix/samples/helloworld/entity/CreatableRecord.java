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

package io.jmix.samples.helloworld.entity;


import io.jmix.core.entity.HasUuid;
import io.jmix.core.entity.annotation.JmixGeneratedId;
import io.jmix.core.metamodel.annotation.InstanceName;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Table(name = "SAMPLE_CREATABLE_RECORD")
@Entity(name = "sample_CreatableRecord")
public class CreatableRecord implements io.jmix.core.Entity, HasUuid {

    private static final long serialVersionUID = -315907834413711763L;

    @Id
    @Column(name = "ID")
    @JmixGeneratedId
    protected UUID id;


    @NotNull
    @Column(name = "title", nullable = false)
    @InstanceName
    private String title;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    protected Date createdDate;

    @CreatedBy
    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;




    /*public CreatableRecord() {//todo investigate and remove if need
        id = UuidProvider.createUuid();
    }*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public UUID getUuid() {
        return id;
    }

    public void setUuid(UUID uuid) {
        this.id = uuid;
    }

}
