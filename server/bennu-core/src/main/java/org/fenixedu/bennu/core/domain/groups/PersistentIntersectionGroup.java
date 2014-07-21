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
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.IntersectionGroup;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

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
        Group[] children = FluentIterable.from(getChildrenSet()).transform(persistentGroupToGroup).toArray(Group.class);
        return IntersectionGroup.of(children);
    }

    @Override
    protected void gc() {
        getChildrenSet().clear();
        super.gc();
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
        PersistentIntersectionGroup instance = select(children);
        return instance != null ? instance : create(children);
    }

    @Atomic(mode = TxMode.WRITE)
    private static PersistentIntersectionGroup create(final Set<PersistentGroup> children) {
        PersistentIntersectionGroup instance = select(children);
        return instance != null ? instance : new PersistentIntersectionGroup(children);
    }

    private static PersistentIntersectionGroup select(final Set<PersistentGroup> children) {
        FluentIterable<PersistentIntersectionGroup> intersection = null;
        Predicate<PersistentIntersectionGroup> sizePredicate = new Predicate<PersistentIntersectionGroup>() {
            @Override
            public boolean apply(PersistentIntersectionGroup group) {
                return group.getChildrenSet().size() == children.size();
            }
        };
        for (final PersistentGroup child : children) {
            if (intersection == null) {
                intersection = FluentIterable.from(child.getIntersectionsSet()).filter(sizePredicate);
            } else {
                intersection = intersection.filter(new Predicate<PersistentIntersectionGroup>() {
                    @Override
                    public boolean apply(PersistentIntersectionGroup group) {
                        return group.getChildrenSet().contains(child);
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
