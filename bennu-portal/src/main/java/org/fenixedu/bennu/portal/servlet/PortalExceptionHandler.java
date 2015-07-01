package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.servlet.ExceptionHandlerFilter.ExceptionHandler;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * Custom {@link ExceptionHandler} that allows Portal themes to provide their own custom error pages.
 * 
 * If the current theme does not define an error page, a default is shown.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class PortalExceptionHandler implements ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(PortalExceptionHandler.class);

    private final PebbleEngine engine;

    public PortalExceptionHandler(final ServletContext context) {
        this(new ClasspathLoader() {
            @Override
            public Reader getReader(String themeName) throws LoaderException {
                // Try to resolve the page from the theme...
                InputStream stream = context.getResourceAsStream("/themes/" + themeName + "/500.html");
                if (stream != null) {
                    return new InputStreamReader(stream, StandardCharsets.UTF_8);
                } else {
                    // ... and fall back if none is provided.
                    return new InputStreamReader(context.getResourceAsStream("/bennu-portal/500.html"), StandardCharsets.UTF_8);
                }
            }
        });
    }

    protected PortalExceptionHandler(Loader loader) {
        this.engine = new PebbleEngine(loader);
        engine.addExtension(new PortalExtension());
        if (BennuPortalConfiguration.getConfiguration().themeDevelopmentMode()) {
            engine.setTemplateCache(null);
        }
    }

    @Override
    public boolean handle(ServletRequest request, ServletResponse response, Throwable exception) throws ServletException,
            IOException {
        if (response.isCommitted()) {
            return false;
        }
        response.reset();
        ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        HttpServletRequest req = (HttpServletRequest) request;
        logger.error("Request at " + req.getRequestURI() + " threw an exception: ", exception);

        Map<String, Object> ctx = new HashMap<>();
        PortalConfiguration config = PortalConfiguration.getInstance();
        ctx.put("loggedUser", Authenticate.getUser());
        ctx.put("config", config);
        ctx.put("contextPath", req.getContextPath());
        ctx.put("request", req);
        ctx.put("exception", exception);
        ctx.put("locale", I18N.getLocale());
        ctx.put("userAgent", req.getHeader("User-Agent"));
        ctx.put("referer", req.getHeader("Referer"));
        ctx.put("parameters", getParameters(req));
        ctx.put("attributes", getAttributes(req));
        ctx.put("functionality", BennuPortalDispatcher.getSelectedFunctionality(req));
        setExtraParameters(ctx, req, exception);

        StringWriter writer = new StringWriter(1024);
        exception.printStackTrace(new PrintWriter(writer));
        ctx.put("stackTrace", writer.toString());

        try {
            response.setContentType("text/html;charset=UTF-8");
            PebbleTemplate template = engine.getTemplate(config.getTheme());
            template.evaluate(response.getWriter(), ctx, I18N.getLocale());
            return true;
        } catch (PebbleException e) {
            throw new IOException(e);
        }
    }

    protected void setExtraParameters(Map<String, Object> ctx, HttpServletRequest req, Throwable exception) {
        // Do nothing by default
    }

    private Object getParameters(HttpServletRequest req) {
        Map<String, String> params = new TreeMap<>();
        Enumeration<String> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            params.put(name, String.join(" | ", req.getParameterValues(name)));
        }
        return params.entrySet();
    }

    private Object getAttributes(HttpServletRequest req) {
        Map<String, Object> attrs = new TreeMap<>();
        Enumeration<String> names = req.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            attrs.put(name, req.getAttribute(name));
        }
        return attrs.entrySet();
    }
}
