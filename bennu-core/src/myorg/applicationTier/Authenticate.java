/*
 * @(#)Authenticate.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.applicationTier;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import myorg.domain.MyOrg;
import myorg.domain.RoleType;
import myorg.domain.User;
import myorg.domain.VirtualHost;
import myorg.domain.groups.Role;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.util.DomainReference;

public class Authenticate implements Serializable {

    private static final long serialVersionUID = -8446811315540707574L;

    private static final Map<RoleType, Set<String>> roleUsernamesMap = new HashMap<RoleType, Set<String>>();

    private static Set<String> getUsernames(final RoleType roleType) {
	Set<String> usernames = roleUsernamesMap.get(roleType);
	if (usernames == null) {
	    usernames = new HashSet<String>();
	    roleUsernamesMap.put(roleType, usernames);
	}
	return usernames;
    }

    public static synchronized void initRole(final RoleType roleType, final String usernameStrings) {
	final Set<String> usernames = getUsernames(roleType);
	for (final String username : usernameStrings.split(",")) {
	    usernames.add(username.trim());
	}
    }

    public static class UserView implements pt.ist.fenixWebFramework.security.User, Serializable {

	private static final long serialVersionUID = -3306363183634102130L;

	private final DomainReference<User> userReference;
	private DomainReference<User> mockReference;

	private final String privateConstantForDigestCalculation;

	private final DateTime userViewCreationDateTime = new DateTime();

	private UserView(final String username) {
	    final User user = findByUsername(username);
	    userReference = new DomainReference<User>(user);
	    mockReference = null;

	    SecureRandom random = null;

	    try {
		random = SecureRandom.getInstance("SHA1PRNG");
	    } catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
		throw new Error("No secure algorithm available.");
	    }

	    random.setSeed(System.currentTimeMillis());

	    privateConstantForDigestCalculation = user.getUsername() + user.getPassword() + random.nextLong();
	    System.out.println("[Key] User: " + user.getUsername() + " Key: " + privateConstantForDigestCalculation);
	}

	public void mockUser(final String username) {
	    final User user = User.findByUsername(username);
	    mockReference = new DomainReference<User>(user);
	}

	public void unmockUser() {
	    mockReference = null;
	}

	private User findByUsername(final String username) {
	    final User user = User.findByUsername(username);
	    if (user == null) {
		final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
		if ((virtualHost != null && virtualHost.isCasEnabled()) || MyOrg.getInstance().getUserCount() == 0) {
		    return new User(username);
		}
		return new User(username);
		// throw new Error("authentication.exception");
	    }
	    return user;
	}

	public String getUsername() {
	    return mockReference == null ? getUser().getUsername() : mockReference.getObject().getUsername();
	}

	public boolean hasRole(final String roleAsString) {
	    final User user = mockReference == null ? getUser() : mockReference.getObject();
	    return user != null && user.hasRoleType(roleAsString);
	}

	@Override
	public boolean equals(Object obj) {
	    return obj instanceof UserView && getUsername().equals(((UserView) obj).getUsername());
	}

	@Override
	public int hashCode() {
	    return getUsername().hashCode();
	}

	public User getUser() {
	    return mockReference == null ? userReference.getObject() : mockReference.getObject();
	}

	public String getPrivateConstantForDigestCalculation() {
	    return privateConstantForDigestCalculation;
	}

	public static UserView getCurrentUserView() {
	    final UserView userView = (UserView) pt.ist.fenixWebFramework.security.UserView.getUser();
	    return userView;
	}

	public static User getCurrentUser() {
	    final UserView userView = getCurrentUserView();
	    return userView == null ? null : userView.getUser();
	}

	@Override
	public DateTime getLastLogoutDateTime() {
	    return getUser().getLastLogoutDateTime();
	}

	@Override
	public DateTime getUserCreationDateTime() {
	    return userViewCreationDateTime;
	}
    }

    @Service
    public static UserView authenticate(final String username, final String password) {
	final UserView userView = new UserView(username);
	pt.ist.fenixWebFramework.security.UserView.setUser(userView);

	final User user = userView.getUser();
	for (final Entry<RoleType, Set<String>> entry : roleUsernamesMap.entrySet()) {
	    if (entry.getValue().contains(username)) {
		addRoleType(user, entry.getKey());
	    }
	}

	final Role role = Role.getRole(RoleType.MANAGER);
	if (role.getUsersCount() == 0) {
	    user.addPeopleGroups(role);
	}

	return userView;
    }

    protected static void addRoleType(final User user, final RoleType roleType) {
	if (!user.hasRoleType(roleType)) {
	    user.addPeopleGroups(Role.getRole(roleType));
	}
    }

}
