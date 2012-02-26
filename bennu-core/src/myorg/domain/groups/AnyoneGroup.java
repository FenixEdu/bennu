/* 
* @(#)AnyoneGroup.java 
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
package myorg.domain.groups;

import java.util.Set;

import myorg.domain.MyOrg;
import myorg.domain.User;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;

/**
 * 
 * @author  Luis Cruz
 * 
*/
public class AnyoneGroup extends AnyoneGroup_Base {

    private AnyoneGroup() {
        super();
        final MyOrg myOrg = getMyOrg();
        setSystemGroupMyOrg(myOrg);
    }

    @Override
    public boolean isMember(final User user) {
	return true;
    }

    @Service
    public static AnyoneGroup getInstance() {
	final AnyoneGroup anyoneGroup = (AnyoneGroup) PersistentGroup.getSystemGroup(AnyoneGroup.class);
	return anyoneGroup == null ? new AnyoneGroup() : anyoneGroup;
    }

    @Override
    public String getName() {
	return BundleUtil.getStringFromResourceBundle("resources/MyorgResources", "label.persistent.group.anyoneGroup.name");
    }

    @Override
    public Set<User> getMembers() {
	return null;
    }

}
