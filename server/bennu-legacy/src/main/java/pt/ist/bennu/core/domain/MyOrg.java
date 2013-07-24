/* 
* @(#)MyOrg.java 
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.groups.legacy.PeopleUserLog;
import pt.ist.bennu.core.domain.groups.legacy.PersistentGroup;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;

/**
 * 
 * @author Pedro Santos
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public class MyOrg extends MyOrg_Base {

    private static final Logger logger = LoggerFactory.getLogger(MyOrg.class.getName());

    public static MyOrg getInstance() {
        final DomainRoot domainRoot = FenixFramework.getDomainRoot();
        if (domainRoot.getMyOrg() == null) {
            initMyOrg();
        }
        return domainRoot.getMyOrg();
    }

    @Atomic(mode = TxMode.WRITE)
    private static void initMyOrg() {
        new MyOrg();
    }

    private MyOrg() {
        super();
        checkIfIsSingleton();
        FenixFramework.getDomainRoot().setMyOrg(this);
        new VirtualHost(this);
        logger.info("Created new Instance of MyOrg: {}", this);
    }

    private void checkIfIsSingleton() {
        if (FenixFramework.getDomainRoot().getMyOrg() != null && FenixFramework.getDomainRoot().getMyOrg() != this) {
            throw new Error("There can only be one! (instance of MyOrg)");
        }
    }

    @Deprecated
    public java.util.Set<PersistentGroup> getPersistentGroups() {
        return getPersistentGroupsSet();
    }

    @Deprecated
    public java.util.Set<VirtualHost> getVirtualHosts() {
        return getVirtualHostsSet();
    }

    @Deprecated
    public java.util.Set<PeopleUserLog> getPeopleUserLog() {
        return getPeopleUserLogSet();
    }

    @Deprecated
    public java.util.Set<PersistentGroup> getSystemGroups() {
        return getSystemGroupsSet();
    }

}