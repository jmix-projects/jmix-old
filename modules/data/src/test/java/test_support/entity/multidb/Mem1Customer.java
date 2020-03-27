/*
 * Copyright (c) 2008-2016 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package test_support.entity.multidb;

import io.jmix.data.entity.BaseUuidEntity;
import io.jmix.core.metamodel.annotations.ModelObject;
import io.jmix.core.metamodel.annotations.ModelProperty;
import io.jmix.core.metamodel.annotations.NamePattern;
import io.jmix.core.metamodel.annotations.Store;

@ModelObject(name = "test_Mem1Customer")
@NamePattern("%s|name")
@Store(name = "mem1")
public class Mem1Customer extends BaseUuidEntity {

    @ModelProperty
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
