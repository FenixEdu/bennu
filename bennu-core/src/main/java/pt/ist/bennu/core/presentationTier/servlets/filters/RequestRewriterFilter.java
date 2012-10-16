/* 
 * @(#)RequestRewriterFilter.java 
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import pt.ist.bennu.core.presentationTier.servlets.filters.contentRewrite.ContentContextInjectionRewriter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.ResponseWrapper;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class RequestRewriterFilter extends pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriterFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
	    throws IOException, ServletException {

	if (((HttpServletRequest) servletRequest).getRequestURI().contains("/jersey/")) {
	    filterChain.doFilter(servletRequest, servletResponse);
	} else {
	    super.doFilter(servletRequest, servletResponse, filterChain);
	}

    }

    @Override
    protected void writeResponse(FilterChain filterChain, HttpServletRequest httpServletRequest, ResponseWrapper responseWrapper)
	    throws IOException, ServletException {
	responseWrapper.writeRealResponse(new ContentContextInjectionRewriter(httpServletRequest), new GenericChecksumRewriter(
		httpServletRequest));
    }

}
