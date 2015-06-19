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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.joda.time.DateTime;

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
        return getRestSet().stream().map(PersistentGroup::toGroup).reduce(getFirst().toGroup(), Group::minus);
    }

    @Override
    public boolean isMember(User user) {
        if (getFirst().isMember(user)) {
            return !getRestSet().stream().anyMatch(group -> group.isMember(user));
        }
        return false;
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        if (getFirst().isMember(user, when)) {
            return !getRestSet().stream().anyMatch(group -> group.isMember(user, when));
        }
        return false;
    }

    @Override
    protected void gc() {
        setFirst(null);
        getRestSet().clear();
        super.gc();
    }

    /**
     * Get or create instance of a {@link PersistentDifferenceGroup} between the first group and the rest.
     * 
     * @param first the group from which to subtract
     * @param rest the groups to subtract.
     * @return singleton {@link PersistentDifferenceGroup} instance
     * @see #getInstance(PersistentGroup, Set)
     */
    public static PersistentDifferenceGroup getInstance(final PersistentGroup first, final PersistentGroup... rest) {
        return getInstance(first, new HashSet<>(Arrays.asList(rest)));
    }

    /**
     * Get or create instance of a {@link PersistentDifferenceGroup} between the first group and the rest.
     * 
     * @param first the group from which to subtract
     * @param rest the groups to subtract.
     * @return singleton {@link PersistentDifferenceGroup} instance
     */
    public static PersistentDifferenceGroup getInstance(final PersistentGroup first, final Set<PersistentGroup> rest) {
        return singleton(() -> select(first, rest), () -> new PersistentDifferenceGroup(first, rest));
    }

    private static Optional<PersistentDifferenceGroup> select(final PersistentGroup first, final Set<PersistentGroup> rest) {
        if (first.getDifferenceAtFirstSet().isEmpty()) {
            return Optional.empty();
        }
        Stream<PersistentDifferenceGroup> intersection =
                first.getDifferenceAtFirstSet().stream().filter(g -> g.getRestSet().size() == rest.size());
        for (final PersistentGroup child : rest) {
            intersection = intersection.filter(g -> g.getDifferenceAtRestSet().contains(child));
        }
        return intersection.findAny();
    }
}
