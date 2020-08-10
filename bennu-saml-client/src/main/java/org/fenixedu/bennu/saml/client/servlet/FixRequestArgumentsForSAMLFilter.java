package org.fenixedu.bennu.saml.client.servlet;

import javax.servlet.*;
import java.io.IOException;

/**
 * Parses body arguments before other filters breaking the request
 * 
 * @author Diogo Silva (diogo.m.r.silva@tecnico.ulisboa.pt)
 *
 */
public class FixRequestArgumentsForSAMLFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
            request.getParameterMap();
            chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
