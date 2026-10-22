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
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.login.LoginProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Specialized filter that logs the current user out.
 *
 * Registered {@link LoginProvider}s are given a best-effort chance to terminate their own
 * sessions first (e.g. redirect the user to the Identity Provider's Single Logout endpoint, or
 * to the CAS server logout page); a failing provider is logged and ignored. The local session is
 * always terminated. The user is then redirected to the configured logout URL (or the
 * application root), unless a provider already handled the response.
 *
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@WebFilter({ "/logout", "/logout/" })
public class PortalLogoutFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(PortalLogoutFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Give registered login providers a best-effort chance to terminate their own sessions
        // (e.g. SAML Single Logout, CAS logout). A failing provider is logged and ignored: the
        // local logout must never be prevented by a provider misbehaving.
        boolean responseHandled = false;
        for (LoginProvider provider : PortalLoginServlet.providers()) {
            try {
                if (provider.logout(req, resp)) {
                    responseHandled = true;
                    break;
                }
            } catch (Exception e) {
                logger.warn("Login provider {} failed its logout; ignoring", provider.getKey(), e);
            }
        }

        // Logout locally
        Authenticate.logout(req, resp);

        if (responseHandled) {
            // The provider owns the response (e.g. redirected the user to the IdP); the local
            // session has been terminated above.
            return;
        }

        if (Strings.isNullOrEmpty(BennuPortalConfiguration.getConfiguration().logoutURL())) {
            resp.sendRedirect(req.getContextPath() + "/");
        } else {
            resp.sendRedirect(BennuPortalConfiguration.getConfiguration().logoutURL());
        }
    }

    @Override
    public void destroy() {
    }

}
