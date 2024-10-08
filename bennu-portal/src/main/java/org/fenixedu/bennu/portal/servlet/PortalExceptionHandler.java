package org.fenixedu.bennu.portal.servlet;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.servlet.ExceptionHandlerFilter.ExceptionHandler;
import org.fenixedu.bennu.portal.BennuPortalConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        }, context);
    }

    protected PortalExceptionHandler(Loader loader, ServletContext context) {
        this.engine = new Builder().loader(loader).extension(new PortalExtension(context))
                .cacheActive(!BennuPortalConfiguration.getConfiguration().themeDevelopmentMode()).build();
    }

    @Override
    public boolean handle(ServletRequest request, ServletResponse response, Throwable exception) throws ServletException,
            IOException {
        final String refId = getRefId();
        try {
            if (response.isCommitted()) {
                return false;
            }
            response.reset();
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            HttpServletRequest req = (HttpServletRequest) request;
            final String uri = req.getRequestURI();
            logger.error(refId + " - Request at " + req.getRequestURI() + " threw an exception: ", exception);
            final boolean isLoginAttempt = uri.indexOf("/api/cas-client/login") >= 0;
            if (isLoginAttempt && logger.isDebugEnabled()) {
                exception.printStackTrace();
            }

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
            if (isLoginAttempt && logger.isDebugEnabled()) {
                logger.debug(refId + " - calling get parameters.");
            }
            ctx.put("parameters", getParameters(req));
            if (isLoginAttempt && logger.isDebugEnabled()) {
                logger.debug(refId + " - calling get attributes.");
            }
            ctx.put("attributes", getAttributes(req));
            ctx.put("functionality", BennuPortalDispatcher.getSelectedFunctionality(req));
            setExtraParameters(ctx, req, exception);
            if (isLoginAttempt && logger.isDebugEnabled()) {
                logger.debug(refId + " - completed context map.");
            }

            StringWriter writer = new StringWriter(1024);
            exception.printStackTrace(new PrintWriter(writer));
            ctx.put("stackTrace", writer.toString());
            if (isLoginAttempt && logger.isDebugEnabled()) {
                logger.debug(refId + " - completed export of stacktrace.");
            }

            try {
                response.setContentType("text/html;charset=UTF-8");
                PebbleTemplate template = engine.getTemplate(config.getTheme());
                if (isLoginAttempt && logger.isDebugEnabled()) {
                    logger.debug(refId + " - using pebble theme " + config.getTheme());
                }
                template.evaluate(response.getWriter(), ctx, I18N.getLocale());
                if (isLoginAttempt && logger.isDebugEnabled()) {
                    logger.debug(refId + " - completed pebble template evaluation");
                }
                return true;
            } catch (final PebbleException e) {
                if (isLoginAttempt && logger.isDebugEnabled()) {
                    logger.debug(refId + " - error processing pebble tempalte.");
                    e.printStackTrace();
                }
                throw new IOException(e);
            }
        } catch (final Throwable t) {
            if (logger.isDebugEnabled()) {
                logger.debug(refId + " - error processing pebble tempalte.");
                t.printStackTrace();
            }
            throw new ServletException(t);
        }
    }

    private String getRefId() {
        final String uuid = UUID.randomUUID().toString();
        return uuid.substring(uuid.length() - 5);
    }

    protected void setExtraParameters(Map<String, Object> ctx, HttpServletRequest req, Throwable exception) {
        // Do nothing by default
    }

    private Object getParameters(HttpServletRequest req) {
        Map<String, String> params = new TreeMap<>();
        Enumeration<String> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            params.put(name.replace("{{","%7B%7B"),
                    String.join(" | ", req.getParameterValues(name)).replace("{{","%7B%7B"));
        }
        return params.entrySet();
    }

    private Object getAttributes(HttpServletRequest req) {
        Map<String, Object> attrs = new TreeMap<>();
        Enumeration<String> names = req.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            try {
                Object attribute = req.getAttribute(name);
                attrs.put(name, attribute != null ? attribute.toString() : "");
            } catch (Throwable t) {
                attrs.put(name, "Unable to retrieve attribute due to exception " + t.getLocalizedMessage());
            }
        }
        return attrs.entrySet();
    }
}
