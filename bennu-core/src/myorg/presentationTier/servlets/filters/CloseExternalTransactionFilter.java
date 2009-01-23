package myorg.presentationTier.servlets.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import myorg.persistenceTier.externalRepository.DbHandler;

public class CloseExternalTransactionFilter implements Filter {

    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
	    throws IOException, ServletException {
	try {
	    filterChain.doFilter(servletRequest, servletResponse);
	    DbHandler.commitAll();
	} finally {
	    DbHandler.rolebackAll();
	}
    }

}
