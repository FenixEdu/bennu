package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.portal.domain.MenuFunctionality;

/**
 * Builtin Portal Backend that forwards requests to the URL specified as the Item Key of the {@link MenuFunctionality}.
 * 
 * Note that for this Backend, there can be multiple {@link MenuFunctionality} with the same key.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 */
public class ForwarderPortalBackend implements PortalBackend {

    private static final SemanticURLHandler HANDLER = new SemanticURLHandler() {
        @Override
        public void handleRequest(MenuFunctionality functionality, HttpServletRequest request, HttpServletResponse response,
                FilterChain chain) throws IOException, ServletException {
            // Remove the functionality, allowing the target to choose the proper one
            BennuPortalDispatcher.selectFunctionality(request, null);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(functionality.getItemKey());
            if (requestDispatcher != null) {
                requestDispatcher.forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No forward url could be processed");
            }
        }
    };

    @Override
    public SemanticURLHandler getSemanticURLHandler() {
        return HANDLER;
    }

    @Override
    public boolean requiresServerSideLayout() {
        return false;
    }

    @Override
    public String getBackendKey() {
        return "forward";
    }

}
