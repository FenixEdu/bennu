/*
 * Anonymous.java
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

import java.util.stream.Stream;

import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

/**
 * Group for unauthenticated users.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
@GroupOperator("anonymous")
final class AnonymousGroup extends GroupStrategy {
    private static final long serialVersionUID = 719303318142232943L;

    AnonymousGroup() {
        super();
    }

    @Override
    public String getPresentationName() {
        return BundleUtil.getString("resources.BennuResources", "label.bennu.group.anonymous");
    }

    @Override
    public Stream<User> getMembers() {
        return Stream.empty();
    }

    @Override
    public boolean isMember(final User user) {
        return user == null;
    }

    @Override
    public Stream<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

    @Override
    public Group and(Group group) {
        if (group instanceof LoggedGroup) {
            return Group.nobody();
        }
        return super.and(group);
    }

    @Override
    public Group or(Group group) {
        if (group instanceof LoggedGroup) {
            return Group.anyone();
        }
        return super.or(group);
    }

    @Override
    public Group minus(Group group) {
        if (group instanceof LoggedGroup) {
            return this;
        }
        return super.minus(group);
    }

    @Override
    public Group not() {
        return Group.logged();
    }
}
