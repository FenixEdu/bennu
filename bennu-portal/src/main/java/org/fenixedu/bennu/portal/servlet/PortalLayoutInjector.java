package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.escaper.EscaperExtension;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

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
        engine = new PebbleEngine(new ClasspathLoader() {
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
        });
        // Disable auto-escaping
        engine.getExtension(EscaperExtension.class).setAutoEscaping(false);
        engine.addExtension(new PortalExtension(servletContext));

        if (BennuPortalConfiguration.getConfiguration().themeDevelopmentMode()) {
            logger.info("Theme Development Mode Enabled!");
            engine.setTemplateCache(null);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // Wrap the response so it may be later rewritten if necessary
        PortalResponseWrapper wrapper = new PortalResponseWrapper(response);

        chain.doFilter(request, wrapper);

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
