/*
 * @(#)StartupServlet.java
 * 
 * Copyright 2009 Instituto Superior Tecnico Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 * https://fenix-ashes.ist.utl.pt/
 * 
 * This file is part of the Bennu Web Application Infrastructure.
 * 
 * The Bennu Web Application Infrastructure is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Bennu is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Bennu. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.servlets;

import java.util.Locale;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
			new VirtualHost("localhost");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

	private void setLocale() {
		final String language = ConfigurationManager.getProperty("language");
		final String location = ConfigurationManager.getProperty("location");
		Language.setLocale(new Locale(language, location));
	}
}
