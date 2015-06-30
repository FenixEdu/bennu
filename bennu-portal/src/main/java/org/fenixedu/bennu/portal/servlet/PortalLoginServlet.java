package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.I18N;

import com.google.common.base.Strings;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * 
 * Configurable servlet responsible for handling application logins.
 * 
 * As Bennu supports multiple authentication backends, this servlet can
 * be configured to present the user with the most suitable interface for
 * authentication, be it presenting a login page or redirecting to an external
 * service.
 * 
 * Out of the box, Bennu Portal supports CAS authentication (redirecting the user
 * to the correct CAS URL), and local authentication (via a themeable login page).
 * By default, the right one is chosen according to the system's configuration, but
 * can be manually overridden.
 * 
 * Additionally, this servlet supports specifying a callback URL, to which the user
 * should be redirected after authentication is successful. For security purposes,
 * this URL *MUST* start with the configured application URL.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@WebServlet("/login/*")
public class PortalLoginServlet extends HttpServlet {

    private static final long serialVersionUID = -4298321185506045304L;

    private static PortalLoginStrategy strategy =
            CoreConfiguration.casConfig().isCasEnabled() ? new CasLoginStrategy() : new LocalLoginStrategy();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String callback = req.getParameter("callback");
        if (!Strings.isNullOrEmpty(callback) && !callback.startsWith(CoreConfiguration.getConfiguration().applicationUrl())) {
            throw new IllegalArgumentException("Callback URL '" + callback + "' is invalid!");
        }
        strategy.showLoginPage(req, resp, callback);
    }

    /**
     * Override the configured {@link PortalLoginStrategy} with the provided one.
     * 
     * @param loginStrategy
     *            The new strategy to be used
     * @throws NullPointerException
     *             If the provided strategy is null
     */
    public static void setLoginStrategy(PortalLoginStrategy loginStrategy) {
        strategy = Objects.requireNonNull(loginStrategy);
    }

    /**
     * Login strategy that redirects the user to the configured CAS URL.
     * 
     * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
     *
     */
    private static class CasLoginStrategy implements PortalLoginStrategy {
        @Override
        public void showLoginPage(HttpServletRequest req, HttpServletResponse resp, String callback) throws IOException,
                ServletException {
            resp.sendRedirect(CoreConfiguration.casConfig().getCasLoginUrl(
                    Strings.isNullOrEmpty(callback) ? CoreConfiguration.casConfig().getCasServiceUrl() : callback));
        }
    }

    /**
     * Login strategy that shows a themeable login page.
     * 
     * If the selected theme contains a file 'login.html', it will be rendered to the user.
     * This rendering uses Twig, just like regular theme pages.
     * 
     * If the theme does not provide a custom login page, a default one will be shown.
     * 
     * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
     *
     */
    public static class LocalLoginStrategy implements PortalLoginStrategy {

        private volatile PebbleEngine engine;

        @Override
        public void showLoginPage(HttpServletRequest req, HttpServletResponse resp, String callback) throws IOException,
                ServletException {
            Map<String, Object> ctx = new HashMap<>();
            PortalConfiguration config = PortalConfiguration.getInstance();
            // Add relevant variables
            ctx.put("config", config);
            ctx.put("callback", callback);
            ctx.put("url", req.getRequestURI());
            ctx.put("currentLocale", I18N.getLocale());
            ctx.put("contextPath", req.getContextPath());
            ctx.put("locales", CoreConfiguration.supportedLocales());

            try {
                resp.setContentType("text/html;charset=UTF-8");
                PebbleTemplate template = getEngine(req).getTemplate(config.getTheme());
                template.evaluate(resp.getWriter(), ctx, I18N.getLocale());
            } catch (PebbleException e) {
                throw new IOException(e);
            }
        }

        private PebbleEngine getEngine(HttpServletRequest req) {
            if (engine != null) {
                return engine;
            }
            final ServletContext context = req.getServletContext();
            PebbleEngine pebble = new PebbleEngine(new ClasspathLoader() {
                @Override
                public Reader getReader(String themeName) throws LoaderException {
                    // Try to resolve the page from the theme...
                    InputStream stream = context.getResourceAsStream("/themes/" + themeName + "/login.html");
                    if (stream != null) {
                        return new InputStreamReader(stream, StandardCharsets.UTF_8);
                    } else {
                        // ... and fall back if none is provided.
                        return new InputStreamReader(context.getResourceAsStream("/bennu-portal/login.html"),
                                StandardCharsets.UTF_8);
                    }
                }
            });
            pebble.addExtension(new PortalExtension());
            if (BennuPortalConfiguration.getConfiguration().themeDevelopmentMode()) {
                pebble.setTemplateCache(null);
            }
            this.engine = pebble;
            return pebble;
        }

    }

    /**
     * Strategy for dealing with login page requests from the user.
     * 
     * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
     *
     */
    public static interface PortalLoginStrategy {

        /**
         * Present the user with a page which will allow her to identify herself with the system.
         * 
         * @param req
         *            The user's request
         * @param resp
         *            The response
         * @param callback
         *            The requested callback. May be null.
         * @throws IOException
         *             If an exception occurs when writing to the response
         * @throws ServletException
         *             If an exception occurs when writing to the response
         */
        public void showLoginPage(HttpServletRequest req, HttpServletResponse resp, String callback) throws IOException,
                ServletException;
    }

}
