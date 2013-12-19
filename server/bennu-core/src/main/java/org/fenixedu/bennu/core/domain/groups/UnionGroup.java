/*
 * UnionGroup.java
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
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.joda.time.DateTime;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * Union composition group.
 * 
 * @see Group
 */
public class UnionGroup extends UnionGroup_Base {
    protected UnionGroup(Set<Group> children) {
        super();
        init(children);
    }

    @Override
    protected String operator() {
        return "|";
    }

    @Override
    public Set<User> getMembers() {
        final Set<User> users = new HashSet<>();
        for (final Group group : getChildrenSet()) {
            users.addAll(group.getMembers());
        }
        return users;
    }

    @Override
    public boolean isMember(final User user) {
        for (final Group group : getChildrenSet()) {
            if (group.isMember(user)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        final Set<User> users = new HashSet<>();
        for (final Group group : getChildrenSet()) {
            users.addAll(group.getMembers(when));
        }
        return users;
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        for (final Group group : getChildrenSet()) {
            if (group.isMember(user, when)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Group or(Group group) {
        Set<Group> children = new HashSet<>(getChildrenSet());
        children.add(group);
        return UnionGroup.getInstance(children);
    }

    /**
     * @see #getInstance(Set)
     */
    public static UnionGroup getInstance(final Group... children) {
        return getInstance(new HashSet<>(Arrays.asList(children)));
    }

    /**
     * Get or create instance of a {@link UnionGroup} between the requested children.
     * 
     * @param children
     *            the groups to make a {@link UnionGroup} on.
     * @return {@link UnionGroup} instance
     */
    public static UnionGroup getInstance(final Set<Group> children) {
        return select(UnionGroup.class, new Predicate<UnionGroup>() {
            @Override
            public boolean apply(UnionGroup input) {
                return Sets.symmetricDifference(input.getChildrenSet(), children).isEmpty();
            }
        }, new Supplier<UnionGroup>() {
            @Override
            public UnionGroup get() {
                return new UnionGroup(children);
            }
        });
    }
}
