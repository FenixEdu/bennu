/*
 * AnyoneGroup.java
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

import java.util.Collections;
import java.util.Set;

import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

/**
 * Group that always returns <code>true</code> on membership tests.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
@GroupOperator("anyone")
public final class AnyoneGroup extends GroupStrategy {
    private static final long serialVersionUID = -2799124523940818893L;

    private static final AnyoneGroup INSTANCE = new AnyoneGroup();

    private AnyoneGroup() {
        super();
    }

    public static AnyoneGroup get() {
        return INSTANCE;
    }

    @Override
    public String getPresentationName() {
        return BundleUtil.getString("resources.BennuResources", "label.bennu.group.anyone");
    }

    @Override
    public Set<User> getMembers() {
        return Collections.unmodifiableSet(Bennu.getInstance().getUserSet());
    }

    @Override
    public boolean isMember(final User user) {
        return true;
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
        return group;
    }

    @Override
    public Group or(Group group) {
        return this;
    }

    @Override
    public Group minus(Group group) {
        if (group instanceof AnonymousGroup) {
            return LoggedGroup.get();
        }
        if (group instanceof LoggedGroup) {
            return AnonymousGroup.get();
        }
        return super.minus(group);
    }

    @Override
    public Group not() {
        return NobodyGroup.get();
    }
}
