/*
 * I18NFilter.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-core.
 *
 * bennu-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.filters;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import pt.ist.bennu.core.util.Language;

/**
 * 
 * @author Luis Cruz
 */
public class I18NFilter implements Filter {
	public static final String LOCALE_KEY = I18NFilter.class.getName() + "_LOCAL_KEY";

	FilterConfig config;

	ServletContext servletContext;

	@Override
	public void init(final FilterConfig config) throws ServletException {
		this.config = config;
		this.servletContext = config.getServletContext();
	}

	@Override
	public void destroy() {
		this.servletContext = null;
		this.config = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		String requestLocale = request.getParameter("locale");
		Locale locale;
		if (requestLocale != null) {
			String[] localeParts = requestLocale.split("_");
			locale = localeParts.length > 1 ? new Locale(localeParts[0], localeParts[1]) : new Locale(requestLocale);
			((HttpServletRequest) request).getSession(true).setAttribute(LOCALE_KEY, locale);
		} else {
			HttpSession session = ((HttpServletRequest) request).getSession(false);
			if (session != null && session.getAttribute(LOCALE_KEY) != null) {
				locale = (Locale) session.getAttribute(LOCALE_KEY);
			} else {
				locale = Language.getDefaultLocale();
			}
		}
		Language.setLocale(locale);
		chain.doFilter(request, response);
	}
}
