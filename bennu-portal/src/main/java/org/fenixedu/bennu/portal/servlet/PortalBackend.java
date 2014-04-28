package org.fenixedu.bennu.portal.servlet;

import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.model.Functionality;

/**
 * Portal backends represent specific presentation frameworks that acknowledge the presence of bennu-portal.
 * 
 * <p>
 * Backends are required to have unique keys, which are used to determine the backend associated to a {@link MenuFunctionality}.
 * </p>
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public interface PortalBackend {

    /**
     * Returns the {@link SemanticURLHandler} for this backend.
     */
    public SemanticURLHandler getSemanticURLHandler();

    /**
     * Returns whether functionalities provider by this backend require the
     * server to wrap the response in the configured layout.
     */
    public boolean requiresServerSideLayout();

    /**
     * Returns the unique key used to identify this backend.
     * 
     * This key MUST match the provider key of {@link Functionality}s declared by this backend.
     */
    public String getBackendKey();

}
