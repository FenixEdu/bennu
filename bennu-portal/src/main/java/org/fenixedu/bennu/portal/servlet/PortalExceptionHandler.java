package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.servlets.ExceptionHandlerFilter.ExceptionHandler;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
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
        this.engine = new PebbleEngine(new ClasspathLoader() {
            @Override
            public Reader getReader(String themeName) throws LoaderException {
                // Try to resolve the page from the theme...
                InputStream stream = context.getResourceAsStream("/themes/" + themeName + "/500.html");
                if (stream != null) {
                    return new InputStreamReader(stream);
                } else {
                    // ... and fall back if none is provided.
                    return new InputStreamReader(context.getResourceAsStream("/bennu-portal/500.html"));
                }
            }
        });
        engine.addExtension(new PortalExtension());
    }

    @Override
    public boolean handle(ServletRequest request, ServletResponse response, Throwable exception) throws ServletException,
            IOException {
        if (response.isCommitted()) {
            return false;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        logger.error("Request at " + req.getRequestURI() + " threw an exception: ", exception);

        Map<String, Object> ctx = new HashMap<>();
        PortalConfiguration config = PortalConfiguration.getInstance();
        ctx.put("loggedUser", Authenticate.getUser());
        ctx.put("config", config);
        ctx.put("contextPath", req.getContextPath());
        ctx.put("url", req.getRequestURI());
        ctx.put("exception", exception);
        ctx.put("stackTrace", Arrays.asList(exception.getStackTrace()));

        try {
            PebbleTemplate template = engine.getTemplate(config.getTheme());
            template.evaluate(response.getWriter(), ctx, I18N.getLocale());
            return true;
        } catch (PebbleException e) {
            throw new IOException(e);
        }
    }
}
