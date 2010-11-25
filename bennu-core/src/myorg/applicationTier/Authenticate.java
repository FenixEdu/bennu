/*
 * @(#)Authenticate.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu Web Application Infrastructure.
 *
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   Bennu is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Bennu. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.applicationTier;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import myorg.domain.MyOrg;
import myorg.domain.RoleType;
import myorg.domain.User;
import myorg.domain.VirtualHost;
import myorg.domain.groups.Role;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

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
	if (usernameStrings != null && !usernameStrings.isEmpty()) {
	    for (final String username : usernameStrings.split(",")) {
		usernames.add(username.trim());
	    }
	}
    }

    public static class UserView implements pt.ist.fenixWebFramework.security.User, Serializable {

	private static final long serialVersionUID = -16953310282144136L;

	private final String userExternalId;
	private static transient ThreadLocal<User> mockUser = new ThreadLocal<User>();

	private final String privateConstantForDigestCalculation;

	private final DateTime userViewCreationDateTime = new DateTime();

	private UserView(final User user) {
	    userExternalId = user == null ? null : user.getExternalId();

	    SecureRandom random = null;

	    try {
		random = SecureRandom.getInstance("SHA1PRNG");
	    } catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
		throw new Error("No secure algorithm available.");
	    }

	    random.setSeed(System.currentTimeMillis());

	    privateConstantForDigestCalculation = user.getUsername() + user.getPassword() + random.nextLong();
	}

	private UserView(final String username) {
	    this(findByUsername(username));
	}

	public void mockUser(final User user) {
	    mockUser.set(user);
	}

	public void unmockUser() {
	    mockUser.set(null);
	}

	private static User findByUsername(final String username) {
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
	    return mockUser.get() == null ? getUser().getUsername() : mockUser.get().getUsername();
	}

	public boolean hasRole(final String roleAsString) {
	    final User user = mockUser.get() == null ? getUser() : mockUser.get();
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
	    return mockUser.get() == null ? readUser() : mockUser.get();
	}

	private User readUser() {
	    return userExternalId == null ? null : (User) AbstractDomainObject.fromExternalId(userExternalId);
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
	authenticate(userView);
	return userView;
    }

    @Service
    public static UserView authenticate(final User user) {
	final UserView userView = new UserView(user);
	authenticate(userView);
	return userView;
    }

    private static void authenticate(final UserView userView) {
	pt.ist.fenixWebFramework.security.UserView.setUser(userView);

	final User user = userView.getUser();
	for (final Entry<RoleType, Set<String>> entry : roleUsernamesMap.entrySet()) {
	    if (entry.getValue().contains(userView.getUsername())) {
		addRoleType(user, entry.getKey());
	    }
	}

	final Role role = Role.getRole(RoleType.MANAGER);
	if (role.getUsersCount() == 0) {
	    user.addPeopleGroups(role);
	}
    }

    protected static void addRoleType(final User user, final RoleType roleType) {
	if (!user.hasRoleType(roleType)) {
	    user.addPeopleGroups(Role.getRole(roleType));
	}
    }

}
