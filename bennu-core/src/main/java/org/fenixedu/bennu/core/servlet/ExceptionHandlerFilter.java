package org.fenixedu.bennu.core.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter that catches any exception thrown by the application, allowing a configured handler to deal with it.
 *
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 */
public class ExceptionHandlerFilter implements Filter {

    public static final String EXCEPTION_HANDLER_SERVLET_PATH = "/exception-handler";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Throwable throwable) {
            request.getRequestDispatcher(EXCEPTION_HANDLER_SERVLET_PATH).forward(request, response);
        }
    }
}
