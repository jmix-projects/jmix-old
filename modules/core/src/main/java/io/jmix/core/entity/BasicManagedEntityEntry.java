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

package io.jmix.core.entity;

public class BasicManagedEntityEntry implements ManagedEntityEntry {
    protected byte state = NEW;
    protected SecurityState securityState = new SecurityState();

    public static final int NEW = 1;
    public static final int DETACHED = 2;
    public static final int MANAGED = 4;
    public static final int REMOVED = 8;

    @Override
    public boolean isNew() {
        return (state & NEW) == NEW;
    }

    @Override
    public boolean isManaged() {
        return (state & MANAGED) == MANAGED;
    }

    @Override
    public boolean isDetached() {
        return (state & DETACHED) == DETACHED;
    }

    @Override
    public boolean isRemoved() {
        return (state & REMOVED) == REMOVED;
    }

    @Override
    public void setNew(boolean _new) {
        state = (byte) (_new ? state | NEW : state & ~NEW);
    }

    @Override
    public void setManaged(boolean managed) {
        state = (byte) (managed ? state | MANAGED : state & ~MANAGED);
    }

    @Override
    public void setDetached(boolean detached) {
        state = (byte) (detached ? state | DETACHED : state & ~DETACHED);
    }

    @Override
    public void setRemoved(boolean removed) {
        state = (byte) (removed ? state | REMOVED : state & ~REMOVED);
    }

    @Override
    public SecurityState getSecurityState() {
        return securityState;
    }

    @Override
    public void setSecurityState(SecurityState securityState) {
        this.securityState = securityState;
    }

    @Override
    public void copy(ManagedEntityEntry entry) {

    }
}
