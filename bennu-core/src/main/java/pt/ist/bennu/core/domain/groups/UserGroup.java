/* 
* @(#)UserGroup.java 
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

import java.util.Set;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class UserGroup extends UserGroup_Base {

    private UserGroup() {
        super();
        final MyOrg myOrg = getMyOrg();
        setSystemGroupMyOrg(myOrg);
    }

    @Override
    public boolean isMember(final User user) {
        return user != null;
    }

    @Atomic
    public static UserGroup getInstance() {
        final UserGroup userGroup = (UserGroup) PersistentGroup.getSystemGroup(UserGroup.class);
        return userGroup == null ? new UserGroup() : userGroup;
    }

    @Override
    public String getName() {
        return BundleUtil.getStringFromResourceBundle("resources/MyorgResources", "label.persistent.group.userGroup.name");
    }

    @Override
    public Set<User> getMembers() {
        return getMyOrg().getUserSet();
    }

}
