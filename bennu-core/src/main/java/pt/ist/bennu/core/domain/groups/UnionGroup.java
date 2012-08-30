/* 
 * @(#)UnionGroup.java 
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
package pt.ist.bennu.core.domain.groups;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.User;
import pt.ist.fenixWebFramework.services.Service;

/**
 * 
 * @author João Antunes
 * @author Sérgio Silva
 * @author Luis Cruz
 * 
 */
public class UnionGroup extends UnionGroup_Base {

    public UnionGroup() {
	super();
    }

    public UnionGroup(final PersistentGroup... persistentGroups) {
	super();
	for (final PersistentGroup persistentGroup : persistentGroups) {
	    addPersistentGroups(persistentGroup);
	}
    }

    public UnionGroup(final Collection<PersistentGroup> persistentGroups) {
	super();
	for (final PersistentGroup persistentGroup : persistentGroups) {
	    addPersistentGroups(persistentGroup);
	}
    }

    @Override
    public Set<User> getMembers() {
	final Set<User> users = new HashSet<User>();
	for (final PersistentGroup persistentGroup : getPersistentGroupsSet()) {
	    users.addAll(persistentGroup.getMembers());
	}
	return users;
    }

    @Override
    public String getName() {
	String groupName = "Union of: ";
	for (Iterator persistentGroupIterator = getPersistentGroupsIterator(); persistentGroupIterator.hasNext();) {
	    PersistentGroup group = (PersistentGroup) persistentGroupIterator.next();
	    groupName = groupName.concat(group.getName());
	    if (persistentGroupIterator.hasNext())
		groupName = groupName.concat(" AND ");
	}
	return groupName;
    }

    @Override
    public boolean isMember(final User user) {
	for (final PersistentGroup persistentGroup : getPersistentGroupsSet()) {
	    if (persistentGroup.isMember(user)) {
		return true;
	    }
	}
	return false;
    }

    @Service
    public static UnionGroup createUnionGroup(final PersistentGroup... persistentGroups) {
	return new UnionGroup(persistentGroups);
    }

    public static UnionGroup getOrCreateUnionGroup(final PersistentGroup... persistentGroups) {
	for (PersistentGroup group : MyOrg.getInstance().getPersistentGroups()) {
	    if (group instanceof UnionGroup) {
		UnionGroup unionGroup = (UnionGroup) group;
		if (CollectionUtils.isEqualCollection(unionGroup.getPersistentGroups(), Arrays.asList(persistentGroups))) {
		    return unionGroup;
		}
	    }
	}
	return UnionGroup.createUnionGroup(persistentGroups);
    }

}
