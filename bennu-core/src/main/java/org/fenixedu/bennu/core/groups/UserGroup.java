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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.annotation.GroupArgument;
import org.fenixedu.bennu.core.annotation.GroupArgumentParser;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentUserGroup;
import org.joda.time.DateTime;

/**
 * Groups of an explicit set of users. Order is not relevant.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
@GroupOperator("U")
final class UserGroup extends CustomGroup {
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

    UserGroup() {
        super();
    }

    UserGroup(Set<User> members) {
        // this set must be immutable
        super();
        this.members = members;
    }

    @Override
    public String getPresentationName() {
        return members.stream().map(User::getUsername).collect(Collectors.joining(", "));
    }

    @Override
    public PersistentGroup toPersistentGroup() {
        return PersistentUserGroup.getInstance(members);
    }

    @Override
    public Stream<User> getMembers() {
        return members.stream();
    }

    @Override
    public boolean isMember(User user) {
        return user != null && members.contains(user);
    }

    @Override
    public Stream<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

    @Override
    public Group and(Group group) {
        if (group instanceof UserGroup) {
            return Group.users(members.stream().filter(user -> ((UserGroup) group).members.contains(user)));
        }
        return super.and(group);
    }

    @Override
    public Group or(Group group) {
        if (group instanceof UserGroup) {
            return Group.users(Stream.concat(members.stream(), ((UserGroup) group).members.stream()).distinct());
        }
        return super.or(group);
    }

    @Override
    public Group minus(Group group) {
        if (group instanceof UserGroup) {
            return Group.users(members.stream().filter(user -> !((UserGroup) group).members.contains(user)));
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
