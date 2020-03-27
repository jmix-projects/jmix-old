/*
 * Copyright (c) 2008-2016 Haulmont.
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

package com.haulmont.cuba.core.model;

import io.jmix.core.DeletePolicy;
import io.jmix.data.entity.StandardEntity;
import io.jmix.core.entity.annotation.OnDelete;
import io.jmix.core.metamodel.annotations.NamePattern;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "test$CascadeDeletionPolicyEntity")
@Table(name = "TEST_CASCADE_DELETION_POLICY_ENTITY")
@NamePattern("%s|name")
public class CascadeDeletionPolicyEntity extends StandardEntity {

    private static final long serialVersionUID = -5934642550137679651L;

    @Column(name = "NAME")
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FATHER_ID")
    protected CascadeDeletionPolicyEntity father;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FIRST_CHILD_ID")
    protected CascadeDeletionPolicyEntity firstChild;

    @OneToMany(mappedBy = "father")
    @OnDelete(DeletePolicy.CASCADE)
    protected Set<CascadeDeletionPolicyEntity> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CascadeDeletionPolicyEntity getFather() {
        return father;
    }

    public void setFather(CascadeDeletionPolicyEntity father) {
        this.father = father;
    }

    public CascadeDeletionPolicyEntity getFirstChild() {
        return firstChild;
    }

    public void setFirstChild(CascadeDeletionPolicyEntity firstChild) {
        this.firstChild = firstChild;
    }

    public Set<CascadeDeletionPolicyEntity> getChildren() {
        return children;
    }

    public void setChildren(Set<CascadeDeletionPolicyEntity> children) {
        this.children = children;
    }
}
