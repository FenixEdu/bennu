/*
 * @(#)MyOrgRequestWrapperFilter.java
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.presentationTier.servlets.filters.requestWrappers.ResponseWrapper;
import pt.ist.fenixWebFramework.servlets.filters.RequestWrapperFilter;

public class MyOrgRequestWrapperFilter extends RequestWrapperFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	    ServletException {
	final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
	if (httpServletRequest.getRequestURI().contains("vaadin")
		|| httpServletRequest.getRequestURI().contains("VAADIN") || httpServletRequest.getRequestURI().contains("CertDocSisREST")
		// && httpServletRequest.getHeader("Content-Type").equals("multipart/form-data")
	) {
	    chain.doFilter(request, response);
	} else {
	    final ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);
	    super.doFilter(request, responseWrapper, chain);
	    responseWrapper.writeRealResponse();
	}
    }

}
