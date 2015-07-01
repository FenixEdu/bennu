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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.joda.time.DateTime;

import pt.ist.fenixframework.dml.runtime.Relation;

/**
 * Groups of specific users.
 * 
 * @see PersistentGroup
 */
public final class PersistentUserGroup extends PersistentUserGroup_Base {
    protected PersistentUserGroup(Set<User> members) {
        super();
        getMemberSet().addAll(members);
    }

    @Override
    public Stream<User> getMembers() {
        return getMemberSet().stream();
    }

    @Override
    public Stream<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user) {
        return getMemberSet().contains(user);
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

    @Override
    public Group toGroup() {
        return Group.users(getMemberSet().stream());
    }

    @Override
    protected Collection<Relation<?, ?>> getContextRelations() {
        return Collections.singleton(getRelationUserGroupMembers());
    }

    /**
     * Get or create instance of a {@link PersistentUserGroup} for the requested users
     * 
     * @param users the users to be part of the group
     * @return {@link PersistentUserGroup} instance
     * @see #getInstance(Set)
     */
    public static PersistentUserGroup getInstance(User... users) {
        return getInstance(new HashSet<>(Arrays.asList(users)));
    }

    /**
     * Get or create instance of a {@link PersistentUserGroup} for the requested users
     * 
     * @param users the users to be part of the group
     * @return {@link PersistentUserGroup} instance
     */
    public static PersistentUserGroup getInstance(final Set<User> users) {
        return singleton(() -> select(users), () -> new PersistentUserGroup(users));
    }

    private static Optional<PersistentUserGroup> select(final Set<User> users) {
        Stream<PersistentUserGroup> intersection = null;
        for (User user : users) {
            if (intersection == null) {
                intersection = BennuGroupIndex.getUserGroups(user).stream().filter(g -> g.getMemberSet().size() == users.size());
            } else {
                intersection = intersection.filter(g -> g.getMemberSet().contains(user));
            }
        }
        return intersection != null ? intersection.findAny() : Optional.empty();
    }
}
