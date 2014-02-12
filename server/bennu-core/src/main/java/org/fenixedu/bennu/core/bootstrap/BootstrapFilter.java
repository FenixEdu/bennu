package org.fenixedu.bennu.core.bootstrap;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class BootstrapFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();
        if (!shouldFilter() || path.endsWith(".js") || path.endsWith(".css") || path.startsWith(req.getContextPath() + "/img")
                || path.startsWith(req.getContextPath() + "/api/")) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher("/bennu-core/bootstrap.jsp").forward(request, response);
        }
    }

    @Atomic(mode = TxMode.READ)
    private final boolean shouldFilter() {
        return Bennu.getInstance().getUserSet().isEmpty();
    }

    @Override
    public void destroy() {
    }

}
