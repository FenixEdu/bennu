package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.core.servlets.ExceptionHandlerFilter;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class PortalInitializer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(PortalInitializer.class);
    private static final Set<String> themes = new HashSet<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Collection<String> themePaths = sce.getServletContext().getResourcePaths("/themes/");
        if (themePaths != null) {
            for (String themePath : themePaths) {
                try (InputStream stream = sce.getServletContext().getResourceAsStream(themePath + "/default.html")) {
                    if (stream != null) {
                        themes.add(themePath.substring("/themes/".length(), themePath.length() - 1));
                    }
                } catch (IOException e) {
                    logger.warn("Could not detect default layout for theme " + themePath, e);
                }
            }
        }
        logger.info("Available Themes : " + Arrays.toString(themes.toArray()));

        registerBuiltinPortalBackends();

        // Install Bennu Portal Dispatcher. It must be programmatically registered, so that is runs after EVERY other filter
        FilterRegistration registration = sce.getServletContext().addFilter("BennuPortalDispatcher", BennuPortalDispatcher.class);
        registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        if (CoreConfiguration.getConfiguration().developmentMode()) {
            ExceptionHandlerFilter.setExceptionHandler(new PortalDevModeExceptionHandler(sce.getServletContext()));
        } else {
            ExceptionHandlerFilter.setExceptionHandler(new PortalExceptionHandler(sce.getServletContext()));
        }

        sce.getServletContext().setAttribute("portal", new PortalBean(sce.getServletContext()));
    }

    private void registerBuiltinPortalBackends() {
        PortalBackendRegistry.registerPortalBackend(new RedirectPortalBackend());
        PortalBackendRegistry.registerPortalBackend(new ForwarderPortalBackend());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    public static Set<String> getThemes() {
        return Collections.unmodifiableSet(themes);
    }

    public static boolean isThemeAvailable(String theme) {
        return themes.contains(theme);
    }
}
