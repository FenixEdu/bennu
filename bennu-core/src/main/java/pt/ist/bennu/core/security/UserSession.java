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
package pt.ist.bennu.core.security;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.BennuGroup;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

public class UserSession implements Serializable, Principal {
    private static final long serialVersionUID = -16953310282144136L;

    private final String userExternalId;

    private final String privateConstantForDigestCalculation;

    private final DateTime userViewCreationDateTime = new DateTime();

    private final DateTime lastLogoutDateTime;

    private final Set<String> groupExpressions = new HashSet<>();

    private transient Set<BennuGroup> groups = null;

    UserSession(final User user) {
        userExternalId = user.getExternalId();

        SecureRandom random = null;

        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Error("No secure algorithm available.");
        }

        random.setSeed(System.currentTimeMillis());

        privateConstantForDigestCalculation = user.getUsername() + user.getPassword() + random.nextLong();

        lastLogoutDateTime = user.getLastLogoutDateTime();

        for (BennuGroup group : user.accessibleGroups()) {
            groupExpressions.add(group.expression());
        }
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
        return userExternalId == null ? null : (User) AbstractDomainObject.fromExternalId(userExternalId);
    }

    public String getUsername() {
        return getUser() != null ? getUser().getUsername() : null;
    }

    public String getPrivateConstantForDigestCalculation() {
        return privateConstantForDigestCalculation;
    }

    public DateTime getUserCreationDateTime() {
        return userViewCreationDateTime;
    }

    public DateTime getLastLogoutDateTime() {
        return lastLogoutDateTime;
    }

    public Set<BennuGroup> accessibleGroups() {
        if (groups == null) {
            groups = new HashSet<>();
            for (String expression : groupExpressions) {
                groups.add(BennuGroup.parse(expression));
            }
        }
        return groups;
    }

    @Override
    public String getName() {
        return getUser().getPresentationName();
    }
}