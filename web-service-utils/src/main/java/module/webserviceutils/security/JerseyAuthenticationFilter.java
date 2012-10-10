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
import pt.ist.bennu.core.applicationTier.Authenticate;
import pt.ist.bennu.core.domain.User;

public class JerseyAuthenticationFilter implements Filter {

    @Override
    public void destroy() {
	// TODO Auto-generated method stub

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
		Authenticate.authenticate(User.findByUsername(userToLogin));
		filterChain.doFilter(request, response);
	    } finally {
		pt.ist.fenixWebFramework.security.UserView.setUser(null);
	    }
	} else {
	    throw new ServletException("Not Authorized");
	}
    }

    private boolean checkAccessControl(final HttpServletRequest request) {
	final String username = request.getHeader("__username__");
	final String password = request.getHeader("__password__");

	final String remoteHostUrl = getRemoteHostUrl(request);

	final ServerHost serverHost = HostSystem.getServerByClientAddress(remoteHostUrl);
	if (serverHost != null && serverHost.matches(username, password)) {
	    final Boolean enabled = serverHost.getEnabled();
	    return enabled == null ? false : enabled;
	}
	return false;
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
