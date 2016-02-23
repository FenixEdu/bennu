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

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentDifferenceGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

import com.google.common.collect.ImmutableSet;

/**
 * Difference composition group. Can be read as members of first group except members of the remaining ones.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
final class DifferenceGroup extends Group {
    private static final long serialVersionUID = 7610837328092733166L;

    private final Group first;

    private final ImmutableSet<Group> rest;

    DifferenceGroup(Group first, ImmutableSet<Group> rest) {
        super();
        this.first = first;
        this.rest = rest;
    }

    @Override
    public String getPresentationName() {
        String minus = " " + BundleUtil.getString("resources.BennuResources", "label.bennu.minus") + " ";
        return first.getPresentationName() + minus
                + rest.stream().map(g -> g.getPresentationName()).collect(Collectors.joining(minus));
    }

    @Override
    public String getExpression() {
        return first.getExpression() + " - " + rest.stream().map(Group::compositeExpression).collect(Collectors.joining(" - "));
    }

    @Override
    public PersistentGroup toPersistentGroup() {
        return PersistentDifferenceGroup.getInstance(first.toPersistentGroup(), rest.stream().map(g -> g.toPersistentGroup())
                .collect(Collectors.toSet()));
    }

    @Override
    public Stream<User> getMembers() {
        return first.getMembers().filter(user -> !rest.stream().anyMatch(g -> g.isMember(user)));
    }

    @Override
    public Stream<User> getMembers(DateTime when) {
        return first.getMembers(when).filter(user -> !rest.stream().anyMatch(g -> g.isMember(user, when)));
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
            return Group.nobody();
        }
        if (group instanceof NobodyGroup) {
            return this;
        }
        if (group instanceof AnyoneGroup) {
            return Group.nobody();
        }
        return new DifferenceGroup(first, ImmutableSet.<Group> builder().addAll(rest).add(group).build());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DifferenceGroup) {
            DifferenceGroup diff = (DifferenceGroup) object;
            return first.equals(diff.first) && rest.equals(diff.rest);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(first.hashCode() + rest.hashCode());
    }
}
