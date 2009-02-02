/*
 * @(#)Role.java
 *
 * Copyright 2009 Instituto Superior Tecnico, João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
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

package myorg.domain.groups;

import myorg.domain.MyOrg;
import myorg.domain.RoleType;
import myorg.domain.User;
import pt.ist.fenixWebFramework.services.Service;

public class Role extends Role_Base {

    public Role(final RoleType roleType) {
        super();
        final MyOrg myOrg = getMyOrg();
        setSystemGroupMyOrg(myOrg);
        setRoleType(roleType);
    }

    @Service
    public static Role createRole(final RoleType roleType) {
	final Role role = find(roleType);
	return role == null ? new Role(roleType) : role;
    }

    protected static Role find(final RoleType roleType) {
	for (final PersistentGroup group : MyOrg.getInstance().getSystemGroupsSet()) {
	    if (group instanceof Role) {
		final Role role = (myorg.domain.groups.Role) group;
		if (role.getRoleType() == roleType) {
		    return role;
		}
	    }
	}
	return null;
    }

    public static Role getRole(final RoleType roleType) {
	final Role role = find(roleType);
	return role == null ? createRole(roleType) : role;
    }

    @Override
    public boolean isMember(final User user) {
	if (user != null) {
	    for (final People people : user.getPeopleGroupsSet()) {
		if (people == this) {
		    return true;
		}
	    }
	}
	return false;
    }

    @Override
    public String getName() {
	return getRoleType().getPresentationName();
    }

}
