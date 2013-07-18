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
package pt.ist.bennu.core.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.util.CookieReaderUtils;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class ContextPathFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ContextPathFilter.class);

    private static final String CONTEXT_PATH_COOKIE_NAME = "contextPath";

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
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        final String contextPath = request.getServletContext().getContextPath();
        Cookie cookie = CookieReaderUtils.getCookieForName("contextPath", httpServletRequest);
        if (cookie == null) {
            Cookie newCookie = new Cookie(CONTEXT_PATH_COOKIE_NAME, contextPath);
            newCookie.setPath("/");
            httpServletResponse.addCookie(newCookie);
        }
        chain.doFilter(request, response);
    }
}
