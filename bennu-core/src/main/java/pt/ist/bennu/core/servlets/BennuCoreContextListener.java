/*
 * BennuCoreContextListener.java
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
package pt.ist.bennu.core.servlets;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.core.util.Language;
import pt.ist.bennu.service.Service;
import pt.ist.fenixframework.FenixFrameworkInitializer;
import pt.ist.fenixframework.pstm.PersistentRoot;

/**
 * 
 * @author Pedro Santos
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
@WebListener
public class BennuCoreContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(BennuCoreContextListener.class);

    private ClassLoader thisClassLoader;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            Language.setDefaultLocale(ConfigurationManager.getDefaultLocale());
            setLocale();
            try {
                Class.forName(FenixFrameworkInitializer.class.getName());
            } catch (ClassNotFoundException e) {
            }
            PersistentRoot.initRootIfNeeded(ConfigurationManager.getFenixFrameworkConfig());

            ensureModelBootstrap();
        } finally {
            Language.setLocale(null);
        }
    }

    @Service
    private void ensureModelBootstrap() {
        if (!Bennu.getInstance().hasAnyVirtualHosts()) {
            logger.info("Bootstrapping bennu application");
            new VirtualHost("localhost");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        this.thisClassLoader = this.getClass().getClassLoader();
        interruptThreads();
        deregisterJDBCDrivers();
    }

    private void setLocale() {
        final String language = ConfigurationManager.getProperty("language");
        final String location = ConfigurationManager.getProperty("location");
        Language.setLocale(new Locale(language, location));
    }

    private void deregisterJDBCDrivers() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            ClassLoader loader = driver.getClass().getClassLoader();
            if (loader != null && loader.equals(this.thisClassLoader)) {
                try {
                    DriverManager.deregisterDriver(driver);
                    logger.info("Successfully deregistered JDBC driver " + driver);
                } catch (SQLException e) {
                    logger.warn("Failed to deregister JDBC driver " + driver + ". This may cause a potential leak.", e);
                }
            }
        }
    }

    private void interruptThreads() {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread == null || thread.getContextClassLoader() != thisClassLoader || thread == Thread.currentThread()) {
                continue;
            }

            if (thread.isAlive()) {
                System.out.println("Killing initiated thread: " + thread + " of class " + thread.getClass());
                thread.interrupt();
            }
        }
    }
}
