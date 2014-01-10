/*
 * NobodyGroup.java
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

import java.util.Collections;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

import com.google.common.base.Supplier;

/**
 * Group that always returns false.
 * 
 * @see Group
 */
public final class NobodyGroup extends NobodyGroup_Base {
    protected NobodyGroup() {
        super();
    }

    @Override
    public String getPresentationName() {
        return BundleUtil.getString("resources.BennuResources", "label.bennu.group.nobody");
    }

    @Override
    public String expression() {
        return "nobody";
    }

    @Override
    public Set<User> getMembers() {
        return Collections.emptySet();
    }

    @Override
    public boolean isMember(final User user) {
        return false;
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

    @Override
    public Group and(Group group) {
        return this;
    }

    @Override
    public Group or(Group group) {
        return group;
    }

    @Override
    public Group minus(Group group) {
        return this;
    }

    @Override
    public Group not() {
        return AnyoneGroup.getInstance();
    }

    @Override
    protected boolean isGarbageCollectable() {
        // Singleton group, no point in delete
        return false;
    }

    /**
     * Get or create singleton instance of {@link NobodyGroup}
     * 
     * @return singleton {@link NobodyGroup} instance
     */
    public static NobodyGroup getInstance() {
        return select(NobodyGroup.class, new Supplier<NobodyGroup>() {
            @Override
            public NobodyGroup get() {
                return new NobodyGroup();
            }
        });
    }
}
