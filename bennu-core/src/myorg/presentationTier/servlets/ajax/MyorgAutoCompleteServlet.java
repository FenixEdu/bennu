/*
 * @(#)MyorgAutoCompleteServlet.java
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

package myorg.presentationTier.servlets.ajax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import myorg.presentationTier.renderers.autoCompleteProvider.AutoCompleteProvider;
import pt.ist.fenixWebFramework.security.User;
import pt.ist.fenixWebFramework.security.UserView;
import pt.ist.fenixWebFramework.servlets.ajax.AutoCompleteServlet;

public class MyorgAutoCompleteServlet extends AutoCompleteServlet {

    private static List<String> statsInfo = new ArrayList<String>();

    private static void registerString(String className, String value, String username) {
	String result = "[AutoCompleteStatsInfo]: " + className + ":" + value + ":" + username;
	synchronized (statsInfo) {
	    statsInfo.add(result);
	}
    }

    static {
	Runnable runnable = new Runnable() {

	    @Override
	    public void run() {
		while (true) {
		    try {
			Thread.sleep(30000);
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		    synchronized (statsInfo) {
			for (String info : statsInfo) {
			    System.out.println(info);
			}
			statsInfo.clear();
		    }
		}
	    }

	};

	Thread thread = new Thread(runnable);
	thread.start();
    }

    @Override
    protected Collection getSearchResult(Map<String, String> argsMap, String value, int maxCount) {
	AutoCompleteProvider provider = getProvider(argsMap.get("provider"));
	User user = UserView.getUser();
	MyorgAutoCompleteServlet.registerString(provider.getClass().getName(), value, user != null ? user.getUsername() : "-");
	return provider.getSearchResults(argsMap, value, maxCount);
    }

    private AutoCompleteProvider getProvider(String providerClass) {
	try {
	    Class provider = Class.forName(providerClass);
	    return (AutoCompleteProvider) provider.newInstance();
	} catch (Exception e) {
	    throw new RuntimeException("cannot find provider " + providerClass);
	}
    }

}
