/*
 * I18NFilter.java
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
package org.fenixedu.bennu.core.i18n;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.I18N;

/**
 * Ensures the session {@link Locale} is available in {@link I18N#getLocale()}
 * 
 * @author Luis Cruz
 */
public class I18NFilter implements Filter {

    private static final String LOCALE_COOKIE_NAME = "i18n.locale";

    // 30 Days
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30;

    private Map<String, Locale> locales;

    @Override
    public void init(final FilterConfig config) throws ServletException {
        locales = CoreConfiguration.supportedLocales().stream().collect(Collectors.toMap(Locale::toLanguageTag, a -> a));
    }

    @Override
    public void destroy() {
        locales = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        try {
            I18N.setLocale(retrieveFromCookies(((HttpServletRequest) request).getCookies()));
            chain.doFilter(request, response);
        } finally {
            I18N.setLocale(null);
        }
    }

    private Locale retrieveFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(LOCALE_COOKIE_NAME)) {
                    return locales.get(cookie.getValue());
                }
            }
        }
        return null;
    }

    public static void updateLocale(Locale locale, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(LOCALE_COOKIE_NAME, locale == null ? "" : locale.toLanguageTag());

        cookie.setMaxAge(locale == null ? 0 : COOKIE_MAX_AGE);
        cookie.setPath(request.getContextPath() + "/");

        response.addCookie(cookie);
        I18N.setLocale(locale);
    }
}
