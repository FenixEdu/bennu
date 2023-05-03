package org.fenixedu.bennu.portal.servlet;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.fenixedu.bennu.alerts.Alert;
import org.fenixedu.bennu.alerts.AlertType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PersistentAlertMessage;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.fenixframework.FenixFramework;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Filter that injects layout for {@link MenuFunctionality}s that require it.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@WebFilter("/*")
public class PortalLayoutInjector implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(PortalLayoutInjector.class);

    private static final String SKIP_LAYOUT_INJECTION = "$$SKIP_LAYOUT_INJECTION$$";

    private PebbleEngine engine;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        final ServletContext servletContext = filterConfig.getServletContext();
        this.engine = new Builder().extension(new PortalExtension(servletContext)).loader(new ClasspathLoader() {
            @Override
            public Reader getReader(String templateName) throws LoaderException {
                // Try loading the specified template...
                InputStream stream = servletContext.getResourceAsStream("/themes/" + templateName + ".html");
                if (stream != null) {
                    return new InputStreamReader(stream, StandardCharsets.UTF_8);
                } else {
                    // ... fallback to default if it doesn't exist
                    logger.warn("Could not find template named {}, falling back to default!", templateName);
                    return new InputStreamReader(servletContext.getResourceAsStream("/themes/"
                            + PortalConfiguration.getInstance().getTheme() + "/default.html"), StandardCharsets.UTF_8);
                }
            }
        }).cacheActive(!BennuPortalConfiguration.getConfiguration().themeDevelopmentMode()).build();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // Wrap the response so it may be later rewritten if necessary
        PortalResponseWrapper wrapper = new PortalResponseWrapper(response);
        chain.doFilter(request, wrapper);
        Alert.flush(request, response);

        final User user = Authenticate.getUser();
        if (user != null) {
            final Locale locale = user.getProfile().getPreferredLocale();
            user.getPersistentAlertMessageUserViewCountSet().stream()
                    .map(count -> count.getPersistentAlertMessage())
                    .filter(alert -> cleanup(alert) != null)
                    .forEach(alert -> {
                        final LocalizedString ls = alert.getMessage();
                        final String message = locale == null ? ls.getContent() : ls.getContent(locale);
                        if (alert.getType() == AlertType.SUCCESS) {
                            Alert.success(request, message);
                        } else if (alert.getType() == AlertType.INFO) {
                            Alert.info(request, message);
                        } else if (alert.getType() == AlertType.WARNING) {
                            Alert.warning(request, message);
                        } else if (alert.getType() == AlertType.DANGER) {
                            Alert.danger(request, message);
                        }
                    });
        }

        MenuFunctionality functionality = BennuPortalDispatcher.getSelectedFunctionality(request);
        if (functionality != null && wrapper.hasData() && request.getAttribute(SKIP_LAYOUT_INJECTION) == null) {
            PortalBackend backend = PortalBackendRegistry.getPortalBackend(functionality.getProvider());
            if (backend.requiresServerSideLayout()) {
                String body = wrapper.getContent();
                try {
                    PortalConfiguration config = PortalConfiguration.getInstance();
                    Map<String, Object> ctx = new HashMap<>();
                    List<MenuItem> path = functionality.getPathFromRoot();
                    ctx.put("loggedUser", Authenticate.getUser());
                    ctx.put("body", body);
                    ctx.put("functionality", functionality);
                    ctx.put("config", config);
                    ctx.put("topLevelMenu", config.getMenu().getUserMenuStream());
                    ctx.put("contextPath", request.getContextPath());
                    ctx.put("devMode", CoreConfiguration.getConfiguration().developmentMode());
                    ctx.put("pathFromRoot", path);
                    ctx.put("selectedTopLevel", path.get(0));
                    ctx.put("locales", CoreConfiguration.supportedLocales());
                    ctx.put("currentLocale", I18N.getLocale());
                    ctx.put("alerts", Alert.getAlertsAsJson(request, response));

                    PebbleTemplate template = engine.getTemplate(config.getTheme() + "/" + functionality.resolveLayout());
                    template.evaluate(response.getWriter(), ctx, I18N.getLocale());
                } catch (PebbleException e) {
                    throw new ServletException("Could not render template!", e);
                }
            } else {
                wrapper.flushBuffer();
            }
        } else {
            wrapper.flushBuffer();
        }
    }

    private static PersistentAlertMessage cleanup(final PersistentAlertMessage alert) {
        if (alert.getHideAfterDateTime() == null || alert.getHideAfterDateTime().isAfterNow()) {
            return alert;
        }
        new Thread(() -> FenixFramework.atomic(() -> {
            if (alert.getHideAfterDateTime() != null && alert.getHideAfterDateTime().isBeforeNow()) {
                alert.delete();
            }
            Bennu.getInstance().getPersistentAlertMessageSet().stream()
                    .filter(pam -> pam.getPersistentAlertMessageUserViewCountSet().isEmpty()
                            || (pam.getHideAfterDateTime() != null && pam.getHideAfterDateTime().isBeforeNow()))
                    .forEach(PersistentAlertMessage::delete);
        })).start();
        return null;
    }

    /**
     * Requests that layout injection be skipped on the given request
     * 
     * @param request
     *            The request for which layouting should be skipped
     * @throws NullPointerException
     *             If the given {@code request} is {@code null}
     */
    public static void skipLayoutOn(HttpServletRequest request) {
        request.setAttribute(SKIP_LAYOUT_INJECTION, SKIP_LAYOUT_INJECTION);
    }

    @Override
    public void destroy() {

    }

}
