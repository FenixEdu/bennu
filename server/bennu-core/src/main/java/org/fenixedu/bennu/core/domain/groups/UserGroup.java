/*
 * UserGroup.java
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Groups of specific users.
 * 
 * @see Group
 */
public final class UserGroup extends UserGroup_Base {
    protected UserGroup(Set<User> users) {
        super();
        getMemberSet().addAll(users);
    }

    @Override
    public String getPresentationName() {
        Iterable<String> usernames = Iterables.transform(getMemberSet(), new Function<User, String>() {
            @Override
            public String apply(User user) {
                return user.getUsername();
            }
        });

        return Joiner.on(", ").join(usernames);
    }

    @Override
    public String expression() {
        Iterable<String> usernames = Iterables.transform(getMemberSet(), new Function<User, String>() {
            @Override
            public String apply(User user) {
                return user.getUsername();
            }
        });

        return "U(" + Joiner.on(", ").join(usernames) + ")";
    }

    @Override
    public Set<User> getMembers() {
        return Collections.unmodifiableSet(getMemberSet());
    }

    @Override
    public boolean isMember(User user) {
        return user != null && getMemberSet().contains(user);
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

    @Override
    public Group and(Group group) {
        if (group instanceof UserGroup) {
            Set<User> intersection = Sets.intersection(getMemberSet(), ((UserGroup) group).getMemberSet());
            if (intersection.isEmpty()) {
                return NobodyGroup.getInstance();
            }
            return UserGroup.getInstance(intersection);
        }
        return super.and(group);
    }

    @Override
    public Group or(Group group) {
        if (group instanceof UserGroup) {
            return UserGroup.getInstance(Sets.union(getMemberSet(), ((UserGroup) group).getMemberSet()));
        }
        return super.or(group);
    }

    @Override
    public Group minus(Group group) {
        if (group instanceof UserGroup) {
            Set<User> difference = Sets.difference(getMemberSet(), ((UserGroup) group).getMemberSet());
            if (difference.isEmpty()) {
                return NobodyGroup.getInstance();
            }
            return UserGroup.getInstance(difference);
        }
        return super.minus(group);
    }

    @Override
    protected void gc() {
        getMemberSet().clear();
        super.gc();
    }

    /**
     * @see #getInstance(Set)
     */
    public static UserGroup getInstance(User... users) {
        return getInstance(new HashSet<>(Arrays.asList(users)));
    }

    /**
     * Get or create instance of a {@link UserGroup} for the requested users
     * 
     * @param users
     *            the users to be part of the group
     * @return {@link UserGroup} instance
     */
    public static UserGroup getInstance(final Set<User> users) {
        if (users.isEmpty()) {
            throw BennuCoreDomainException.creatingUserGroupsWithoutUsers();
        }
        UserGroup instance = select(users);
        return instance != null ? instance : create(users);
    }

    @Atomic(mode = TxMode.WRITE)
    private static UserGroup create(final Set<User> users) {
        UserGroup instance = select(users);
        return instance != null ? instance : new UserGroup(users);
    }

    private static UserGroup select(final Set<User> users) {
        FluentIterable<UserGroup> intersection = null;
        Predicate<UserGroup> sizePredicate = new Predicate<UserGroup>() {
            @Override
            public boolean apply(UserGroup group) {
                return group.getMemberSet().size() == users.size();
            }
        };
        for (final User user : users) {
            if (intersection == null) {
                intersection = FluentIterable.from(BennuGroupIndex.getUserGroups(user)).filter(sizePredicate);
            } else {
                intersection = intersection.filter(new Predicate<UserGroup>() {
                    @Override
                    public boolean apply(UserGroup group) {
                        return group.getMemberSet().contains(user);
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
