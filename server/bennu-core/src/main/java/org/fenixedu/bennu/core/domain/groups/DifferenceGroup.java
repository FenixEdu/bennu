/*
 * DifferenceGroup.java
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * Difference composition group. Can be read as members of first group except members of the remaining ones.
 * 
 * @see Group
 */
public final class DifferenceGroup extends DifferenceGroup_Base {
    protected DifferenceGroup(Set<Group> children) {
        super();
        init(children);
    }

    @Override
    protected String operator() {
        return "-";
    }

    @Override
    public Set<User> getMembers() {
        final Set<User> users = new HashSet<>();
        Iterator<Group> iterator = getChildrenSet().iterator();
        if (iterator.hasNext()) {
            users.addAll(iterator.next().getMembers());
            while (iterator.hasNext()) {
                users.removeAll(iterator.next().getMembers());
            }
        }
        return users;
    }

    @Override
    public boolean isMember(final User user) {
        Iterator<Group> iterator = getChildrenSet().iterator();
        if (iterator.hasNext()) {
            if (!iterator.next().isMember(user)) {
                return false;
            }
        } else {
            return false;
        }
        while (iterator.hasNext()) {
            if (iterator.next().isMember(user)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        final Set<User> users = new HashSet<>();
        Iterator<Group> iterator = getChildrenSet().iterator();
        if (iterator.hasNext()) {
            users.addAll(iterator.next().getMembers(when));
            while (iterator.hasNext()) {
                users.removeAll(iterator.next().getMembers());
            }
        }
        return users;
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        Iterator<Group> iterator = getChildrenSet().iterator();
        if (iterator.hasNext()) {
            if (!iterator.next().isMember(user, when)) {
                return false;
            }
        } else {
            return false;
        }
        while (iterator.hasNext()) {
            if (iterator.next().isMember(user, when)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Group minus(Group group) {
        if (this.equals(group)) {
            return NobodyGroup.getInstance();
        }
        if (group instanceof NobodyGroup) {
            return this;
        }
        if (group instanceof AnyoneGroup) {
            return NobodyGroup.getInstance();
        }
        return DifferenceGroup.getInstance(ImmutableSet.<Group> builder().addAll(getChildrenSet()).add(group).build());
    }

    /**
     * @see #getInstance(Set)
     */
    public static DifferenceGroup getInstance(final Group... children) {
        return getInstance(new HashSet<>(Arrays.asList(children)));
    }

    /**
     * Get or create instance of a {@link DifferenceGroup} between the requested children.
     * 
     * @param children
     *            the groups to make a {@link DifferenceGroup} on.
     * @return singleton {@link DifferenceGroup} instance
     */
    public static DifferenceGroup getInstance(final Set<Group> children) {
        DifferenceGroup instance = select(children);
        return instance != null ? instance : create(children);
    }

    @Atomic(mode = TxMode.WRITE)
    private static DifferenceGroup create(Set<Group> children) {
        DifferenceGroup instance = select(children);
        return instance != null ? instance : new DifferenceGroup(children);
    }

    private static DifferenceGroup select(final Set<Group> children) {
        FluentIterable<DifferenceGroup> intersection = null;
        Predicate<DifferenceGroup> sizePredicate = new Predicate<DifferenceGroup>() {
            @Override
            public boolean apply(DifferenceGroup group) {
                return group.getChildrenSet().size() == children.size();
            }
        };
        for (final Group child : children) {
            if (intersection == null) {
                intersection = FluentIterable.from(child.getCompositionSet()).filter(DifferenceGroup.class).filter(sizePredicate);
            } else {
                intersection = intersection.filter(new Predicate<DifferenceGroup>() {
                    @Override
                    public boolean apply(DifferenceGroup group) {
                        return group.getChildrenSet().contains(child);
                    }
                });
            }
            if (intersection.isEmpty()) {
                return null;
            }
        }
        return intersection.first().orNull();
    }
}
