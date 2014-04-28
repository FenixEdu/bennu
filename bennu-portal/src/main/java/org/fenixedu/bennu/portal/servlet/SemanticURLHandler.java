package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.portal.domain.MenuFunctionality;

/**
 * A Semantic URL handler is able to process a request that matches a {@link MenuFunctionality} and knows how to render it.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public interface SemanticURLHandler {

    /**
     * Handles the request to render the given functionality.
     * 
     * @param functionality
     *            The selected {@link MenuFunctionality}.
     * @param request
     *            The incoming request.
     * @param response
     *            The outgoing response.
     * @param chain
     *            The filter chain that intercepted this request.
     */
    public void handleRequest(MenuFunctionality functionality, HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException;

}
