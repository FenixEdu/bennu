/*
 * @(#)VirtualHost.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.domain;

import java.util.Set;

import myorg.domain.contents.Node;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class VirtualHost extends VirtualHost_Base {

    private static final ThreadLocal<VirtualHost> threadVirtualHost = new ThreadLocal<VirtualHost>();

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
	    if (serverName.equals(virtualHost.getHostname())) {
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
	setApplicationTitle(new MultiLanguageString("MyOrg Application Title"));
	setApplicationSubTitle(new MultiLanguageString("MyOrg Application SubTitle"));
	setApplicationCopyright(new MultiLanguageString("My Organization Name"));
	setGoogleSearchEnabled(Boolean.TRUE);
	setLanguageSelectionEnabled(Boolean.TRUE);
    }

    public VirtualHost(final VirtualHostBean virtualHostBean) {
	setMyOrg(MyOrg.getInstance());
	setHostname(virtualHostBean.getHostname());
	setApplicationTitle(virtualHostBean.getApplicationTitle());
	setApplicationSubTitle(virtualHostBean.getApplicationSubTitle());
	setApplicationCopyright(virtualHostBean.getApplicationCopyright());
	setGoogleSearchEnabled(virtualHostBean.getGoogleSearchEnabled());
	setLanguageSelectionEnabled(virtualHostBean.getLanguageSelectionEnabled());
    }

    @Service
    public static VirtualHost createVirtualHost(final VirtualHostBean virtualHostBean) {
	return new VirtualHost(virtualHostBean);
    }

    @Override
    public Theme getTheme() {
	Theme theme = super.getTheme();
	return theme != null ? theme : setAndReturnDefaultTheme();
    }

    @Service
    private Theme setAndReturnDefaultTheme() {
	Theme theme = getMyOrg().getThemes().get(0);
	setTheme(theme);
	return theme;
    }

    @Service
    public void deleteService() {
	delete();
    }

    public void delete() {
	if (MyOrg.getInstance().getVirtualHostsSet().size() > 1) {
	    for (final Node node : getTopLevelNodesSet()) {
		node.delete();
	    }
	    removeTheme();
	    removeMyOrg();
	    Transaction.deleteObject(this);
	}
    }

}
