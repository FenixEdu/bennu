/* 
* @(#)ExceptionHandlerFilter.java 
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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.presentationTier.util.ExceptionInformation;

/**
 * 
 * @author Artur Ventura
 * @author João Neves
 * @author Pedro Santos
 * @author João Figueiredo
 * @author Luis Cruz
 * 
 */
public class ExceptionHandlerFilter implements Filter {

    public static abstract class CustomeHandler {

        public abstract boolean isCustomizedFor(final Throwable t);

        public abstract void handle(final HttpServletRequest httpServletRequest, final ServletResponse response)
                throws ServletException, IOException;

    }

    private static final List<CustomeHandler> customeHandlers = new ArrayList<CustomeHandler>();

    public static void register(final CustomeHandler customeHandler) {
        customeHandlers.add(customeHandler);
    }

    public static void unregister(final CustomeHandler customeHandler) {
        customeHandlers.remove(customeHandler);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        try {
            filterChain.doFilter(request, response);
        } catch (final Throwable t) {
            for (final CustomeHandler customeHandler : customeHandlers) {
                if (customeHandler.isCustomizedFor(t)) {
                    customeHandler.handle(httpServletRequest, response);
                    return;
                }
                printTraceInformation(t);
            }
            request.setAttribute("exceptionInfo",
                    ExceptionInformation.buildUncaughtExceptionInfo((HttpServletRequest) request, t));
            httpServletRequest.getRequestDispatcher(VirtualHost.getVirtualHostForThread().getErrorPage()).forward(request,
                    response);
        }
    }

    private void printTraceInformation(final Throwable t) {
        if (t instanceof ServletException) {
            final ServletException servletException = (ServletException) t;
            if (servletException.getRootCause() != null) {
                servletException.getRootCause().printStackTrace();
                return;
            }
        }
        t.printStackTrace();
    }

}
