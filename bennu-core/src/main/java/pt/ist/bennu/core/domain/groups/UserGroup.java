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
package pt.ist.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.service.Service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Groups of specific users.
 * 
 * @see BennuGroup
 */
public class UserGroup extends UserGroup_Base {
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

        return "P(" + Joiner.on(", ").join(usernames) + ")";
    }

    @Override
    public Set<User> getMembers() {
        return getMemberSet();
    }

    @Override
    public boolean isMember(User user) {
        return getMemberSet().contains(user);
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
    public UserGroup grant(User user) {
        Set<User> users = new HashSet<>(getMemberSet());
        users.add(user);
        return UserGroup.getInstance(users);
    }

    @Override
    public UserGroup revoke(User user) {
        Set<User> users = new HashSet<>(getMemberSet());
        users.remove(user);
        return UserGroup.getInstance(users);
    }

    @Override
    protected void gc() {
        getMemberSet().clear();
        super.gc();
    }

    /**
     * @see #getInstance(Set)
     */
    @Service
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
    @Service
    public static UserGroup getInstance(final Set<User> users) {
        UserGroup group = select(UserGroup.class, new Predicate<UserGroup>() {
            @Override
            public boolean apply(UserGroup input) {
                return Sets.symmetricDifference(input.getMemberSet(), users).isEmpty();
            }
        });
        return group != null ? group : new UserGroup(users);
    }
}
