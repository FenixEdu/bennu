package pt.ist.bennu.core.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.security.UserView;

/**
 * 
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public class VirtualHostFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(VirtualHostFilter.class);

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException {
		final String serverName = servletRequest.getServerName();
		try {
			final VirtualHost virtualHost = VirtualHost.setVirtualHostForThread(serverName.toLowerCase());
			if (logger.isDebugEnabled()) {
				final String hostname = virtualHost == null ? null : virtualHost.getHostname();
				final User user = UserView.getUser();
				final String username = user == null ? null : user.getUsername();
				logger.debug("Setting virtual host: " + hostname + " for user: " + username + " on server: " + serverName);
			}
			servletRequest.setAttribute("virtualHost", virtualHost);
			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			VirtualHost.releaseVirtualHostFromThread();
		}
	}

}
