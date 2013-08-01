/* 
* @(#)RoleType.java 
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
package pt.ist.bennu.core.domain;

import pt.ist.bennu.core.domain.groups.legacy.IRoleEnum;
import pt.ist.bennu.core.util.legacy.BundleUtil;

/**
 * 
 * @author João Marques
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public enum RoleType implements IRoleEnum {

    MANAGER, USER_MANAGER;

    @Override
    public String getPresentationName() {
        return BundleUtil.getStringFromResourceBundle("resources/MyorgResources", "label.persistent.group.roleType.name."
                + name());
    }

    public String getName() {
        return name();
    }

    @Override
    public String getRoleName() {
        return this.getName();
    }

}