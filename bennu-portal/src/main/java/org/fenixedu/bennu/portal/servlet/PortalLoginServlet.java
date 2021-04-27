package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.i18n.I18NFilter;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.portal.login.LoginProvider;
import org.fenixedu.commons.i18n.I18N;

import com.google.common.base.Strings;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
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
        this.engine = new Builder().extension(new PortalExtension(context)).loader(new ClasspathLoader() {
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
        }).cacheActive(!BennuPortalConfiguration.getConfiguration().themeDevelopmentMode()).build();
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
        Set<Locale> supportedLocales = CoreConfiguration.supportedLocales();
        Locale currentLocale = I18N.getLocale();

        if (Boolean.TRUE.equals(config.getDetectBrowserLocalInLoginPage())) {
            currentLocale = selectLocaleToUse(req.getLocale(), supportedLocales);
            I18N.setLocale(req.getSession(), currentLocale);
            I18NFilter.updateLocale(currentLocale, req, resp);
        }

        ctx.put("config", config);
        ctx.put("callback", callback);
        ctx.put("url", req.getRequestURI());
        ctx.put("currentLocale", currentLocale);
        ctx.put("contextPath", req.getContextPath());
        ctx.put("locales", supportedLocales);
        ctx.put("providers", providers);
        ctx.put("localLogin", localLogin);
        ctx.put("loginPath", PortalConfiguration.getInstance().getLoginPath());
        ctx.put("recoveryLinkPath", PortalConfiguration.getInstance().getRecoveryLinkPath());
        ctx.put("signUpPath", PortalConfiguration.getInstance().getSignUpPath());

        try {
            resp.setContentType("text/html;charset=UTF-8");
            PebbleTemplate template = engine.getTemplate(config.getTheme());
            template.evaluate(resp.getWriter(), ctx, currentLocale);
        } catch (PebbleException e) {
            throw new IOException(e);
        }
    }

    private Locale selectLocaleToUse(final Locale browserLocale, Set<Locale> supportedLocales) {
        if (supportedLocales.contains(browserLocale)) {
            return browserLocale;
        }

        Predicate<? super Locale> filterLocaleWithSameLanguageAndCountry =
                l -> l.getLanguage().equals(browserLocale.getLanguage()) && l.getCountry().equals(browserLocale.getLanguage());
        Predicate<? super Locale> filterLocaleWithSameLanguage = l -> l.getLanguage().equals(browserLocale.getLanguage());

        return supportedLocales.stream().filter(filterLocaleWithSameLanguageAndCountry).findFirst().orElseGet(
                () -> supportedLocales.stream().filter(filterLocaleWithSameLanguage).findFirst().orElse(Locale.getDefault()));

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
        if (Strings.isNullOrEmpty(callback)) {
            return true;
        }

        try {
            final URL callbackURL = new URL(callback);
            final URL applicationURL = new URL(CoreConfiguration.getConfiguration().applicationUrl());
            return callbackURL.getHost().equals(applicationURL.getHost())
                    && callbackURL.getPath().startsWith(applicationURL.getPath());
        } catch (MalformedURLException e) {
            //malformed urls are not accepted as callbacks
            return false;
        }

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

    private static final ConcurrentMap<String, LoginProvider> providers = new ConcurrentHashMap<>();

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
        return providers.values().stream().filter(LoginProvider::isEnabled).collect(Collectors.toList());
    }

}
