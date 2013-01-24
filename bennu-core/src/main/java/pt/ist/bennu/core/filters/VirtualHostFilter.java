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
	public void init(final FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		final String serverName = request.getServerName();
		try {
			final VirtualHost virtualHost = VirtualHost.setVirtualHostForThread(serverName.toLowerCase());
			if (logger.isDebugEnabled()) {
				final String hostname = virtualHost == null ? null : virtualHost.getHostname();
				final User user = UserView.getUser();
				final String username = user == null ? null : user.getUsername();
				logger.debug("Setting virtual host: " + hostname + " for user: " + username + " on server: " + serverName);
			}
			request.setAttribute("virtualHost", virtualHost);
			chain.doFilter(request, response);
		} finally {
			VirtualHost.releaseVirtualHostFromThread();
		}
	}

}
