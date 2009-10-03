/*
 * @(#)SetVirtualHostFilter.java
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

package myorg.presentationTier.servlets.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import myorg.domain.VirtualHost;

public class SetVirtualHostFilter implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
	System.out.println("Initializing set virtual host filter.");
    }

    @Override
    public void destroy() {
	System.out.println("Destroying set virtual host filter.");
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
    		throws IOException, ServletException {
	System.out.println("Entering set virtual host doFilter.");
	final String serverName = servletRequest.getServerName();
	System.out.println("serverName: " + serverName);
	try {
	    final VirtualHost virtualHost = VirtualHost.setVirtualHostForThread(serverName.toLowerCase());
	    System.out.println("Found virtual host: " + virtualHost.getHostname());
	    servletRequest.setAttribute("virtualHost", virtualHost);
	    filterChain.doFilter(servletRequest, servletResponse);
	} finally {
	    VirtualHost.releaseVirtualHostFromThread();
	}
	System.out.println("Exiting set virtual host doFilter.");
    }

}
