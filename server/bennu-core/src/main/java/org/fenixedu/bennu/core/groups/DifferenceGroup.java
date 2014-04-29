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
package org.fenixedu.bennu.core.groups;

import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentDifferenceGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Difference composition group. Can be read as members of first group except members of the remaining ones.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
public final class DifferenceGroup extends Group {
    private static final long serialVersionUID = 7610837328092733166L;

    private final Group first;

    private final ImmutableSet<Group> rest;

    protected DifferenceGroup(Group first, ImmutableSet<Group> rest) {
        super();
        this.first = first;
        this.rest = rest;
    }

    public static Group between(Group first, Set<Group> rest) {
        return between(first, rest.toArray(new Group[0]));
    }

    public static Group between(Group first, Group... rest) {
        if (rest.length == 0) {
            return first;
        }
        Group group = first;
        for (Group subtract : rest) {
            group = group.minus(subtract);
        }
        return group;
    }

    @Override
    public String getPresentationName() {
        String minus = " " + BundleUtil.getString("resources.BennuResources", "label.bennu.minus") + " ";
        return first.getPresentationName() + minus + Joiner.on(minus).join(Iterables.transform(rest, groupToGroupName));
    }

    @Override
    public String getExpression() {
        return first.getExpression() + " - " + Joiner.on(" - ").join(Iterables.transform(rest, groupToExpression));
    }

    public Group getFirst() {
        return first;
    }

    public Set<Group> getRest() {
        return rest;
    }

    @Override
    public PersistentDifferenceGroup toPersistentGroup() {
        ImmutableSet<PersistentGroup> groups = FluentIterable.from(rest).transform(groupToPersistentGroup).toSet();
        return PersistentDifferenceGroup.getInstance(first.toPersistentGroup(), groups);
    }

    @Override
    public Set<User> getMembers() {
        final Set<User> users = new HashSet<>();
        users.addAll(first.getMembers());
        for (Group group : rest) {
            users.removeAll(group.getMembers());
        }
        return users;
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        final Set<User> users = new HashSet<>();
        users.addAll(first.getMembers(when));
        for (Group group : rest) {
            users.removeAll(group.getMembers(when));
        }
        return users;
    }

    @Override
    public boolean isMember(final User user) {
        if (!first.isMember(user)) {
            return false;
        }
        for (Group group : rest) {
            if (group.isMember(user)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        if (!first.isMember(user, when)) {
            return false;
        }
        for (Group group : rest) {
            if (group.isMember(user, when)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Group minus(Group group) {
        if (this.equals(group)) {
            return NobodyGroup.get();
        }
        if (group instanceof NobodyGroup) {
            return this;
        }
        if (group instanceof AnyoneGroup) {
            return NobodyGroup.get();
        }
        return new DifferenceGroup(first, ImmutableSet.<Group> builder().addAll(rest).add(group).build());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DifferenceGroup) {
            DifferenceGroup diff = (DifferenceGroup) object;
            return first.equals(diff.rest) && rest.equals(diff.rest);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(first.hashCode() + rest.hashCode());
    }
}
