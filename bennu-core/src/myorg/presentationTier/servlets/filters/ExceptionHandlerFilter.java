package myorg.presentationTier.servlets.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ExceptionHandlerFilter implements Filter {

    public void destroy() {
	// TODO Auto-generated method stub

    }

    public void init(FilterConfig arg0) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
	    ServletException {
	HttpServletRequest httpServletRequest = (HttpServletRequest) request;
	try {
	    filterChain.doFilter(request, response);
	} catch (ServletException servletException) {
	    servletException.getRootCause().printStackTrace();
	    /*
	     * StringWriter out = new StringWriter();
	     * servletException.getRootCause().printStackTrace(new
	     * PrintWriter(out)); request.setAttribute("error", out.toString());
	     */
	    httpServletRequest.getRequestDispatcher("/error.jsp").forward(request, response);
	} catch (Throwable throwable) {
	    throwable.printStackTrace();
	    /*
	     * StringWriter out = new StringWriter();
	     * exception.printStackTrace(new PrintWriter(out));
	     * request.setAttribute("error", out.toString());
	     */
	    httpServletRequest.getRequestDispatcher("/error.jsp").forward(request, response);
	}

    }
}
