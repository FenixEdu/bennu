package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.portal.login.LoginProvider;
import org.fenixedu.commons.i18n.I18N;

import com.google.common.base.Strings;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * Servlet responsible for exposing the various {@link LoginProvider}s to the end user, as a way for him to login into the
 * application.
 * 
 * This servlet shows to the user a login page that shows him a login form (if local login is enabled), or the option to choose an
 * alternative provider with which he can log in.
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

    private PebbleEngine engine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        final ServletContext context = config.getServletContext();
        engine = new PebbleEngine(new ClasspathLoader() {
            @Override
            public Reader getReader(String themeName) throws LoaderException {
                // Try to resolve the page from the theme...
                InputStream stream = context.getResourceAsStream("/themes/" + themeName + "/login.html");
                if (stream != null) {
                    return new InputStreamReader(stream, StandardCharsets.UTF_8);
                } else {
                    // ... and fall back if none is provided.
                    return new InputStreamReader(context.getResourceAsStream("/bennu-portal/login.html"), StandardCharsets.UTF_8);
                }
            }
        });
        engine.addExtension(new PortalExtension(context));
        if (BennuPortalConfiguration.getConfiguration().themeDevelopmentMode()) {
            engine.setTemplateCache(null);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String callback = req.getParameter("callback");
        if (!validateCallback(callback)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid callback URL");
            return;
        }
        boolean localLogin = CoreConfiguration.getConfiguration().localLoginEnabled();
        Collection<LoginProvider> providers = providers();

        // If there is only one login option, show it right away
        if (!localLogin && providers.size() == 1) {
            providers.iterator().next().showLogin(req, resp, callback);
            return;
        }

        Map<String, Object> ctx = new HashMap<>();
        PortalConfiguration config = PortalConfiguration.getInstance();
        // Add relevant variables
        ctx.put("config", config);
        ctx.put("callback", callback);
        ctx.put("url", req.getRequestURI());
        ctx.put("currentLocale", I18N.getLocale());
        ctx.put("contextPath", req.getContextPath());
        ctx.put("locales", CoreConfiguration.supportedLocales());
        ctx.put("providers", providers);
        ctx.put("localLogin", localLogin);

        try {
            resp.setContentType("text/html;charset=UTF-8");
            PebbleTemplate template = engine.getTemplate(config.getTheme());
            template.evaluate(resp.getWriter(), ctx, I18N.getLocale());
        } catch (PebbleException e) {
            throw new IOException(e);
        }
    }

    /**
     * Validates that the provided callback is valid, i.e., it is either not provided, or is a URL internal to the application.
     * 
     * @param callback
     *            The callback to validate. May be {@code null}
     * @return
     *         Whether the provided callback is valid
     */
    public static boolean validateCallback(String callback) {
        return Strings.isNullOrEmpty(callback) || callback.startsWith(CoreConfiguration.getConfiguration().applicationUrl());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String callback = req.getParameter("callback");
        if (!validateCallback(callback)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid callback URL");
            return;
        }

        LoginProvider provider = providerFor(req.getPathInfo());
        if (provider == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unrecognized Login Provider");
        } else {
            provider.showLogin(req, resp, callback);
        }
    }

    private LoginProvider providerFor(String pathInfo) {
        return pathInfo == null ? null : providers.get(pathInfo.replaceFirst("/", ""));
    }

    @Override
    public void destroy() {
        engine = null;
    }

    private static final Map<String, LoginProvider> providers = new HashMap<>();

    /**
     * Registers the given provider.
     * 
     * @param provider
     *            The provider to register
     * @throws NullPointerException
     *             If the given provider is {@code null}
     * @throws IllegalArgumentException
     *             If another provider with the same key is already registered
     */
    public static void registerProvider(LoginProvider provider) {
        if (providers.containsKey(provider.getKey())) {
            throw new IllegalArgumentException("Another provider with key " + provider.getKey() + " already exists");
        }
        providers.put(provider.getKey(), provider);
    }

    private static Collection<LoginProvider> providers() {
        return Collections.unmodifiableCollection(providers.values());
    }

}
