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
package pt.ist.bennu.core.domain.groups;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.bennu.service.Service;

import com.google.common.base.Predicate;

/**
 * Inverse group of another group.
 * 
 * @see Group
 */
public class NegationGroup extends NegationGroup_Base {
    protected NegationGroup(Group negated) {
        super();
        setNegated(negated);
    }

    @Override
    public String getPresentationName() {
        return BundleUtil.getString("resources.BennuResources", "label.bennu.group.negation", getNegated().getPresentationName());
    }

    @Override
    public String expression() {
        return "! " + getNegated().expression();
    }

    @Override
    public Set<User> getMembers() {
        Set<User> users = new HashSet<>(Bennu.getInstance().getUsersSet());
        users.removeAll(getNegated().getMembers());
        return users;
    }

    @Override
    public boolean isMember(User user) {
        return !getNegated().isMember(user);
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        Set<User> users = new HashSet<>(Bennu.getInstance().getUsersSet());
        users.removeAll(getNegated().getMembers(when));
        return users;
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return !getNegated().isMember(user, when);
    }

    @Override
    public Group not() {
        return getNegated();
    }

    /**
     * Get or create singleton instance of {@link NegationGroup}
     * 
     * @param group the group to inverse
     * @return singleton {@link NegationGroup} instance
     */
    @Service
    public static NegationGroup getInstance(final Group group) {
        NegationGroup negated = select(NegationGroup.class, new Predicate<NegationGroup>() {
            @Override
            public boolean apply(NegationGroup input) {
                return input.getNegated().equals(group);
            }
        });
        return negated != null ? negated : new NegationGroup(negated);
    }
}
