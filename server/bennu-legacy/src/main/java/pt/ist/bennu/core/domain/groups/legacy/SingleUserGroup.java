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
package pt.ist.bennu.core.domain.groups.legacy;

import java.util.Collections;
import java.util.Set;

import pt.ist.bennu.core.domain.BennuLegacyDomainException;
import pt.ist.bennu.core.domain.User;
import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author Luis Cruz
 * 
 */
@Deprecated
public class SingleUserGroup extends SingleUserGroup_Base {

    private SingleUserGroup(final User user) {
        super();
        if (user.getSingleUserGroup() != null) {
            throw BennuLegacyDomainException.userAlreadyHasSingleUserGroup();
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

    @Atomic
    public static SingleUserGroup getOrCreateGroup(final User user) {
        return user == null ? null : user.getSingleUserGroup() != null ? user.getSingleUserGroup() : new SingleUserGroup(user);
    }

}
