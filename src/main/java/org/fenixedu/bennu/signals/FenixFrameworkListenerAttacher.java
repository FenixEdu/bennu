/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of Bennu Signals.
 *
 * Bennu Signals is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Signals is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Signals.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.signals;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FenixFrameworkListenerAttacher implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Signal.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Signal.shutdown();
    }
}
