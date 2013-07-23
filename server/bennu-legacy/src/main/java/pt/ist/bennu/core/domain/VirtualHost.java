/* 
 * @(#)VirtualHost.java 
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

import java.util.Set;

import pt.ist.bennu.portal.domain.PortalConfiguration;
import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author João Antunes
 * @author João Neves
 * @author Pedro Santos
 * @author Sérgio Silva
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public class VirtualHost extends VirtualHost_Base {

    private static final ThreadLocal<VirtualHost> threadVirtualHost = new ThreadLocal<>();

    public static VirtualHost getVirtualHostForThread() {
        return threadVirtualHost.get();
    }

    public static void setVirtualHostForThread(final VirtualHost virtualHost) {
        threadVirtualHost.set(virtualHost);
    }

    public static void releaseVirtualHostFromThread() {
        threadVirtualHost.remove();
    }

    public static VirtualHost setVirtualHostForThread(final String serverName) {
        final MyOrg myOrg = MyOrg.getInstance();
        final Set<VirtualHost> virtualHosts = myOrg.getVirtualHostsSet();
        for (final VirtualHost virtualHost : virtualHosts) {
            if (virtualHost.getHostname().startsWith(serverName)) {
                setVirtualHostForThread(virtualHost);
                return virtualHost;
            }
        }
        final VirtualHost virtualHost = virtualHosts.iterator().next();
        setVirtualHostForThread(virtualHost);
        return virtualHost;
    }

    public VirtualHost(final MyOrg myOrg) {
        setMyOrg(myOrg);
        setHostname("localhost");
        setConfiguration(new PortalConfiguration());
    }

    @Atomic
    public void deleteService() {
        delete();
    }

    public void delete() {
        if (MyOrg.getInstance().getVirtualHostsSet().size() > 1) {
            setMyOrg(null);
            deleteDomainObject();
        }
    }

    @Override
    public void setHostname(final String hostname) {
        super.setHostname(hostname.toLowerCase());
    }
}