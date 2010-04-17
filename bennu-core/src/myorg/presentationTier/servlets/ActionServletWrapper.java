/*
 * @(#)ActionServletWrapper.java
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
 *   3 of the License, or (at your option) any later version.*
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

package myorg.presentationTier.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.MessageResourcesConfig;
import org.apache.struts.config.ModuleConfig;

import pt.ist.fenixWebFramework.renderers.plugin.RenderersRequestProcessorImpl;
import pt.ist.fenixWebFramework.renderers.plugin.SimpleRenderersRequestProcessor;
import pt.utl.ist.fenix.tools.util.FileUtils;

public class ActionServletWrapper extends ActionServlet {

    private static class ServletConfigWrapper implements ServletConfig {

	private final ServletConfig servletConfig;

	public static final Map<String, String> parameterMap = new HashMap<String, String>();
	static {
	    parameterMap.put("config", "/WEB-INF/conf/struts-default.xml");
	    parameterMap.put("application", "resources.MyorgResources");

	    parameterMap.put("debug", "3");
	    parameterMap.put("detail", "3");
	    parameterMap.put("validating", "true");
	}

	public ServletConfigWrapper(final ServletConfig servletConfig) {
	    this.servletConfig = servletConfig;
	}

	public String getInitParameter(final String name) {
	    final String parameter = parameterMap.get(name);
	    return parameter == null ? servletConfig == null ? null : servletConfig.getInitParameter(name) : parameter;
	}

	public Enumeration getInitParameterNames() {
	    return new Enumeration() {

		private final Enumeration enumeration = servletConfig == null ? null : servletConfig.getInitParameterNames();

		private final Iterator iterator = parameterMap.keySet().iterator();

		public boolean hasMoreElements() {
		    return iterator.hasNext() || (enumeration != null && enumeration.hasMoreElements());
		}

		public Object nextElement() {
		    return enumeration != null && enumeration.hasMoreElements() ? enumeration.nextElement() : iterator.next();
		}

	    };
	}

	public ServletContext getServletContext() {
	    return servletConfig == null ? null : servletConfig.getServletContext();
	}

	public String getServletName() {
	    return servletConfig == null ? null : servletConfig.getServletName();
	}

    }

    public static ServletConfig servletConfig = null;

    @Override
    public void init(final ServletConfig config) throws ServletException {
	final ServletConfigWrapper servletConfigWrapper = new ServletConfigWrapper(config);
	RenderersRequestProcessorImpl.implementationClass = SimpleRenderersRequestProcessor.class;
	super.init(servletConfigWrapper);
	this.servletConfig = config;
    }

    @Override
    protected ModuleConfig initModuleConfig(String prefix, String paths) throws ServletException {
	final ModuleConfig moduleConfig = super.initModuleConfig(prefix, paths);

	final InputStream inputStream = getClass().getResourceAsStream("/.messageResources");
	try {
	    final String contents = FileUtils.readFile(inputStream);
	    final String[] lines = contents.split("\n");
	    for (final String resourceBundleName : lines) {
		final String key = getMessageResourceBundleKey(resourceBundleName);
		final String parameter = getMessageResourceBundleParameter(resourceBundleName);
		createMessageResourcesConfig(moduleConfig, key, parameter);
		if (resourceBundleName.equals("MyorgResources")) {
		    createMessageResourcesConfig(moduleConfig, "org.apache.struts.action.MESSAGE", parameter);
		}
	    }
	} catch (final IOException e) {
	    e.printStackTrace();
	}

	return moduleConfig;
    }

    private void createMessageResourcesConfig(final ModuleConfig moduleConfig, final String key, final String parameter) {
	final MessageResourcesConfig messageResourcesConfig = new MessageResourcesConfig();
	messageResourcesConfig.setFactory("org.apache.struts.util.PropertyMessageResourcesFactory");
	messageResourcesConfig.setKey(key);
	messageResourcesConfig.setNull(false);
	messageResourcesConfig.setParameter(parameter);
	moduleConfig.addMessageResourcesConfig(messageResourcesConfig);
    }

    private String getMessageResourceBundleKey(final String resourceBundleName) {
	final StringBuilder stringBuilder = new StringBuilder(resourceBundleName.length() + 1);
	for (int i = 0; i < resourceBundleName.length(); i++) {
	    final char c = resourceBundleName.charAt(i);
	    if (i > 0 && Character.isUpperCase(c)) {
		stringBuilder.append("_");
	    }
	    stringBuilder.append(Character.toUpperCase(c));
	}
	return stringBuilder.toString();
    }

    private String getMessageResourceBundleParameter(final String resourceBundleName) {
	return "resources." + resourceBundleName;
    }

}
