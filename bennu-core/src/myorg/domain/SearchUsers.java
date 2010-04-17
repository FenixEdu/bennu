/*
 * @(#)SearchUsers.java
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

package myorg.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import myorg.domain.util.Search;

public class SearchUsers extends Search<User> {

    private String username;
    private User user;
    private RoleType roleType;

    protected class SearchResult extends SearchResultSet<User> {

	public SearchResult(final Collection<? extends User> c) {
	    super(c);
	}

	@Override
	protected boolean matchesSearchCriteria(final User user) {
	    return matchCriteria(username, user.getUsername()) && matchCriteria(roleType, user);
	}

	private boolean matchCriteria(final RoleType roleType, final User user) {
	    return roleType == null || user.hasRoleType(roleType);
	}

    }

    @Override
    public Set<User> search() {
	final User user = getUser();
	if (user != null) {
	    final Set<User> users = new HashSet<User>();
	    users.add(user);
	    return users;
	}
	final Set<User> users = username != null || roleType != null ? MyOrg.getInstance().getUserSet() : Collections.EMPTY_SET;
	return new SearchResult(users);
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public RoleType getRoleType() {
	return roleType;
    }

    public void setRoleType(RoleType roleType) {
	this.roleType = roleType;
    }

    public User getUser() {
	return user;
    }

    public void setUser(final User user) {
	this.user = user;
    }

}
