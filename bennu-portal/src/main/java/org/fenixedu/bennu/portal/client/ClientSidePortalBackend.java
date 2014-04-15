package org.fenixedu.bennu.portal.client;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.servlet.PortalBackend;
import org.fenixedu.bennu.portal.servlet.SemanticURLHandler;

public class ClientSidePortalBackend implements PortalBackend {

    public static final String BACKEND_KEY = "client-side";

    @Override
    public SemanticURLHandler getSemanticURLHandler() {
        return new SemanticURLHandler() {
            @Override
            public void handleRequest(MenuFunctionality functionality, HttpServletRequest request, HttpServletResponse response,
                    FilterChain chain) throws IOException, ServletException {
                chain.doFilter(request, response);
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
