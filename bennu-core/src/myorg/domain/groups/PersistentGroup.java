/*
 * @(#)PersistentGroup.java
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

package myorg.domain.groups;

import java.util.Set;

import myorg.domain.MyOrg;
import myorg.domain.User;

public abstract class PersistentGroup extends PersistentGroup_Base {
    
    public PersistentGroup() {
        super();
        setMyOrg(MyOrg.getInstance());
        setOjbConcreteClass(getClass().getName());
    }

    public boolean isMember(final User user) {
	return false;
    }

    public abstract String getName();

    public abstract Set<User> getMembers();

    protected static PersistentGroup getInstance(final Class clazz) {
	for (final PersistentGroup persistentGroup : MyOrg.getInstance().getPersistentGroupsSet()) {
	    if (persistentGroup.getClass().isAssignableFrom(clazz)) {
		return persistentGroup;
	    }
	}
	return null;
    }

    protected static PersistentGroup getSystemGroup(final Class clazz) {
	for (final PersistentGroup persistentGroup : MyOrg.getInstance().getSystemGroupsSet()) {
	    if (persistentGroup.getClass().isAssignableFrom(clazz)) {
		return persistentGroup;
	    }
	}
	return null;
    }

}
