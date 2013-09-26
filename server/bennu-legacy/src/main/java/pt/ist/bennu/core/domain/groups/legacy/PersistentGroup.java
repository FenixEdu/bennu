/* 
* @(#)PersistentGroup.java 
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

import java.util.Comparator;
import java.util.Set;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.Presentable;
import pt.ist.bennu.core.domain.User;

/**
 * 
 * @author Sérgio Silva
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
@Deprecated
public abstract class PersistentGroup extends PersistentGroup_Base implements Presentable {

    public static final Comparator<PersistentGroup> COMPARATOR_BY_NAME = new Comparator<PersistentGroup>() {

        @Override
        public int compare(PersistentGroup r1, PersistentGroup r2) {
            final int c = r1.getName().compareTo(r2.getName());
            return c == 0 ? r1.getExternalId().compareTo(r2.getExternalId()) : c;
        }

    };

    public PersistentGroup() {
        super();
        setMyOrg(MyOrg.getInstance());
    }

    public abstract boolean isMember(final User user);

    public abstract String getName();

    public abstract Set<User> getMembers();

    protected static PersistentGroup getInstance(final Class clazz) {
        for (final PersistentGroup persistentGroup : MyOrg.getInstance().getPersistentGroupsSet()) {
            if (persistentGroup.getClass().isAssignableFrom(clazz)) {
                return persistentGroup;
            }
        }
        return null;
    }

    protected static PersistentGroup getSystemGroup(final Class clazz) {
        for (final PersistentGroup persistentGroup : MyOrg.getInstance().getSystemGroupsSet()) {
            if (persistentGroup.getClass().isAssignableFrom(clazz)) {
                return persistentGroup;
            }
        }
        return null;
    }

    public void delete() {
        setGroupOwner(null);
        setSystemGroupMyOrg(null);
        setMyOrg(null);
        deleteDomainObject();
    }

    @Override
    public String getPresentationName() {
        return getName();
    }

    @Deprecated
    public java.util.Set<NegationGroup> getNegationGroups() {
        return getNegationGroupsSet();
    }

    @Deprecated
    public java.util.Set<IntersectionGroup> getIntersectionGroups() {
        return getIntersectionGroupsSet();
    }

    @Deprecated
    public java.util.Set<UnionGroup> getUnionGroups() {
        return getUnionGroupsSet();
    }

}
