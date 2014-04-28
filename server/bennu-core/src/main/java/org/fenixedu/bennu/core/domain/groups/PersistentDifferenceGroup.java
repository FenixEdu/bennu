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
package org.fenixedu.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.core.groups.DifferenceGroup;
import org.fenixedu.bennu.core.groups.Group;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * Difference composition group. Can be read as members of first group except members of the remaining ones.
 * 
 * @see PersistentGroup
 */
public final class PersistentDifferenceGroup extends PersistentDifferenceGroup_Base {
    protected PersistentDifferenceGroup(PersistentGroup first, Set<PersistentGroup> rest) {
        super();
        setFirst(first);
        getRestSet().addAll(rest);
    }

    @Override
    public Group toGroup() {
        Group[] children = FluentIterable.from(getRestSet()).transform(persistentGroupToGroup).toArray(Group.class);
        return DifferenceGroup.between(getFirst().toGroup(), children);
    }

    @Override
    protected void gc() {
        setFirst(null);
        getRestSet().clear();
        super.gc();
    }

    /**
     * @see #getInstance(Set)
     */
    public static PersistentDifferenceGroup getInstance(final PersistentGroup first, final PersistentGroup... children) {
        return getInstance(first, new HashSet<>(Arrays.asList(children)));
    }

    /**
     * Get or create instance of a {@link PersistentDifferenceGroup} between the first group and the rest.
     * 
     * @param first the group from which to subtract
     * @param rest the groups to subtract.
     * @return singleton {@link PersistentDifferenceGroup} instance
     */
    public static PersistentDifferenceGroup getInstance(final PersistentGroup first, final Set<PersistentGroup> rest) {
        PersistentDifferenceGroup instance = select(first, rest);
        return instance != null ? instance : create(first, rest);
    }

    @Atomic(mode = TxMode.WRITE)
    private static PersistentDifferenceGroup create(PersistentGroup first, Set<PersistentGroup> rest) {
        PersistentDifferenceGroup instance = select(first, rest);
        return instance != null ? instance : new PersistentDifferenceGroup(first, rest);
    }

    public static PersistentDifferenceGroup select(final PersistentGroup first, final Set<PersistentGroup> rest) {
        if (first.getDifferenceAtFirstSet().isEmpty()) {
            return null;
        }
        Predicate<PersistentDifferenceGroup> sizePredicate = new Predicate<PersistentDifferenceGroup>() {
            @Override
            public boolean apply(PersistentDifferenceGroup group) {
                return group.getRestSet().size() == rest.size();
            }
        };
        FluentIterable<PersistentDifferenceGroup> intersection =
                FluentIterable.from(first.getDifferenceAtFirstSet()).filter(sizePredicate);
        for (final PersistentGroup child : rest) {
            intersection = intersection.filter(new Predicate<PersistentDifferenceGroup>() {
                @Override
                public boolean apply(PersistentDifferenceGroup group) {
                    return group.getDifferenceAtRestSet().contains(child);
                }
            });
            if (intersection.isEmpty()) {
                return null;
            }
        }
        return intersection.first().orNull();
    }
}
