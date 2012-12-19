package module.webserviceutils.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.webserviceutils.domain.HostSystem;
import module.webserviceutils.domain.ServerHost;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import pt.ist.bennu.core.applicationTier.Authenticate;
import pt.ist.bennu.core.domain.User;

public class JerseyAuthenticationFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(JerseyAuthenticationFilter.class);

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
	final String userToLogin = request.getHeader("__userToLogin__");
	if (checkAccessControl(request)) {
	    try {
		if (!StringUtils.isEmpty(userToLogin)) {
		    Authenticate.authenticate(User.findByUsername(userToLogin));
		}
		filterChain.doFilter(request, response);
	    } finally {
		if (!StringUtils.isEmpty(userToLogin)) {
		    pt.ist.fenixWebFramework.security.UserView.setUser(null);
		}
	    }
	} else {
	    throw new ServletException("Not Authorized");
	}
    }

    private boolean checkAccessControl(final HttpServletRequest request) {
	final String username = request.getHeader("__username__");
	final String password = request.getHeader("__password__");

	final String remoteHostUrl = getRemoteHostUrl(request);
	boolean accessControl = false;

	final ServerHost serverHost = HostSystem.getServerByClientAddress(remoteHostUrl);
	if (serverHost != null) {
	    if (serverHost.matches(username, password)) {
		accessControl = serverHost.isEnabled();
		if (!accessControl) {
		    LOG.warn(String.format("disabled server host\nrequest url: %s\nusername: %s\npassword: %s\n", remoteHostUrl,
			    username, password));
		}
	    } else {
		LOG.warn(String.format("server host doesn't match\nhost: %s\nusername: %s\npassword: %s\n", remoteHostUrl,
			username, password));
	    }
	} else {
	    LOG.warn(String.format("server host is null : %s\n", remoteHostUrl));
	}
	return accessControl;
    }

    @Override
    public void init(final FilterConfig arg0) throws ServletException {

    }

    private String getRemoteHostUrl(final HttpServletRequest request) {
	final String xForwardForHeader = request.getHeader("X-Forwarded-For");
	if (xForwardForHeader != null && !xForwardForHeader.isEmpty()) {
	    final int urlSeperator = xForwardForHeader.indexOf(',');
	    return urlSeperator > 0 ? xForwardForHeader.substring(0, urlSeperator) : xForwardForHeader;
	}
	return request.getRemoteHost();
    }

}
