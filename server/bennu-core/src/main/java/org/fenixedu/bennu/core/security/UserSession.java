/*
 * UserSession.java
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
package org.fenixedu.bennu.core.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.Group;
import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;

public class UserSession implements Serializable, Principal {
    private static final long serialVersionUID = -16953310282144136L;

    private final String userExternalId;

    private final DateTime userViewCreationDateTime = new DateTime();

    private Set<String> groupExpressions = null;

    private transient Set<Group> groups = null;

    UserSession(final User user) {
        userExternalId = user.getExternalId();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UserSession && userExternalId.equals(((UserSession) obj).userExternalId);
    }

    @Override
    public int hashCode() {
        return userExternalId.hashCode();
    }

    public User getUser() {
        return userExternalId == null ? null : (User) FenixFramework.getDomainObject(userExternalId);
    }

    public boolean isSessionStillValid() {
        return !getUser().isBeforeLastLogoutTime(userViewCreationDateTime);
    }

    public Set<Group> accessibleGroups() {
        if (groups == null) {
            groups = new HashSet<>();
            if (groupExpressions == null) {
                groupExpressions = new HashSet<>();
                for (Group group : getUser().accessibleGroups()) {
                    groupExpressions.add(group.expression());
                }
            }
            for (String expression : groupExpressions) {
                groups.add(Group.parse(expression));
            }
        }
        return groups;
    }

    @Override
    public String getName() {
        return getUser().getPresentationName();
    }
}