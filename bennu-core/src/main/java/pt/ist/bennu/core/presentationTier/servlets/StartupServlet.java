/* 
 * @(#)StartupServlet.java 
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
package pt.ist.bennu.core.presentationTier.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core._development.PropertiesManager;
import pt.ist.bennu.core.applicationTier.Authenticate;
import pt.ist.bennu.core.domain.Layout;
import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.Theme;
import pt.ist.bennu.core.domain.ThemeType;
import pt.ist.bennu.core.domain.groups.AnyoneGroup;
import pt.ist.bennu.core.domain.groups.Role;
import pt.ist.bennu.core.domain.groups.UserGroup;
import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter.ChecksumPredicate;
import pt.ist.fenixframework.Atomic;
import pt.utl.ist.fenix.tools.util.i18n.Language;

/**
 * 
 * @author Pedro Santos
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
@WebListener
public class StartupServlet implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(StartupServlet.class);

    private static final long serialVersionUID = -7035892286820898843L;

    @Override
    @Atomic(readOnly = true)
    public void contextInitialized(ServletContextEvent event) {
        try {

            logger.info("Initializing Bennu");

            setLocale();
            FenixWebFramework.initialize(PropertiesManager.getFenixFrameworkConfig());

            ensureMyOrg();

            try {
                MyOrg.initModules();
            } catch (Throwable t) {
                t.printStackTrace();
                throw new Error(t);
            }

            final String managerUsernames = PropertiesManager.getProperty("manager.usernames");
            Authenticate.initRole(RoleType.MANAGER, managerUsernames);

            initializePersistentGroups();

            syncThemes(event.getServletContext());

            syncLayouts(event.getServletContext());

            registerFilterCheckSumRules();

            logger.info("Bennu Initialized Successfully");
        } finally {
            Language.setLocale(null);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @Atomic
    private MyOrg ensureMyOrg() {
        MyOrg myOrg = MyOrg.getInstance();
        if (myOrg == null) {
            myOrg = new MyOrg();
        }
        return myOrg;
    }

    private void setLocale() {
        final String language = PropertiesManager.getProperty("language");
        final String location = PropertiesManager.getProperty("location");
        Language.setLocale(new Locale(language, location));
    }

    private void syncThemes(ServletContext context) {
        Set<String> themeFolders = context.getResourcePaths("/CSS/");

        Set<String> themeNames = new HashSet<>();
        for (String folder : themeFolders) {
            themeNames.add(folder.substring("/CSS/".length(), folder.length() - 1));
        }

        for (Theme theme : MyOrg.getInstance().getThemes()) {
            if (!matchThemeOrLayoutName(theme.getName(), themeNames)) {
                Theme.deleteTheme(theme);
            }
        }

        for (String themeName : themeNames) {
            if (!Theme.isThemeAvailable(themeName)) {
                try {
                    Properties themeProperties = new Properties();
                    InputStream resource = context.getResourceAsStream("CSS/" + themeName + "/theme.properties");
                    if (resource != null) {
                        themeProperties.load(resource);
                        ThemeType type = ThemeType.valueOf(themeProperties.getProperty("theme.type"));
                        String description = themeProperties.getProperty("theme.description");
                        String screenshotFileName = themeProperties.getProperty("theme.screenshotFileName");
                        Theme.createTheme(themeName, description, type, screenshotFileName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void syncLayouts(ServletContext context) {
        Set<String> layoutFolders = context.getResourcePaths("/layout/");

        Set<String> layoutNames = new HashSet<>();
        for (String folder : layoutFolders) {
            layoutNames.add(folder.substring("/layout/".length(), folder.length() - 1));
        }

        for (Layout layout : MyOrg.getInstance().getLayoutSet()) {
            if (!matchThemeOrLayoutName(layout.getName(), layoutNames)) {
                layout.delete();
            }
        }

        for (String layoutName : layoutNames) {
            if (Layout.getLayoutByName(layoutName) == null) {
                Layout.createLayout(layoutName);
            }
        }
    }

    private boolean matchThemeOrLayoutName(String name, Set<String> themeFolders) {
        for (String file : themeFolders) {
            if (file.equals(name)) {
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

            @Override
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
