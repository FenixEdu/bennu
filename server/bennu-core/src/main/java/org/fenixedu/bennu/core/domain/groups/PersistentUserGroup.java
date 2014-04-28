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
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.UserGroup;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

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
    public Group toGroup() {
        return UserGroup.of(getMemberSet());
    }

    @Override
    protected void gc() {
        getMemberSet().clear();
        super.gc();
    }

    /**
     * @see #getInstance(Set)
     */
    public static PersistentUserGroup getInstance(User... members) {
        return getInstance(new HashSet<>(Arrays.asList(members)));
    }

    /**
     * Get or create instance of a {@link PersistentUserGroup} for the requested users
     * 
     * @param users
     *            the users to be part of the group
     * @return {@link PersistentUserGroup} instance
     */
    public static PersistentUserGroup getInstance(final Set<User> users) {
        if (users.isEmpty()) {
            throw BennuCoreDomainException.creatingUserGroupsWithoutUsers();
        }
        PersistentUserGroup instance = select(users);
        return instance != null ? instance : create(users);
    }

    @Atomic(mode = TxMode.WRITE)
    private static PersistentUserGroup create(final Set<User> users) {
        PersistentUserGroup instance = select(users);
        return instance != null ? instance : new PersistentUserGroup(users);
    }

    private static PersistentUserGroup select(final Set<User> users) {
        FluentIterable<PersistentUserGroup> intersection = null;
        Predicate<PersistentUserGroup> sizePredicate = new Predicate<PersistentUserGroup>() {
            @Override
            public boolean apply(PersistentUserGroup group) {
                return group.getMemberSet().size() == users.size();
            }
        };
        for (final User user : users) {
            if (intersection == null) {
                intersection = FluentIterable.from(BennuGroupIndex.getUserGroups(user)).filter(sizePredicate);
            } else {
                intersection = intersection.filter(new Predicate<PersistentUserGroup>() {
                    @Override
                    public boolean apply(PersistentUserGroup group) {
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
