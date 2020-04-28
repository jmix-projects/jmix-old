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
package io.jmix.samples.rest.entity.driver;


import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.Creatable;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotations.InstanceName;
import io.jmix.data.entity.BaseUuidEntity;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "ref$DriverAllocation")
@Table(name = "REF_DRIVER_ALLOC")
public class DriverAllocation extends BaseUuidEntity implements Creatable {

    private static final long serialVersionUID = 8101497971694305079L;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

    @ManyToOne
    @JoinColumn(name = "DRIVER_ID")
    @OnDeleteInverse(DeletePolicy.DENY)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "CAR_ID")
    private Car car;

    @InstanceName(relatedProperties = {"driver", "car"})
    public String getCaption() {
        return String.format("%s:(%s)", getDriver(), getCar());
    }

    @Override
    public Date getCreateTs() {
        return createTs;
    }

    @Override
    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
