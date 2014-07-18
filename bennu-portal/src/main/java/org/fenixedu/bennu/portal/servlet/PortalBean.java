package org.fenixedu.bennu.portal.servlet;

import javax.servlet.ServletContext;

import org.fenixedu.bennu.core.json.adapters.AuthenticatedUserViewer;
import org.fenixedu.bennu.core.rest.BennuRestResource;

/**
 * The goal of this bean is to allow easy injection of Bennu Portal variables in JSP pages.
 * 
 * Refer to each individual method for its documentation.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class PortalBean {

    private final String ctxPath;

    public PortalBean(ServletContext servletContext) {
        this.ctxPath = servletContext.getContextPath();
    }

    /**
     * Injects a Javascript context with contains the {@code BennuPortal} variable, which is a subset
     * of the Bennu Portal Data REST API, containing information about the configured locales, the current
     * locale, as well as some information regarding the currently logged user.
     * 
     * If also sets up the {@code contextPath} variable, which contains the configured context path of the
     * application.
     * 
     * @return
     *         A {@code <script>} tag containing the {@code BennuPortal} and {@code contextPath} variables.
     */
    public String bennuPortal() {
        StringBuilder builder = new StringBuilder();
        builder.append("<script>");
        {
            builder.append("window.BennuPortal = ");
            builder.append(BennuRestResource.getBuilder().view(null, Void.class, AuthenticatedUserViewer.class)).append(";");
        }
        {
            builder.append("window.contextPath = '").append(ctxPath).append("';");
        }
        builder.append("</script>");
        return builder.toString();
    }
}
