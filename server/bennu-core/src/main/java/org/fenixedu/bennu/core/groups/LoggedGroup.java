/*
 * LoggedGroup.java
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
 * Authenticated users group.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
@GroupOperator("logged")
public final class LoggedGroup extends GroupStrategy {
    private static final long serialVersionUID = -933443447959188208L;

    private static final LoggedGroup INSTANCE = new LoggedGroup();

    private LoggedGroup() {
        super();
    }

    public static LoggedGroup get() {
        return INSTANCE;
    }

    @Override
    public String getPresentationName() {
        return BundleUtil.getString("resources.BennuResources", "label.bennu.group.logged");
    }

    @Override
    public Set<User> getMembers() {
        return Collections.unmodifiableSet(Bennu.getInstance().getUserSet());
    }

    @Override
    public boolean isMember(final User user) {
        return user != null;
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
        if (group instanceof AnonymousGroup) {
            return NobodyGroup.get();
        }
        if (!group.isMember(null)) {
            return group;
        }
        return super.and(group);
    }

    @Override
    public Group or(Group group) {
        if (group instanceof AnonymousGroup) {
            return AnyoneGroup.get();
        }
        if (!group.isMember(null)) {
            return this;
        }
        return super.or(group);
    }

    @Override
    public Group minus(Group group) {
        if (group instanceof AnonymousGroup) {
            return this;
        }
        return super.minus(group);
    }

    @Override
    public Group not() {
        return AnonymousGroup.get();
    }
}
