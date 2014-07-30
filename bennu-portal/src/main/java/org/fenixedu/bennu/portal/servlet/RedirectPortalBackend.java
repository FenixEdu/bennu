package org.fenixedu.bennu.portal.servlet;

import org.fenixedu.bennu.portal.domain.MenuFunctionality;

/**
 * Builtin Portal Backend that redirects requests to the URL specified as the Item Key of the {@link MenuFunctionality}.
 * 
 * Note that for this Backend, there can be multiple {@link MenuFunctionality} with the same key.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 */
public class RedirectPortalBackend implements PortalBackend {

    @Override
    public SemanticURLHandler getSemanticURLHandler() {
        return (functionality, request, response, chain) -> response.sendRedirect(functionality.getItemKey());
    }

    @Override
    public boolean requiresServerSideLayout() {
        return false;
    }

    @Override
    public String getBackendKey() {
        return "redirect";
    }

}
