/*
 * PersistentDynamicGroup.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Optional;

/**
 * <p>
 * Mutable named group. In the group language is referred by {@literal #}name. Keeps history of every change made.
 * </p>
 * 
 * <p>
 * In practice it's a wrapper over an immutable group with versioning over the relation between this and the wrapped group.
 * </p>
 * 
 * @see PersistentGroup
 */
public final class PersistentDynamicGroup extends PersistentDynamicGroup_Base {
    protected PersistentDynamicGroup() {
        super();
    }

    protected PersistentDynamicGroup(String name, PersistentGroup group) {
        this();
        setName(name);
        setCreated(new DateTime());
        setCreator(Authenticate.getUser());
        setGroup(group);
        setRootForDynamicGroup(getRoot());
    }

    @Override
    public Group toGroup() {
        return DynamicGroup.get(getName());
    }

    @Override
    public String getName() {
        //FIXME: remove when the framework enables read-only slots
        return super.getName();
    }

    @Override
    public PersistentGroup getGroup() {
        //FIXME: remove when the framework enables read-only slots
        return super.getGroup();
    }

    public PersistentGroup getGroup(DateTime when) {
        if (when.isAfter(getCreated())) {
            return getGroup();
        }
        if (getPrevious() != null) {
            return getPrevious().getGroup(when);
        }
        return PersistentNobodyGroup.getInstance();
    }

    private void pushHistory() {
        PersistentDynamicGroup old = new PersistentDynamicGroup();
        old.setRoot(null);
        old.setName(getName());
        old.setCreated(getCreated());
        old.setCreator(getCreator());
        old.setGroup(getGroup());
        old.setNext(this);
        setCreated(new DateTime());
        setCreator(Authenticate.getUser());
    }

    public PersistentDynamicGroup rename(String name) {
        pushHistory();
        setName(name);
        return this;
    }

    public PersistentDynamicGroup changeGroup(PersistentGroup overridingGroup) {
        if (!overridingGroup.equals(getGroup())) {
            pushHistory();
            setGroup(overridingGroup);
        }
        return this;
    }

    @Override
    protected boolean isGarbageCollectable() {
        // Dynamic (named) groups cannot be recovered only from language, therefore we can never safely delete.
        return false;
    }

    public static Optional<PersistentDynamicGroup> getInstance(final String name) {
        return BennuGroupIndex.getDynamic(name);
    }

    @Atomic(mode = TxMode.WRITE)
    public static PersistentDynamicGroup set(final String name, PersistentGroup overridingGroup) {
        Optional<PersistentDynamicGroup> persistent = PersistentDynamicGroup.getInstance(name);
        if (persistent.isPresent()) {
            return persistent.get().changeGroup(overridingGroup);
        } else {
            return new PersistentDynamicGroup(name, overridingGroup);
        }
    }
}
