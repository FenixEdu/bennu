package org.fenixedu.bennu.spring.portal;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.servlet.PortalBackend;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;

public class SpringPortalBackend implements PortalBackend {

    public static final String BACKEND_KEY = "bennu-spring";

    private static final SemanticURLHandler HANDLER = new SemanticURLHandler() {
        @Override
        public void handleRequest(MenuFunctionality functionality, HttpServletRequest request, HttpServletResponse response,
                FilterChain chain) throws IOException, ServletException {
            request.getRequestDispatcher(functionality.getItemKey()).forward(request, response);
        }
    };

    @Override
    public SemanticURLHandler getSemanticURLHandler() {
        return HANDLER;
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
