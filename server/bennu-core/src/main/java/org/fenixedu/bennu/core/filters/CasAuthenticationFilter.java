/*
 * CasAuthenticationFilter.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.filters;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class CasAuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CasAuthenticationFilter.class);

    private final TicketValidator validator =
            new Cas20ServiceTicketValidator(CoreConfiguration.getConfiguration().casServerUrl());

    public static final String AUTHENTICATION_EXCEPTION_KEY = "CAS_AUTHENTICATION_EXCEPTION";

    @Override
    public void init(final FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final String ticket = httpServletRequest.getParameter("ticket");
        if (ticket != null) {
            Authenticate.logout(httpServletRequest.getSession());
            final String requestURL = URLDecoder.decode(httpServletRequest.getRequestURL().toString(), Charsets.UTF_8.name());
            try {
                String username = validator.validate(ticket, requestURL).getPrincipal().getName();
                Authenticate.login(httpServletRequest.getSession(), username);
                // Redirect to the same page, to prevent replaying the ticket parameter
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.sendRedirect(httpServletRequest.getRequestURL().toString());
                return;
            } catch (TicketValidationException | AuthorizationException e) {
                // its ok, the user just won't have his session
                logger.warn(e.getMessage(), e);
                request.setAttribute(AUTHENTICATION_EXCEPTION_KEY, e);
            }
        }
        chain.doFilter(request, response);
    }

}
