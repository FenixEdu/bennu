/* 
* @(#)JavaUtil.java 
* 
* Copyright 2012 Instituto Superior Tecnico 
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
package pt.ist.bennu.core.util;

/**
 * 
 * @author  João Antunes
 * 
*/
public class JavaUtil {
    /**
     * 
     * @param object1
     * @param object2
     * @return true if both objects are equal, regardless if they are null or
     *         not.
     */
    public static boolean isObjectEqualTo(Object object1, Object object2) {
	return object1 == null ? object2 == null : object1.equals(object2);
    }

}
