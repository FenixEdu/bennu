/* 
* @(#)People.java 
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

import pt.ist.bennu.core.domain.User;
import pt.ist.fenixWebFramework.services.Service;
import dml.runtime.RelationAdapter;

/**
 * 
 * @author Luis Cruz
 * 
 */
public abstract class People extends People_Base {

    public static class PeopleUserListener extends RelationAdapter<User, People> {

        @Override
        public void afterAdd(final User user, final People people) {
            super.afterAdd(user, people);
            if (user == null || people == null || !user.hasPeopleGroups(people)) {
                new PeopleUserLog("Add", user == null ? "" : user.getUsername(), people == null ? "" : people.getName());
            }
        }

        @Override
        public void afterRemove(final User user, final People people) {
            new PeopleUserLog("Remove", user == null ? "" : user.getUsername(), people == null ? "" : people.getName());
            super.afterRemove(user, people);
        }

    }

    static {
        User.UserPeople.addListener(new PeopleUserListener());
    }

    public People() {
        super();
    }

    @Override
    public Set<User> getMembers() {
        return getUsersSet();
    }

    @Override
    public void delete() {
        getUsersSet().clear();
        super.delete();
    }

    @Service
    public void removeMember(final User user) {
        removeUsers(user);
    }

    @Service
    public void addMember(final User user) {
        addUsers(user);
    }

}
