package org.fenixedu.bennu.portal.client;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.portal.servlet.PortalBackend;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;

public class ClientSidePortalBackend implements PortalBackend {

    public static final String BACKEND_KEY = "client-side";

    @Override
    public SemanticURLHandler getSemanticURLHandler() {
        return (functionality, request, response, chain) -> {
            String forwardUrl =
                    "/" + functionality.getParent().getPath() + "/"
                            + (functionality.getPath().startsWith("#") ? "" : functionality.getPath());
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(forwardUrl);
            if (requestDispatcher != null) {
                requestDispatcher.forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No forward url could be processed");
            }
        };
    }

    @Override
    public boolean requiresServerSideLayout() {
        return true;
    }

    @Override
    public String getBackendKey() {
        return BACKEND_KEY;
    }

}
