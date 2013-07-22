/*
 * DynamicGroup.java
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
package pt.ist.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.BennuCoreDomainException;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.fenixframework.Atomic;

import com.google.common.base.Predicate;

/**
 * <p>
 * Mutable named group. In the group language is referred by {@literal#}name. Keeps history of every change made.
 * </p>
 * 
 * <p>
 * In practice it's a wrapper over an immutable group with versioning over the relation between this and the wrapped group.
 * </p>
 * 
 * @see Group
 */
public final class DynamicGroup extends DynamicGroup_Base {
    protected DynamicGroup() {
        super();
    }

    protected DynamicGroup(String name, Group group) {
        this();
        setName(name);
        setCreated(new DateTime());
        setCreator(Authenticate.getUser());
        setGroup(group);
    }

    @Override
    public String getPresentationName() {
        return getName() + ": (" + getGroup().getPresentationName() + ")";
    }

    @Override
    public String expression() {
        return "#" + getName();
    }

    @Override
    public Set<User> getMembers() {
        return getGroup().getMembers();
    }

    @Override
    public boolean isMember(User user) {
        return getGroup().isMember(user);
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        Group group = getGroup(when);
        return group != null ? group.getMembers() : Collections.<User> emptySet();
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        Group group = getGroup(when);
        return group != null ? group.isMember(user) : false;
    }

    public Group getGroup(DateTime when) {
        if (when.isAfter(getCreated())) {
            return getGroup();
        }
        if (getPrevious() != null) {
            return getPrevious().getGroup(when);
        }
        return null;
    }

    private void pushHistory() {
        DynamicGroup old = new DynamicGroup();
        old.setHost(null);
        old.setName(getName());
        old.setCreated(getCreated());
        old.setCreator(getCreator());
        old.setGroup(getGroup());
        old.setNext(this);
        setCreated(new DateTime());
        setCreator(Authenticate.getUser());
    }

    public DynamicGroup rename(String name) {
        pushHistory();
        setName(name);
        return this;
    }

    public DynamicGroup changeGroup(Group group) {
        if (!group.equals(getGroup())) {
            pushHistory();
            setGroup(group);
        }
        return this;
    }

    @Override
    public Group and(Group group) {
        return getGroup().and(group);
    }

    @Override
    public Group or(Group group) {
        return getGroup().or(group);
    }

    @Override
    public Group minus(Group group) {
        return getGroup().minus(group);
    }

    @Override
    public Group not() {
        return getGroup().not();
    }

    @Override
    public Group grant(User user) {
        return getGroup().grant(user);
    }

    @Override
    public Group revoke(User user) {
        return getGroup().revoke(user);
    }

    @Override
    protected boolean isGarbageCollectable() {
        // Dynamic (named) groups cannot be recovered only from language, therefore we can never safely delete.
        return false;
    }

    @Atomic
    public static DynamicGroup getInstance(final String name) {
        DynamicGroup group = select(DynamicGroup.class, new Predicate<DynamicGroup>() {
            @Override
            public boolean apply(DynamicGroup input) {
                return input.getName().equals(name);
            }
        });
        if (group != null) {
            return group;
        }
        throw BennuCoreDomainException.dynamicGroupNotFound(name);
    }

    @Atomic
    public static DynamicGroup initialize(final String name, Group overridingGroup) {
        DynamicGroup group = select(DynamicGroup.class, new Predicate<DynamicGroup>() {
            @Override
            public boolean apply(DynamicGroup input) {
                return input.getName().equals(name);
            }
        });
        if (group != null) {
            group.changeGroup(overridingGroup);
            return group;
        }
        return new DynamicGroup(name, overridingGroup);
    }
}
