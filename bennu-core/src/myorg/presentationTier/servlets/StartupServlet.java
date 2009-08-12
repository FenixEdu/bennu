/*
 * @(#)StartupServlet.java
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

package myorg.presentationTier.servlets;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import myorg._development.PropertiesManager;
import myorg.applicationTier.Authenticate;
import myorg.domain.MyOrg;
import myorg.domain.RoleType;
import myorg.domain.Theme;
import myorg.domain.Theme.ThemeType;
import myorg.domain.groups.AnyoneGroup;
import myorg.domain.groups.Role;
import myorg.domain.groups.UserGroup;
import myorg.domain.index.IndexListener;
import myorg.domain.scheduler.Scheduler;
import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter.ChecksumPredicate;
import pt.ist.fenixframework.pstm.TopLevelTransaction;

public class StartupServlet extends HttpServlet {

    private static final long serialVersionUID = -7035892286820898843L;

    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	final String domainmodelPath = getServletContext().getRealPath(getInitParameter("domainmodelPath"));
	final File dir = new File(domainmodelPath);
	final List<String> urls = new ArrayList<String>();
	for (final File file : dir.listFiles()) {
	    if (file.isFile() && file.getName().endsWith(".dml")) {
		try {
		    urls.add(file.getCanonicalPath());
		} catch (IOException e) {
		    e.printStackTrace();
		    throw new ServletException(e);
		}
	    }
	}
	Collections.sort(urls);
	final String[] paths = new String[urls.size()];
	for (int i = 0; i < urls.size(); i++) {
	    paths[i] = urls.get(i);
	}
	try {
	    FenixWebFramework.initialize(PropertiesManager.getFenixFrameworkConfig(paths));
	} catch (Throwable t) {
	    t.printStackTrace();
	    throw new Error(t);
	}

	TopLevelTransaction.addCommitListener(new IndexListener());

	try {
	    MyOrg.initModules();
	} catch (Throwable t) {
	    t.printStackTrace();
	    throw new Error(t);
	}

	final String managerUsernames = PropertiesManager.getProperty("manager.usernames");
	Authenticate.initRole(RoleType.MANAGER, managerUsernames);

	initializePersistentGroups();

	Scheduler.initialize();

	syncThemes();

	registerFilterCheckSumRules();

    }

    private void syncThemes() {
	File cssDir = new File(getServletContext().getRealPath("CSS"));
	File[] files = cssDir.listFiles(new FileFilter() {

	    @Override
	    public boolean accept(File file) {
		return file.isDirectory();
	    }
	});

	for (Theme theme : MyOrg.getInstance().getThemes()) {
	    if (!matchTheme(theme.getName(), files)) {
		Theme.deleteTheme(theme);
	    }
	}

	for (File directory : files) {
	    String themeName = directory.getName();
	    if (!Theme.isThemeAvailable(themeName)) {
		Properties themeProperties = loadThemePropeties(themeName);
		ThemeType type = ThemeType.valueOf(themeProperties.getProperty("theme.type"));
		String description = themeProperties.getProperty("theme.description");
		String screenshotFileName = themeProperties.getProperty("theme.screenshotFileName");
		Theme.createTheme(themeName, description, type, screenshotFileName);
	    }
	}

    }

    private Properties loadThemePropeties(String themeName) {
	Properties properties = null;
	try {
	    properties = PropertiesManager.loadPropertiesFromFile(getServletContext().getRealPath(
		    "CSS/" + themeName + "/theme.properties"));
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new Error(themeName + " could not read property file");
	}
	return properties;
    }

    private boolean matchTheme(String name, File[] files) {
	for (File file : files) {
	    if (file.getName().equals(name)) {
		return true;
	    }
	}
	return false;
    }

    private void initializePersistentGroups() {
	UserGroup.getInstance();
	AnyoneGroup.getInstance();
	for (final RoleType roleType : RoleType.values()) {
	    Role.getRole(roleType);
	}
    }

    private void registerFilterCheckSumRules() {

	RequestChecksumFilter.registerFilterRule(new ChecksumPredicate() {

	    public boolean shouldFilter(HttpServletRequest httpServletRequest) {
		return !httpServletRequest.getRequestURI().endsWith("/home.do")
			&& !httpServletRequest.getRequestURI().endsWith("/isAlive.do")
			&& !(httpServletRequest.getRequestURI().endsWith("/authenticationAction.do")
				&& httpServletRequest.getQueryString() != null && httpServletRequest.getQueryString().contains(
				"method=logoutEmptyPage"));
	    }
	});

    }
}
