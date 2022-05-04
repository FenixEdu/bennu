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

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.dml.runtime.Relation;

import com.google.common.collect.Sets;

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

    @Override
    public Stream<User> getMembers() {
        return getGroup().getMembers();
    }

    @Override
    public Stream<User> getMembers(DateTime when) {
        return getGroup(when).getMembers(when);
    }

    @Override
    public boolean isMember(User user) {
        return getGroup().isMember(user);
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return getGroup(when).isMember(user, when);
    }

    public PersistentGroup getGroup(DateTime when) {
        if (when.isAfter(getCreated())) {
            return getGroup();
        }
        if (getPrevious() != null) {
            return getPrevious().getGroup(when);
        }
        return Group.nobody().toPersistentGroup();
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
    protected void gc() {
        //Dynamic groups are never garbage collected automatically
    }

    public void delete() {
        if (getPrevious() != null) {
            getPrevious().gc();
        }
        super.gc();
    }

    @Override
    protected Collection<Relation<?, ?>> getContextRelations() {
        return Sets.newHashSet(getRelationDynamicGroupHistoric(), getRelationPersistentDynamicGroupCreator(),
                getRelationPersistentDynamicGroups(), getRelationPersistentDynamicGroupWrapper());
    }

    public static Optional<PersistentDynamicGroup> getInstance(final String name) {
        return BennuGroupIndex.dynamic(name);
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

    @Atomic(mode = TxMode.WRITE)
    public void changePresentationName(LocalizedString presentationName) {
        setCustomPresentationName(presentationName);
    }
}
