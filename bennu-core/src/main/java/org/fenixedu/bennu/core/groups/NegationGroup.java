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
package org.fenixedu.bennu.core.groups;

import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentNegationGroup;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

/**
 * Inverse group of another group.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
final class NegationGroup extends Group {
    private static final long serialVersionUID = 2689363908571859594L;

    private final Group negated;

    NegationGroup(Group negated) {
        super();
        this.negated = negated;
    }

    @Override
    public String getPresentationName() {
        return BundleUtil.getString("resources.BennuResources", "label.bennu.group.negation", negated.getPresentationName());
    }

    @Override
    public String getExpression() {
        return "! " + negated.getExpression();
    }

    @Override
    public PersistentGroup toPersistentGroup() {
        return PersistentNegationGroup.getInstance(negated.toPersistentGroup());
    }

    @Override
    public Set<User> getMembers() {
        Set<User> users = new HashSet<>(Bennu.getInstance().getUserSet());
        users.removeAll(negated.getMembers());
        return users;
    }

    @Override
    public boolean isMember(User user) {
        return !negated.isMember(user);
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        Set<User> users = new HashSet<>(Bennu.getInstance().getUserSet());
        users.removeAll(negated.getMembers(when));
        return users;
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return !negated.isMember(user, when);
    }

    @Override
    public Group not() {
        return negated;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof NegationGroup) {
            return negated.equals(((NegationGroup) object).negated);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return -negated.hashCode();
    }
}
