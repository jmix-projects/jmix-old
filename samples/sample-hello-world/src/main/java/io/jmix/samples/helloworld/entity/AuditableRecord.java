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
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "JMIX_AUDITABLE_RECORD")
@Entity(name = "jmix_AuditableRecord")
public class AuditableRecord implements io.jmix.core.Entity, HasUuid {
    private static final long serialVersionUID = 6739877153536998528L;


    @Id
    @Column(name = "ID")
    @JmixGeneratedId
    protected UUID id;


    @NotNull
    @Column(name = "title", nullable = false)
    @InstanceName
    private String title;

    @CreatedDate
    @Column(name = "CREATED_TS")
    protected Long createTs;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    protected Customer customer;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE")
    protected LocalDateTime updatedDate;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "UPDATED_BY")
    protected User updatedBy;


    /*public AuditableRecord() {//todo remove if not need
        id = UuidProvider.createUuid();
    }*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Long createTs) {
        this.createTs = createTs;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
}