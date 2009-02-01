package myorg.presentationTier.servlets.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import myorg.domain.VirtualHost;

public class SetVirtualHostFilter implements Filter {

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
	    final VirtualHost virtualHost = VirtualHost.setVirtualHostForThread(serverName);
	    servletRequest.setAttribute("virtualHost", virtualHost);
	    filterChain.doFilter(servletRequest, servletResponse);
	} finally {
	    VirtualHost.releaseVirtualHostFromThread();
	}
    }

}
