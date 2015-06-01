package org.fenixedu.bennu.portal.servlet;

import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalBean.class);

    public PortalBean(ServletContext servletContext) {
        this.ctxPath = servletContext.getContextPath();
    }

    /**
     * Injects a Javascript context with contains the {@code Bennu} variable, which is a subset
     * of the Bennu Portal Data REST API, containing information about the configured locales, the current
     * locale, as well as some information regarding the currently logged user.
     * 
     * If also sets up the {@code contextPath} variable, which contains the configured context path of the
     * application.
     * 
     * @return
     *         A {@code <script>} tag containing the {@code Bennu} and {@code contextPath} variables.
     */
    public String bennuPortal() {
        StringBuilder builder = new StringBuilder();
        builder.append("<script>");
        {
            builder.append("window.Bennu = ");
            builder.append(BennuRestResource.getBuilder().view(null, Void.class, AuthenticatedUserViewer.class)).append(";");
        }
        {
            builder.append("window.contextPath = '").append(ctxPath).append("';");
        }
        builder.append("</script>");
        return builder.toString();
    }

    private static String DEBUG_TOOLKIT_TAG = null;
    private static String DEBUG_TOOLKIT_ANGULAR_TAG = null;

    private NodeList getFilesForId(String id) throws Exception {
        InputStream is = this.getClass().getResource("/META-INF/maven/org.fenixedu/bennu-toolkit/pom.xml").openStream();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = factory.newDocumentBuilder();
        Document doc = b.parse(is);

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();

        XPathExpression expr =
                xpath.compile("//project/build/plugins/plugin/executions/execution[id = '" + id
                        + "']/configuration/jsSourceFiles/jsSourceFile");
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        return (NodeList) result;

    }

    private String getDebugToolkit() {
        if (DEBUG_TOOLKIT_TAG == null) {
            try {
                NodeList nodes = getFilesForId("toolkit");
                StringBuilder builder = new StringBuilder();
                builder.append("<link href=\"").append(ctxPath).append("/bennu-toolkit/css/toolkit.css\" rel=\"stylesheet\"/>");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node currentItem = nodes.item(i);

                    builder.append("<script type=\"text/javascript\" src=\"").append(ctxPath).append("/bennu-toolkit/js/")
                            .append(currentItem.getTextContent()).append("\"></script>");
                }
                

                DEBUG_TOOLKIT_TAG = builder.toString();
            } catch (Exception e) {
                LOGGER.warn("Error resolving the portal POM, falling back into compiled toolkit");

                return getToolkit();
            }
        }

        return bennuPortal() + DEBUG_TOOLKIT_TAG;
    }

    private String getDebugToolkitAngular() {
        if (DEBUG_TOOLKIT_ANGULAR_TAG == null) {
            try {
                NodeList nodes = getFilesForId("toolkit-angular");
                StringBuilder builder = new StringBuilder();
                builder.append("<link href=\"").append(ctxPath).append("/bennu-toolkit/css/toolkit.css\" rel=\"stylesheet\"/>");
                builder.append("<script type=\"text/javascript\" src=\"").append(ctxPath)
                        .append("/bennu-portal/js/angular.min.js\"></script>");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node currentItem = nodes.item(i);

                    builder.append("<script type=\"text/javascript\" src=\"").append(ctxPath).append("/bennu-toolkit/js/")
                            .append(currentItem.getTextContent()).append("\"></script>");
                }
                

                DEBUG_TOOLKIT_ANGULAR_TAG = builder.toString();
            } catch (Exception e) {
                LOGGER.warn("Error resolving the portal POM, falling back into compiled toolkit");

                return getAngularToolkit();
            }
        }

        return bennuPortal() + DEBUG_TOOLKIT_ANGULAR_TAG;
    }

    private String getToolkit() {
        StringBuilder builder = new StringBuilder(bennuPortal());
        builder.append("<link href=\"").append(ctxPath).append("/bennu-toolkit/css/toolkit.css\" rel=\"stylesheet\"/>");
        builder.append("<script type=\"text/javascript\" src=\"").append(ctxPath)
                .append("/bennu-toolkit/js/toolkit.js\"></script>");
        return builder.toString();
    }

    private String getAngularToolkit() {
        StringBuilder builder = new StringBuilder(bennuPortal());
        builder.append("<link href=\"").append(ctxPath).append("/bennu-toolkit/css/toolkit.css\" rel=\"stylesheet\"/>");
        builder.append("<script type=\"text/javascript\" src=\"").append(ctxPath)
                .append("/bennu-portal/js/angular.min.js\"></script>");
        builder.append("<script type=\"text/javascript\" src=\"").append(ctxPath)
                .append("/bennu-toolkit/js/toolkit-angular.js\"></script>");
        return builder.toString();
    }

    public String toolkit() {
        if (CoreConfiguration.getConfiguration().developmentMode()) {
            return getDebugToolkit();
        } else {
            return getToolkit();
        }
    }

    public String angularToolkit() {
        if (CoreConfiguration.getConfiguration().developmentMode()) {
            return getDebugToolkitAngular();
        } else {
            return getAngularToolkit();
        }
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
