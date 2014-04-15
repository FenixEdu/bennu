package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;

/**
 * Filter that translates semantic URLs to {@link MenuFunctionality}s. Requests whose path matches that of a functionality are
 * handled by the functionality's {@link SemanticURLHandler}, whereas any other request will simply continue the chain. In this
 * scenario, the underlying presentation technology should attempt to resolve the {@link MenuFunctionality}.
 * 
 * <p>
 * Implementation note: This filter is programmatically registered, to ensure that it is the last filter in the chain to be
 * executed. This ensures that any filter declared in other modules are given a chance to run.
 * </p>
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public class BennuPortalDispatcher implements Filter {

    private int contextPathLength = -1;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        contextPathLength = filterConfig.getServletContext().getContextPath().length();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String path = trim(request.getRequestURI());
        MenuFunctionality functionality = selectFunctionality(path);

        if (functionality != null) {
            selectFunctionality(request, functionality);
            PortalBackendRegistry.getPortalBackend(functionality.getProvider()).getSemanticURLHandler()
                    .handleRequest(functionality, request, response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    private MenuFunctionality selectFunctionality(String path) {
        String[] parts = path.split("/");
        MenuContainer root = PortalConfiguration.getInstance().getMenu();
        return root.findFunctionalityWithPath(parts);
    }

    /**
     * Returns the selected {@link MenuFunctionality} from the given request, or null if no functionality is selected.
     */
    public static MenuFunctionality getSelectedFunctionality(HttpServletRequest request) {
        return (MenuFunctionality) request.getAttribute("PORTAL_SELECTED_FUNCTIONALITY");
    }

    /**
     * Selects the given {@link MenuFunctionality} for the given request.
     */
    public static void selectFunctionality(HttpServletRequest request, MenuFunctionality functionality) {
        request.setAttribute("PORTAL_SELECTED_FUNCTIONALITY", functionality);
    }

    /**
     * Trims the given string, removing all trailing and leading slashes. It also removes the context path.
     */
    private String trim(String value) {
        int len = value.length();
        int st = contextPathLength;
        char[] val = value.toCharArray();

        while ((st < len) && (val[st] == '/')) {
            st++;
        }
        while ((st < len) && (val[len - 1] == '/')) {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }

}
