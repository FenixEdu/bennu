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
package org.fenixedu.bennu.core.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.core.api.SystemResource;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.rest.Healthcheck;
import org.fenixedu.bennu.core.signals.Signal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;

/**
 * 
 * @author Pedro Santos
 * @author Luis Cruz
 * @author Paulo Abrantes
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 * 
 */
@WebListener
public class BennuCoreContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(BennuCoreContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {

        // BootstrapperRegistry.registerBootstrapFilter(event.getServletContext());

        SystemResource.registerHealthcheck(new Healthcheck() {
            @Override
            public String getName() {
                return "FenixFramework";
            }

            @Override
            protected Result check() throws Exception {
                Bennu.getInstance().getUserSet().size();
                return Result.healthy();
            }
        });

        Signal.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Signal.shutdown();
        FenixFramework.shutdown();
        interruptThreads();
    }

    private void interruptThreads() {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread == null || thread.getContextClassLoader() != getClass().getClassLoader()
                    || thread == Thread.currentThread()) {
                continue;
            }

            if (thread.isAlive()) {
                logger.info("Killing initiated thread: {} of class {}", thread, thread.getClass().getName());
                thread.interrupt();
            }
        }
    }
}
