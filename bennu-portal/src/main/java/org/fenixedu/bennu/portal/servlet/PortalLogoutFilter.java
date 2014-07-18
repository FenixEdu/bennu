package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;

/**
 * Specialized filter that logs the current user out.
 * 
 * If CAS is enabled, the user is redirected to the CAS logout page, after
 * being logged out locally.
 * 
 * If CAS is not enabled, the user is redirected to the application's root.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@WebFilter({ "/logout", "/logout/" })
public class PortalLogoutFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Logout locally
        Authenticate.logout(req.getSession(false));

        if (CoreConfiguration.casConfig().isCasEnabled()) {
            // Send to the CAS logout page
            resp.sendRedirect(CoreConfiguration.casConfig().getCasLogoutUrl());
        } else {
            // Send to the application's root
            resp.sendRedirect(req.getContextPath() + "/");
        }
    }

    @Override
    public void destroy() {
    }

}
