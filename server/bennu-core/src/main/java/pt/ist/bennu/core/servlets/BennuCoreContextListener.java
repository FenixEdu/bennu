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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import pt.ist.fenixframework.FenixFramework;

/**
 * 
 * @author Pedro Santos
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
@WebListener
public class BennuCoreContextListener implements ServletContextListener {
    private ClassLoader thisClassLoader;

    @Override
    public void contextInitialized(ServletContextEvent event) {
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
