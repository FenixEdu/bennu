/*
 * CloseTransactionFilter.java
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
package org.fenixedu.bennu.core.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.io.IOUtils;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class CloseTransactionFilter implements Filter {

    @Override
    public void init(final FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            doChain(request, response, chain);
        } catch (Error t) {
            t.printStackTrace();
            InputStream resource = request.getServletContext().getResourceAsStream("/error.html");
            if (resource != null) {
                ServletOutputStream outputStream = response.getOutputStream();
                response.setContentType("text/html");
                IOUtils.copy(resource, outputStream);
                outputStream.flush();
            } else {
                throw t;
            }
        }
    }

    @Atomic(mode = TxMode.READ, flattenNested = false)
    private void doChain(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}
