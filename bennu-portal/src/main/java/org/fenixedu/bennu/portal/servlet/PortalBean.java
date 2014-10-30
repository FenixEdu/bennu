package org.fenixedu.bennu.portal.servlet;

import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.json.adapters.AuthenticatedUserViewer;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.core.util.CoreConfiguration.ConfigurationProperties;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.I18N;

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
            builder.append("window.Bennu = ");
            builder.append(BennuRestResource.getBuilder().view(null, Void.class, AuthenticatedUserViewer.class)).append(
                    ";window.BennuPortal=window.Bennu;");
        }
        {
            builder.append("window.contextPath = '").append(ctxPath).append("';");
        }
        builder.append("</script>");
        return builder.toString();
    }

    public String toolkit() {
        StringBuilder builder = new StringBuilder(bennuPortal());
        builder.append("<script type=\"text/javascript\" src=\"").append(ctxPath)
                .append("/bennu-toolkit/js/toolkit.js\"></script>");
        builder.append("<link href=\"").append(ctxPath).append("/bennu-toolkit/css/toolkit.css\" rel=\"stylesheet\"/>");
        return builder.toString();
    }

    /**
     * Returns the current instance of {@link PortalConfiguration}, providing access to application configuration.
     * 
     * @return
     *         The {@link PortalConfiguration} instance.
     */
    public PortalConfiguration getConfiguration() {
        return PortalConfiguration.getInstance();
    }

    /**
     * Returns the current locale, as defined by {@link I18N#getLocale()}.
     * 
     * @return
     *         The current locale.
     */
    public Locale getLocale() {
        return I18N.getLocale();
    }

    /**
     * Returns all the configured locales for this application, as defined by {@link CoreConfiguration#supportedLocales()}.
     * 
     * @return
     *         All the configured locales for this application.
     */
    public Set<Locale> getSupportedLocales() {
        return CoreConfiguration.supportedLocales();
    }

    /**
     * Determines whether the application is in development mode.
     * 
     * @return
     *         Whether the application is in development mode.
     */
    public boolean getDevMode() {
        return CoreConfiguration.getConfiguration().developmentMode();
    }

    /**
     * Returns the current user, as determined by {@link Authenticate#getUser()}.
     * 
     * @return
     *         The current user.
     */
    public User getCurrentUser() {
        return Authenticate.getUser();
    }

    /**
     * Returns the selected functionality for the current request, as determined by
     * {@link BennuPortalDispatcher#getSelectedFunctionality(HttpServletRequest)}.
     * 
     * @param request
     *            The request for which to return the functionality.
     * @return
     *         The selected functionality.
     */
    public MenuFunctionality selectedFunctionality(HttpServletRequest request) {
        return BennuPortalDispatcher.getSelectedFunctionality(request);
    }

    /**
     * Retrieves the given key from the given bundle, as determined by {@link BundleUtil#getString(String, String, String...)}.
     * 
     * @param bundle
     *            The bundle for which to retrieve the message.
     * @param key
     *            The message's key.
     * @return
     *         The localized message.
     */
    public String message(String bundle, String key) {
        return BundleUtil.getString(bundle, key);
    }

    /**
     * Returns the application's configured URL, as defined by {@link ConfigurationProperties#applicationUrl()}.
     * 
     * @return
     *         The application's URL.
     */
    public String getApplicationUrl() {
        return CoreConfiguration.getConfiguration().applicationUrl();
    }
}
