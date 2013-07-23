/* 
* @(#)NamedGroup.java 
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
package pt.ist.bennu.core.domain.groups.legacy;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.User;

/**
 * 
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
@Deprecated
public class NamedGroup extends NamedGroup_Base {

    public NamedGroup() {
        super();
        final MyOrg myOrg = getMyOrg();
        setSystemGroupMyOrg(myOrg);
    }

    public NamedGroup(String groupName) {
        this();
        setGroupName(groupName);
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
        return getGroupName();
    }
}
