/*
 * IntersectionGroup.java
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
 * Intersection composition group.
 * 
 * @see Group
 */
public final class IntersectionGroup extends IntersectionGroup_Base {
    protected IntersectionGroup(Set<Group> children) {
        super();
        init(children);
    }

    @Override
    protected String operator() {
        return "&";
    }

    @Override
    public Set<User> getMembers() {
        final Set<User> users = new HashSet<>();
        Iterator<Group> iterator = getChildrenSet().iterator();
        if (iterator.hasNext()) {
            users.addAll(iterator.next().getMembers());
            while (iterator.hasNext()) {
                users.retainAll(iterator.next().getMembers());
            }
        }
        return users;
    }

    @Override
    public boolean isMember(final User user) {
        if (getChildrenSet().isEmpty()) {
            return false;
        }
        for (final Group group : getChildrenSet()) {
            if (!group.isMember(user)) {
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
                users.retainAll(iterator.next().getMembers(when));
            }
        }
        return users;
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        if (getChildrenSet().isEmpty()) {
            return false;
        }
        for (final Group group : getChildrenSet()) {
            if (!group.isMember(user, when)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Group and(Group group) {
        if (this.equals(group)) {
            return this;
        }
        if (group instanceof NobodyGroup) {
            return NobodyGroup.getInstance();
        }
        if (group instanceof AnyoneGroup) {
            return this;
        }
        return IntersectionGroup.getInstance(ImmutableSet.<Group> builder().addAll(getChildrenSet()).add(group).build());
    }

    /**
     * @see #getInstance(Set)
     */
    public static IntersectionGroup getInstance(final Group... children) {
        return getInstance(new HashSet<>(Arrays.asList(children)));
    }

    /**
     * Get or create instance of a {@link IntersectionGroup} between the requested children.
     * 
     * @param children
     *            the groups to make a {@link IntersectionGroup} on.
     * @return singleton {@link IntersectionGroup} instance
     */
    public static IntersectionGroup getInstance(final Set<Group> children) {
        IntersectionGroup instance = select(children);
        return instance != null ? instance : create(children);
    }

    @Atomic(mode = TxMode.WRITE)
    private static IntersectionGroup create(final Set<Group> children) {
        IntersectionGroup instance = select(children);
        return instance != null ? instance : new IntersectionGroup(children);
    }

    private static IntersectionGroup select(final Set<Group> children) {
        FluentIterable<IntersectionGroup> intersection = null;
        Predicate<IntersectionGroup> sizePredicate = new Predicate<IntersectionGroup>() {
            @Override
            public boolean apply(IntersectionGroup group) {
                return group.getChildrenSet().size() == children.size();
            }
        };
        for (final Group child : children) {
            if (intersection == null) {
                intersection =
                        FluentIterable.from(child.getCompositionSet()).filter(IntersectionGroup.class).filter(sizePredicate);
            } else {
                intersection = intersection.filter(new Predicate<IntersectionGroup>() {
                    @Override
                    public boolean apply(IntersectionGroup group) {
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
