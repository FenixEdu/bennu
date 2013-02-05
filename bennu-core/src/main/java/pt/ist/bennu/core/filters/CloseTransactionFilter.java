/*
 * CloseTransactionFilter.java
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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import pt.ist.fenixframework.pstm.RequestInfo;
import pt.ist.fenixframework.pstm.Transaction;

public class CloseTransactionFilter implements Filter {

    @Override
    public void init(final FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (request instanceof HttpServletRequest) {
            RequestInfo.setRequestURI(((HttpServletRequest) request).getRequestURI());
        }

        try {
            Transaction.begin(true);
            Transaction.currentFenixTransaction().setReadOnly();
            chain.doFilter(request, response);
        } finally {
            Transaction.forceFinish();
            RequestInfo.clear();
        }
    }

}
