/* 
* @(#)IntersectionGroup.java 
* 
* Copyright 2010 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import myorg.domain.User;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.services.Service;

/**
 * 
 * @author  Susana Fernandes
 * 
*/
public class IntersectionGroup extends IntersectionGroup_Base {

    protected IntersectionGroup() {
    }

    public IntersectionGroup(final PersistentGroup... persistentGroups) {
	super();
	for (final PersistentGroup persistentGroup : persistentGroups) {
	    addPersistentGroups(persistentGroup);
	}
    }

    @Override
    public Set<User> getMembers() {
	final Set<User> users = new HashSet<User>();
	if (hasAnyPersistentGroups()) {
	    users.addAll(getPersistentGroupsSet().iterator().next().getMembers());
	    for (final PersistentGroup persistentGroup : getPersistentGroupsSet()) {
		users.retainAll(persistentGroup.getMembers());
	    }
	}
	return users;
    }

    @Override
    public String getName() {
	List<String> names = new ArrayList<String>();
	for (PersistentGroup group : getPersistentGroups()) {
	    names.add("(" + group.getName() + ")");
	}
	return "Intersection of: ".concat(StringUtils.join(names, " AND "));
    }

    @Override
    public boolean isMember(final User user) {
	if (getPersistentGroupsCount() == 0) {
	    return false;
	}
	for (final PersistentGroup persistentGroup : getPersistentGroupsSet()) {
	    if (!persistentGroup.isMember(user)) {
		return false;
	    }
	}
	return true;
    }

    @Service
    public static IntersectionGroup createIntersectionGroup(final PersistentGroup... persistentGroups) {
	return new IntersectionGroup(persistentGroups);
    }

}
