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
package pt.ist.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.fenixframework.Atomic;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Difference composition group. Can be read as members of first group except members of the remaining ones.
 * 
 * @see BennuGroup
 */
public class DifferenceGroup extends DifferenceGroup_Base {
    protected DifferenceGroup(Set<BennuGroup> children) {
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
        Iterator<BennuGroup> iterator = getChildrenSet().iterator();
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
        Iterator<BennuGroup> iterator = getChildrenSet().iterator();
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
        Iterator<BennuGroup> iterator = getChildrenSet().iterator();
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
        Iterator<BennuGroup> iterator = getChildrenSet().iterator();
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
    public BennuGroup minus(BennuGroup group) {
        Set<BennuGroup> children = new HashSet<>(getChildrenSet());
        children.add(group);
        return DifferenceGroup.getInstance(children);
    }

    /**
     * @see #getInstance(Set)
     */
    @Atomic
    public static DifferenceGroup getInstance(final BennuGroup... children) {
        return getInstance(new HashSet<>(Arrays.asList(children)));
    }

    /**
     * Get or create instance of a {@link DifferenceGroup} between the requested children.
     * 
     * @param children
     *            the groups to make a {@link DifferenceGroup} on.
     * @return singleton {@link DifferenceGroup} instance
     */
    @Atomic
    public static DifferenceGroup getInstance(final Set<BennuGroup> children) {
        DifferenceGroup group = select(DifferenceGroup.class, new Predicate<DifferenceGroup>() {
            @Override
            public boolean apply(DifferenceGroup input) {
                return Iterables.elementsEqual(input.getChildrenSet(), children);
            }
        });
        return group != null ? group : new DifferenceGroup(children);
    }
}
