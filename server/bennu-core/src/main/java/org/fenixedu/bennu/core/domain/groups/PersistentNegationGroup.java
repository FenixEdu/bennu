/*
 * NegationGroup.java
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

import java.util.Optional;

import org.fenixedu.bennu.core.groups.Group;

/**
 * Inverse group of another group.
 * 
 * @see PersistentGroup
 */
public final class PersistentNegationGroup extends PersistentNegationGroup_Base {
    protected PersistentNegationGroup(PersistentGroup negated) {
        super();
        setNegated(negated);
    }

    @Override
    public Group toGroup() {
        return getNegated().toGroup().not();
    }

    @Override
    protected void gc() {
        setNegated(null);
        super.gc();
    }

    /**
     * Get or create singleton instance of {@link PersistentNegationGroup}
     * 
     * @param group the group to inverse
     * @return singleton {@link PersistentNegationGroup} instance
     */
    public static PersistentNegationGroup getInstance(final PersistentGroup group) {
        return singleton(() -> Optional.ofNullable(group.getNegation()), () -> new PersistentNegationGroup(group));
    }
}
