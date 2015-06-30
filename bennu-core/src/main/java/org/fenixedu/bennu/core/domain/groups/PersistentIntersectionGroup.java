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
package org.fenixedu.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.joda.time.DateTime;

import pt.ist.fenixframework.dml.runtime.Relation;

/**
 * Intersection composition group.
 * 
 * @see PersistentGroup
 */
public final class PersistentIntersectionGroup extends PersistentIntersectionGroup_Base {
    protected PersistentIntersectionGroup(Set<PersistentGroup> children) {
        super();
        getChildrenSet().addAll(children);
    }

    @Override
    public Group toGroup() {
        return getChildrenSet().stream().map(PersistentGroup::toGroup).reduce(Group::and).orElseGet(Group::nobody);
    }

    @Override
    public boolean isMember(User user) {
        if (getChildrenSet().isEmpty()) {
            return false;
        }
        return getChildrenSet().stream().allMatch(group -> group.isMember(user));
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        if (getChildrenSet().isEmpty()) {
            return false;
        }
        return getChildrenSet().stream().allMatch(group -> group.isMember(user, when));
    }

    @Override
    protected Collection<Relation<?, ?>> getContextRelations() {
        return Collections.singleton(getRelationIntersectionGroupComposition());
    }

    /**
     * Get or create instance of a {@link PersistentIntersectionGroup} between the requested children.
     * 
     * @param children the groups to make a {@link PersistentIntersectionGroup} on.
     * @return singleton {@link PersistentIntersectionGroup} instance
     * @see #getInstance(Set)
     */
    public static PersistentIntersectionGroup getInstance(final PersistentGroup... children) {
        return getInstance(new HashSet<>(Arrays.asList(children)));
    }

    /**
     * Get or create instance of a {@link PersistentIntersectionGroup} between the requested children.
     * 
     * @param children the groups to make a {@link PersistentIntersectionGroup} on.
     * @return singleton {@link PersistentIntersectionGroup} instance
     */
    public static PersistentIntersectionGroup getInstance(final Set<PersistentGroup> children) {
        return singleton(() -> select(children), () -> new PersistentIntersectionGroup(children));
    }

    private static Optional<PersistentIntersectionGroup> select(final Set<PersistentGroup> children) {
        Stream<PersistentIntersectionGroup> intersection = null;
        for (final PersistentGroup child : children) {
            if (intersection == null) {
                intersection = child.getIntersectionsSet().stream().filter(g -> g.getChildrenSet().size() == children.size());
            } else {
                intersection = intersection.filter(g -> g.getChildrenSet().contains(child));
            }
        }
        return intersection != null ? intersection.findAny() : Optional.empty();
    }
}
