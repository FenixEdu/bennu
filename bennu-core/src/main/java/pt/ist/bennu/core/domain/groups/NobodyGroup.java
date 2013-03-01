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
package pt.ist.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.bennu.service.Service;

/**
 * Group that always returns false.
 * 
 * @see BennuGroup
 */
public class NobodyGroup extends NobodyGroup_Base {
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
    public BennuGroup and(BennuGroup group) {
        return this;
    }

    @Override
    public BennuGroup or(BennuGroup group) {
        return group;
    }

    @Override
    public BennuGroup minus(BennuGroup group) {
        return this;
    }

    @Override
    public BennuGroup not() {
        return AnyoneGroup.getInstance();
    }

    /**
     * Get or create singleton instance of {@link NobodyGroup}
     * 
     * @return singleton {@link NobodyGroup} instance
     */
    @Service
    public static NobodyGroup getInstance() {
        NobodyGroup group = select(NobodyGroup.class);
        return group == null ? new NobodyGroup() : group;
    }
}
