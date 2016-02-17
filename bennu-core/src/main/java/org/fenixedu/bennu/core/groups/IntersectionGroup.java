/*
 * IntersectionGroup.java
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

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentIntersectionGroup;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

import com.google.common.collect.ImmutableSet;

/**
 * Intersection composition group.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
final class IntersectionGroup extends Group {
    private static final long serialVersionUID = -6302479291671819210L;

    private final ImmutableSet<Group> children;

    IntersectionGroup(ImmutableSet<Group> children) {
        super();
        this.children = children;
    }

    @Override
    public String getPresentationName() {
        String and = " " + BundleUtil.getString("resources.BennuResources", "label.bennu.and") + " ";
        return children.stream().map(g -> g.getPresentationName()).collect(Collectors.joining(and));
    }

    @Override
    public String getExpression() {
        return children.stream().map(Group::compositeExpression).collect(Collectors.joining(" & "));
    }

    @Override
    public PersistentGroup toPersistentGroup() {
        return PersistentIntersectionGroup.getInstance(children.stream().map(g -> g.toPersistentGroup())
                .collect(Collectors.toSet()));
    }

    @Override
    public Stream<User> getMembers() {
        return Bennu.getInstance().getUserSet().stream().filter(user -> isMember(user));
    }

    @Override
    public boolean isMember(final User user) {
        if (children.isEmpty()) {
            return false;
        }
        for (final Group group : children) {
            if (!group.isMember(user)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Stream<User> getMembers(DateTime when) {
        return Bennu.getInstance().getUserSet().stream().filter(user -> isMember(user, when));
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        if (children.isEmpty()) {
            return false;
        }
        for (final Group group : children) {
            if (!group.isMember(user, when)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Group and(Group group) {
        if (this.equals(group)) {
            return this;
        }
        if (group instanceof NobodyGroup) {
            return Group.nobody();
        }
        if (group instanceof AnyoneGroup) {
            return this;
        }
        if (group instanceof IntersectionGroup) {
            return new IntersectionGroup(ImmutableSet.<Group> builder().addAll(children)
                    .addAll(((IntersectionGroup) group).children).build());
        }
        return new IntersectionGroup(ImmutableSet.<Group> builder().addAll(children).add(group).build());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof IntersectionGroup) {
            return children.equals(((IntersectionGroup) object).children);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return children.hashCode();
    }
}
