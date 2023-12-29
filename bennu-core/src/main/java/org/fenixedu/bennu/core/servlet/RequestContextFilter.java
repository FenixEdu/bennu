package org.fenixedu.bennu.core.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequestContextFilter implements Filter {

    private static final InheritableThreadLocal<HttpServletRequest> REQUEST = new InheritableThreadLocal<>();

    @Override
    public void init(final FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest httpServletRequest) {
                REQUEST.set(httpServletRequest);
            }
            chain.doFilter(request, response);
        } finally {
            REQUEST.remove();
        }
    }

    public static HttpServletRequest get() {
        return REQUEST.get();
    }

}
