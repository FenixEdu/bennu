package org.fenixedu.bennu.core.servlets;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter that catches any exception thrown by the application, allowing a configured handler to deal with it.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class ExceptionHandlerFilter implements Filter {

    /**
     * An {@link ExceptionHandler} is invoked whenever an uncaught exception is thrown by the application, giving it an
     * opportunity to recover gracefully.
     */
    public static interface ExceptionHandler {
        /**
         * Attempts to handle the exception thrown by the application.
         * 
         * @param request
         *            The request that caused the exception
         * @param response
         *            The response object
         * @param throwable
         *            The exception that was thrown by the application
         * @return
         *         Whether the handler was able to handle the exception. If not, the exception is re-thrown, leaving its handling
         *         to the container.
         * @throws ServletException
         *             If an exception occurs while handling the original exception
         * @throws IOException
         *             If an exception occurs while handling the original exception
         */
        public boolean handle(ServletRequest request, ServletResponse response, Throwable throwable) throws ServletException,
                IOException;
    }

    /**
     * Registers the given {@link ExceptionHandler} as this application's exception handler
     * 
     * @param handler the {@link ExceptionHandler} instance to register
     * @throws NullPointerException if the provided handler is null
     */
    public static void setExceptionHandler(ExceptionHandler handler) {
        exceptionHandler = Objects.requireNonNull(handler);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Throwable throwable) {
            if (!exceptionHandler.handle(request, response, throwable)) {
                // Re-throw the exception to the container if the handler cannot handle it
                throw throwable;
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    private static ExceptionHandler exceptionHandler = (req, resp, t) -> false;

}
