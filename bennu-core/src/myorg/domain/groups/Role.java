/*
 * @(#)Role.java
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

package myorg.domain.groups;

import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;
import pt.ist.fenixWebFramework.services.Service;

public class Role extends Role_Base {

    private transient IRoleEnum roleType;

    public Role(IRoleEnum roleType) {
	super();
	this.roleType = roleType;
	if (find(roleType) != null) {
	    throw new DomainException("role.already.exists", getRepresentationName(roleType));
	}
	setGroupName(getRepresentationName(roleType));
	setSystemGroupMyOrg(MyOrg.getInstance());
    }

    @Service
    public static Role createRole(IRoleEnum roleType) {
	final Role role = find(roleType);
	return role == null ? new Role(roleType) : role;
    }

    protected static Role find(final IRoleEnum roleType) {
	for (final PersistentGroup group : MyOrg.getInstance().getSystemGroupsSet()) {
	    if (group instanceof Role) {
		final Role role = (myorg.domain.groups.Role) group;
		if (role.isRole(roleType)) {
		    return role;
		}
	    }
	}
	return null;
    }


    public static Role getRole(final IRoleEnum roleType) {
	final Role role = find(roleType);
	return role == null ? createRole(roleType) : role;
    }

    public boolean isRole(IRoleEnum roleType) {
	final String groupName = getGroupName();
	return roleType != null && groupName != null && groupName.equals(getRepresentationName(roleType));
    }

    private String getRepresentationName(IRoleEnum roleType) {
	return roleType.getClass().getName() + "." + roleType.getRoleName();
    }

    @Override
    public String getName() {
	if (this.roleType == null) {
	    try {
		String groupName = this.getGroupName();
		final int lastDot = groupName.lastIndexOf('.');
		final String className = groupName.substring(0, lastDot);
		final String enumName = groupName.substring(lastDot + 1);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Class<Enum> enumClass = (Class<Enum>) Class.forName(className);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Enum valueOf = Enum.valueOf(enumClass, enumName);
		this.roleType = (IRoleEnum) valueOf;
	    } catch (ClassNotFoundException e) {
		throw new Error(e);
	    }
	}

	return roleType.getLocalizedName();
    }

}
