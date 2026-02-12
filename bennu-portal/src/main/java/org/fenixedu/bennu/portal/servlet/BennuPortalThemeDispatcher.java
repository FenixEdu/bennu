package org.fenixedu.bennu.portal.servlet;

import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import pt.ist.fenixframework.FenixFramework;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
public class BennuPortalThemeDispatcher implements Filter {

    private static String theme = "default";

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
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;
        final String path = trim(request.getRequestURI());

        if (path.startsWith("api/theme/")) {
            FenixFramework.atomic(() -> {
                theme = PortalConfiguration.getInstance().getTheme();
            });

            final String resource = path.substring("api/theme/".length());
            final String finalPath = "/themes/" + theme + "/" + resource;
            final RequestDispatcher dispatcher = req.getServletContext().getRequestDispatcher(finalPath);
            dispatcher.forward(req, resp);
        } else {
            chain.doFilter(request, response);
        }
    }

    private MenuFunctionality selectFunctionality(String path) {
        String[] parts = path.split("/");
        MenuContainer root = PortalConfiguration.getInstance().getMenu();
        return root.findFunctionalityWithPath(parts);
    }

    private MenuFunctionality findFunctionality(String path) {
        String[] parts = path.split("/");
        MenuContainer root = PortalConfiguration.getInstance().getMenu();
        return root.findFunctionalityWithPathWithoutAccessControl(parts);
    }

    /**
     * Returns the selected {@link MenuFunctionality} from the given request, or null if no functionality is selected.
     * 
     * @param request
     *            The request for which to retrieve the functionality
     * @return
     *         The functionality associated with the given request. {@code null} if no functionality is mapped.
     * @throws NullPointerException
     *             If {@code request} is {@code null}
     */
    public static MenuFunctionality getSelectedFunctionality(HttpServletRequest request) {
        return (MenuFunctionality) request.getAttribute("PORTAL_SELECTED_FUNCTIONALITY");
    }

    /**
     * Selects the given {@link MenuFunctionality} for the given request.
     * 
     * @param request
     *            The request for which to select the given functionality
     * @param functionality
     *            The functionality to select
     * @throws NullPointerException
     *             If {@code request} is {@code null}
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
