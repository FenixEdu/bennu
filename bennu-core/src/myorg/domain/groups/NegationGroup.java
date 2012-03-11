/* 
* @(#)NegationGroup.java 
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

import java.util.Set;

import myorg.domain.User;
import myorg.domain.exceptions.DomainException;
import pt.ist.fenixWebFramework.services.Service;

/**
 * 
 * @author  João Antunes
 * @author  Susana Fernandes
 * 
*/
public class NegationGroup extends NegationGroup_Base {

    public NegationGroup(PersistentGroup persistentGroup) {
	super();
	setPersistentGroup(persistentGroup);
    }

    @Override
    public Set<User> getMembers() {
	throw new DomainException();
    }

    @Override
    public String getName() {
	return ("NOT " + getPersistentGroup().getName());
    }

    @Override
    public boolean isMember(User user) {
	return !getPersistentGroup().isMember(user);
    }

    @Service
    public static NegationGroup createNegationGroup(final PersistentGroup persistentGroup) {
	return new NegationGroup(persistentGroup);
    }
}
