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
package org.fenixedu.bennu.core.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.core.bootstrap.BootstrapperRegistry;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.rest.Healthcheck;
import org.fenixedu.bennu.core.rest.SystemResource;
import org.fenixedu.bennu.core.util.CoreConfiguration;
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

    private ClassLoader thisClassLoader;

    /**
     * This methods enables multipart feature in the JAX-RS api.
     * 
     * @see CoreConfiguration#getMultipartConfigElement
     * 
     * @param event The servlet context
     */
    private void registerMultipartConfig(ServletContextEvent event) {
        final ServletRegistration restApplicationRegistration =
                event.getServletContext().getServletRegistration(BennuJerseyRestApplication.class.getName());

        if (restApplicationRegistration != null) {
            if (restApplicationRegistration instanceof ServletRegistration.Dynamic) {
                ((ServletRegistration.Dynamic) restApplicationRegistration).setMultipartConfig(CoreConfiguration
                        .getMultipartConfigElement());
                logger.info("Configure MultiPart for REST Application {}", restApplicationRegistration.getName());
            } else {
                logger.debug("ServletRegistration for {} doesn't implement ServletRegistration.Dynamic",
                        BennuJerseyRestApplication.class.getName());
            }
        } else {
            logger.debug("Couldn't find ServletRegistration for {}", BennuJerseyRestApplication.class.getName());
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {

        registerMultipartConfig(event);

        BootstrapperRegistry.registerBootstrapFilter(event.getServletContext());

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
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        FenixFramework.shutdown();
        this.thisClassLoader = this.getClass().getClassLoader();
        interruptThreads();
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
