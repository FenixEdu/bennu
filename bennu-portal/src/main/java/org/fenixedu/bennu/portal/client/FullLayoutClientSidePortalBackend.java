package org.fenixedu.bennu.portal.client;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.portal.servlet.PortalBackend;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;

/***
 * Portal support for client side technology (e.g. VueJS) with full layout schema.
 * This backend does not require server side layout since it gives full control to a SPWA implemented
 * by the main functionality.
 *
 * @author Sérgio Silva (hello@fenixedu.org)
 */
public class FullLayoutClientSidePortalBackend implements PortalBackend {

    public static final String BACKEND_KEY = "full-client-side";

    @Override
    public SemanticURLHandler getSemanticURLHandler() {
        return (functionality, request, response, chain) -> {
            final String forwardUrl = "/" + functionality.getPath() + "/";
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
        return false;
    }

    @Override
    public String getBackendKey() {
        return BACKEND_KEY;
    }

}
