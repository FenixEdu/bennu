/*
 * CharsetEncodingFilter.java
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
import java.nio.charset.Charset;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharsetEncodingFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(CharsetEncodingFilter.class);

    private static String defaultCharset = Charset.defaultCharset().name();

    @Override
    public void init(final FilterConfig config) throws ServletException {
        final String defaultCharset = config.getInitParameter("defaultCharset");
        if (defaultCharset != null && !defaultCharset.isEmpty() && Charset.forName(defaultCharset) != null) {
            CharsetEncodingFilter.defaultCharset = defaultCharset;
        }
        logger.trace("Charset : {}", defaultCharset);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(defaultCharset);
        }
        chain.doFilter(request, response);
        if (response.getCharacterEncoding() == null) {
            response.setCharacterEncoding(defaultCharset);
        }
    }

}
