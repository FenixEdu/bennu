/*
 * VirtualHost.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.domain;

import java.util.Locale;
import java.util.Set;

import pt.ist.bennu.core.domain.groups.AnonymousGroup;
import pt.ist.bennu.core.domain.groups.AnyoneGroup;
import pt.ist.bennu.core.domain.groups.BennuGroup;
import pt.ist.bennu.core.domain.groups.LoggedGroup;
import pt.ist.bennu.core.domain.groups.NobodyGroup;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.core.util.ConfigurationManager.CasConfig;
import pt.ist.bennu.core.util.LocaleArray;
import pt.ist.bennu.service.Service;

public class VirtualHost extends VirtualHost_Base {

    private static final ThreadLocal<VirtualHost> current = new ThreadLocal<>();

    public static VirtualHost getVirtualHostForThread() {
        return current.get();
    }

    public static void setVirtualHostForThread(final VirtualHost virtualHost) {
        current.set(virtualHost);
    }

    public static void releaseVirtualHostFromThread() {
        current.remove();
    }

    public static VirtualHost setVirtualHostForThread(final String serverName) {
        final Bennu bennu = Bennu.getInstance();
        final Set<VirtualHost> virtualHosts = bennu.getVirtualHostsSet();
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

    public VirtualHost(String hostname) {
        setBennu(Bennu.getInstance());
        setHostname(hostname);
        setVirtualHostForThread(this);
        setSupportedLanguages(new LocaleArray(Locale.forLanguageTag("en-UK"), Locale.forLanguageTag("pt-PT")));
        initializeGroups();
    }

    private void initializeGroups() {
        AnyoneGroup.getInstance();
        LoggedGroup.getInstance();
        AnonymousGroup.getInstance();
        NobodyGroup.getInstance();
    }

    @Service
    public Boolean delete() {
        if (Bennu.getInstance().getVirtualHostsSet().size() > 1) {
            removeBennu();
            deleteGroups();
            deleteDomainObject();
            return true;
        }
        return false;
    }

    private void deleteGroups() {
        for (BennuGroup group : getGroupsSet()) {
            group.removeHost();
        }
    }

    @Override
    public void setHostname(final String hostname) {
        super.setHostname(hostname.toLowerCase());
    }

    public boolean isCasEnabled() {
        CasConfig casConfig = ConfigurationManager.getCasConfig(getHostname());
        return casConfig != null && casConfig.isCasEnabled();
    }
}