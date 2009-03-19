package myorg.presentationTier.servlets.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixWebFramework.renderers.components.state.EditRequest.ViewStateUserChangedException;

public class ViewStateChangedExceptionCatcher implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
	    ServletException {
	try {
	    filterChain.doFilter(request, response);
	} catch (ViewStateUserChangedException exception) {
	    ((HttpServletResponse) response).sendRedirect(FenixWebFramework.getConfig().getTamperingRedirect());
	}
    }
}
