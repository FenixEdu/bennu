/* 
* @(#)SingleUserGroup.java 
* 
* Copyright 2011 Instituto Superior Tecnico 
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

import java.util.Collections;
import java.util.Set;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.fenixWebFramework.services.Service;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class SingleUserGroup extends SingleUserGroup_Base {

    private SingleUserGroup(final User user) {
        super();
        if (user.hasSingleUserGroup()) {
            throw new DomainException("user.already.has.single.user.group");
        }
        setUser(user);
    }

    @Override
    public Set<User> getMembers() {
        return Collections.singleton(getUser());
    }

    @Override
    public String getName() {
        return getUser().getPresentationName();
    }

    @Override
    public boolean isMember(final User user) {
        return user == getUser();
    }

    @Service
    public static SingleUserGroup getOrCreateGroup(final User user) {
        return user == null ? null : user.hasSingleUserGroup() ? user.getSingleUserGroup() : new SingleUserGroup(user);
    }

}
