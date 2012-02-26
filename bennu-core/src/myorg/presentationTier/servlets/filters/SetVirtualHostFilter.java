/* 
* @(#)SetVirtualHostFilter.java 
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
package myorg.presentationTier.servlets.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.User;
import myorg.domain.VirtualHost;

import org.apache.log4j.Logger;

/**
 * 
 * @author  Paulo Abrantes
 * @author  Luis Cruz
 * 
*/
public class SetVirtualHostFilter implements Filter {

    private static final Logger logger = Logger.getLogger(SetVirtualHostFilter.class.getName());

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
	    throws IOException, ServletException {
	final String serverName = servletRequest.getServerName();
	try {
	    final VirtualHost virtualHost = VirtualHost.setVirtualHostForThread(serverName.toLowerCase());
	    if (logger.isDebugEnabled()) {
		final String hostname = virtualHost == null ? null : virtualHost.getHostname();
		final User user = UserView.getCurrentUser();
		final String username = user == null ? null : user.getUsername();
		logger.debug("Setting virtual host: " + hostname + " for user: " + username + " on server: " + serverName);
	    }
	    servletRequest.setAttribute("virtualHost", virtualHost);
	    filterChain.doFilter(servletRequest, servletResponse);
	} finally {
	    VirtualHost.releaseVirtualHostFromThread();
	}
    }

}
