/*
 * Union.java
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
package pt.ist.bennu.core.grouplanguage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.ist.bennu.core.domain.groups.GroupException;
import pt.ist.bennu.core.domain.groups.PersistentGroup;
import pt.ist.bennu.core.domain.groups.UnionGroup;

class Union extends Group {
    private final List<Group> children;

    public Union(List<Group> children) {
        this.children = children;
    }

    @Override
    public PersistentGroup group() throws GroupException {
        Set<PersistentGroup> groups = new HashSet<>();
        for (Group group : children) {
            groups.add(group.group());
        }
        return UnionGroup.getInstance(groups);
    }
}
