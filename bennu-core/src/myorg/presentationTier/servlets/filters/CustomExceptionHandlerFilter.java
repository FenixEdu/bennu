/*
 * @(#)CustomExceptionHandlerFilter.java
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

package myorg.presentationTier.servlets.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import myorg.domain.VirtualHost;
import myorg.presentationTier.servlets.filters.ExceptionHandlerFilter.CustomeHandler;

public class CustomExceptionHandlerFilter extends CustomeHandler implements Filter {

    private Class exceptionClass;
    private String forwardPath;

    @Override
    public void init(FilterConfig arg0) throws ServletException {
	try {
	    exceptionClass = Class.forName(arg0.getInitParameter("exceptionClassname"));
	} catch (ClassNotFoundException e) {
	    throw new ServletException(e);
	}
	forwardPath = arg0.getInitParameter("forwardPath");
	ExceptionHandlerFilter.register(this);
    }

    @Override
    public void destroy() {
	exceptionClass = null;
	forwardPath = null;
	ExceptionHandlerFilter.unregister(this);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
	filterChain.doFilter(request, response);
    }

    @Override
    public boolean isCustomizedFor(final Throwable t) {
	if (t instanceof JspException) {
	    final JspException j = (JspException) t;
	    if (j.getRootCause() != null) {
		return isCustomizedFor(j.getRootCause());
	    }
	}
	if (t instanceof ServletException) {
	    final ServletException s = (ServletException) t;
	    if (s.getRootCause() != null) {
		return isCustomizedFor(s.getRootCause());
	    }
	}
	return exceptionClass.isAssignableFrom(t.getClass());
    }

    @Override
    public void handle(final HttpServletRequest httpServletRequest, final ServletResponse response) throws ServletException, IOException {
	httpServletRequest.getRequestDispatcher(forwardPath).forward(httpServletRequest, response);
    }

}
