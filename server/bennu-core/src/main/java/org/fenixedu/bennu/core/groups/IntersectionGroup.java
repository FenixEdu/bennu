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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentIntersectionGroup;
import org.joda.time.DateTime;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Intersection composition group.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
public final class IntersectionGroup extends Group {
    private static final long serialVersionUID = -6302479291671819210L;

    private final ImmutableSet<Group> children;

    protected IntersectionGroup(ImmutableSet<Group> children) {
        super();
        this.children = children;
    }

    public static Group of(Set<Group> groups) {
        return of(groups.toArray(new Group[0]));
    }

    public static Group of(Group... groups) {
        if (groups.length == 0) {
            return NobodyGroup.get();
        }
        Group group = groups[0];
        for (int i = 1; i < groups.length; i++) {
            group = group.and(groups[i]);
        }
        return group;
    }

    @Override
    public String getPresentationName() {
        return "(" + Joiner.on(" & ").join(Iterables.transform(children, groupToGroupName)) + ")";
    }

    @Override
    public String getExpression() {
        return "(" + Joiner.on(" & ").join(Iterables.transform(children, groupToExpression)) + ")";
    }

    public Set<Group> getChildren() {
        return children;
    }

    @Override
    public PersistentIntersectionGroup toPersistentGroup() {
        ImmutableSet<PersistentGroup> groups = FluentIterable.from(children).transform(groupToPersistentGroup).toSet();
        return PersistentIntersectionGroup.getInstance(groups);
    }

    @Override
    public Set<User> getMembers() {
        final Set<User> users = new HashSet<>();
        Iterator<Group> iterator = children.iterator();
        if (iterator.hasNext()) {
            users.addAll(iterator.next().getMembers());
            while (iterator.hasNext()) {
                users.retainAll(iterator.next().getMembers());
            }
        }
        return users;
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
    public Set<User> getMembers(DateTime when) {
        final Set<User> users = new HashSet<>();
        Iterator<Group> iterator = children.iterator();
        if (iterator.hasNext()) {
            users.addAll(iterator.next().getMembers(when));
            while (iterator.hasNext()) {
                users.retainAll(iterator.next().getMembers(when));
            }
        }
        return users;
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
            return NobodyGroup.get();
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
