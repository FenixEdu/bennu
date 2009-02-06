/*
 * @(#)ExceptionHandlerFilter.java
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
import javax.servlet.http.HttpServletRequest;

public class ExceptionHandlerFilter implements Filter {

    public void destroy() {
	// TODO Auto-generated method stub

    }

    public void init(FilterConfig arg0) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
	    ServletException {
	HttpServletRequest httpServletRequest = (HttpServletRequest) request;
	try {
	    filterChain.doFilter(request, response);
	} catch (ServletException servletException) {
	    if (servletException.getRootCause() != null) {
		servletException.getRootCause().printStackTrace();
	    }
	    /*
	     * StringWriter out = new StringWriter();
	     * servletException.getRootCause().printStackTrace(new
	     * PrintWriter(out)); request.setAttribute("error", out.toString());
	     */
	    httpServletRequest.getRequestDispatcher("/error.jsp").forward(request, response);
	} catch (Throwable throwable) {
	    throwable.printStackTrace();
	    /*
	     * StringWriter out = new StringWriter();
	     * exception.printStackTrace(new PrintWriter(out));
	     * request.setAttribute("error", out.toString());
	     */
	    httpServletRequest.getRequestDispatcher("/error.jsp").forward(request, response);
	}

    }
}
