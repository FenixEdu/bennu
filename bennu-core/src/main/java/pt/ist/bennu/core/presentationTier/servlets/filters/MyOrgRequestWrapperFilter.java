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
package pt.ist.bennu.core.presentationTier.servlets.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.ist.bennu.core.presentationTier.servlets.filters.requestWrappers.ResponseWrapper;
import pt.ist.fenixWebFramework.servlets.filters.RequestWrapperFilter;

/**
 * 
 * @author Sérgio Silva
 * @author Luis Cruz
 * 
 */
public class MyOrgRequestWrapperFilter extends RequestWrapperFilter {

	final static Set<String> urlPiecesToExclude = new HashSet<String>();

	static {
		urlPiecesToExclude.add("vaadin");
		urlPiecesToExclude.add("VAADIN");
		urlPiecesToExclude.add("/jersey/");
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		if (urlMustbeSkipped(httpServletRequest)) {
			// &&
			// httpServletRequest.getHeader("Content-Type").equals("multipart/form-data")
			chain.doFilter(request, response);
		} else {
			final ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);
			super.doFilter(request, responseWrapper, chain);
			responseWrapper.writeRealResponse();
		}
	}

	private boolean urlMustbeSkipped(final HttpServletRequest httpServletRequest) {
		final String requestURI = httpServletRequest.getRequestURI();
		for (String piece : urlPiecesToExclude) {
			if (requestURI.contains(piece)) {
				return true;
			}
		}
		return false;
	}

}
