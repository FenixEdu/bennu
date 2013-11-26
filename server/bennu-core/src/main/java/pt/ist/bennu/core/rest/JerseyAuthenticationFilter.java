package pt.ist.bennu.core.rest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.core.util.ConfigurationManager;

import com.sun.jersey.core.util.Base64;

public class JerseyAuthenticationFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(JerseyAuthenticationFilter.class);

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        doFilter(httpServletRequest, httpServletResponse, filterChain);
    }

    public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        if (isPublicRequest(request)) {
            LOG.trace("Request is public");
            if (Authenticate.getUser() != null) {
                LOG.trace("User is logged in {}", Authenticate.getUser().getUsername());
            }
            filterChain.doFilter(request, response);
        } else {
            String userToLogin = checkAccessControl(request);
            if (userToLogin == null) {
                throw new ServletException("Not Authorized");
            }
            try {
                if (!StringUtils.isEmpty(userToLogin)) {
                    LOG.trace("login user {}", userToLogin);
                    Authenticate.login(request.getSession(true), userToLogin, "", false);
                }
                filterChain.doFilter(request, response);
            } finally {
                if (!StringUtils.isEmpty(userToLogin)) {
                    LOG.trace("logout user {}", userToLogin);
                    Authenticate.logout(request.getSession(false));
                }
            }
        }

    }

    private boolean isPublicRequest(final HttpServletRequest request) {
        final String authorization = request.getHeader("authorization");
        return StringUtils.isBlank(authorization);
    }

    /*
     * returns null if access is forbidden or username to logged in if access is allowed
     */
    private String checkAccessControl(final HttpServletRequest request) {
        final String authorization = request.getHeader("authorization");
        if (StringUtils.isBlank(authorization)) {
            return null;
        }
        final String userPass = authorization.replaceFirst("[Bb]asic ", "");
        final String[] decodedUserPass = Base64.base64Decode(userPass).split(":");
        final String username = decodedUserPass[0];
        final String password = decodedUserPass[1];
        if (password.equals(ConfigurationManager.getThisServerSecret())) {
            LOG.debug("Rest secret is known, login mock user {}", username);
            return username;
        }
        return null;
    }

    @Override
    public void init(final FilterConfig arg0) throws ServletException {

    }

}
