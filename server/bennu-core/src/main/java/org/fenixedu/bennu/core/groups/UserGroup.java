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
package org.fenixedu.bennu.core.groups;

import java.util.Set;

import org.fenixedu.bennu.core.annotation.GroupArgument;
import org.fenixedu.bennu.core.annotation.GroupArgumentParser;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentUserGroup;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Groups of an explicit set of users. Order is not relevant.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
@GroupOperator("U")
public final class UserGroup extends CustomGroup {
    private static final long serialVersionUID = -6178473769354461857L;

    @GroupArgumentParser
    public static class UserArgumentParser implements ArgumentParser<User> {
        @Override
        public User parse(String argument) {
            return User.findByUsername(argument);
        }

        @Override
        public String serialize(User argument) {
            return argument.getUsername();
        }

        @Override
        public Class<User> type() {
            return User.class;
        }
    }

    @GroupArgument("")
    private Set<User> members;

    private UserGroup() {
        super();
    }

    private UserGroup(ImmutableSet<User> members) {
        super();
        this.members = members;
    }

    public static Group of(ImmutableSet<User> members) {
        if (members.isEmpty()) {
            return NobodyGroup.get();
        }
        return new UserGroup(members);
    }

    public static Group of(Set<User> members) {
        if (members.isEmpty()) {
            return NobodyGroup.get();
        }
        return new UserGroup(ImmutableSet.copyOf(members));
    }

    public static Group of(User... members) {
        if (members.length == 0) {
            return NobodyGroup.get();
        }
        return new UserGroup(ImmutableSet.copyOf(members));
    }

    @Override
    public String getPresentationName() {
        Iterable<String> usernames = Iterables.transform(members, new Function<User, String>() {
            @Override
            public String apply(User user) {
                return user.getUsername();
            }
        });

        return Joiner.on(", ").join(usernames);
    }

    @Override
    public PersistentUserGroup toPersistentGroup() {
        return PersistentUserGroup.getInstance(members);
    }

    @Override
    public Set<User> getMembers() {
        return members;
    }

    @Override
    public boolean isMember(User user) {
        return user != null && members.contains(user);
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
            return UserGroup.of(Sets.intersection(members, ((UserGroup) group).members));
        }
        return super.and(group);
    }

    @Override
    public Group or(Group group) {
        if (group instanceof UserGroup) {
            return UserGroup.of(Sets.union(members, ((UserGroup) group).members));
        }
        return super.or(group);
    }

    @Override
    public Group minus(Group group) {
        if (group instanceof UserGroup) {
            return UserGroup.of(Sets.difference(members, ((UserGroup) group).members));
        }
        return super.minus(group);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof UserGroup) {
            return members.equals(((UserGroup) object).members);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }
}
