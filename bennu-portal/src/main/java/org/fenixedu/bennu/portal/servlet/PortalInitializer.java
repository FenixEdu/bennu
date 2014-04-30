package org.fenixedu.bennu.portal.servlet;

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
                themes.add(themePath.substring("/themes/".length(), themePath.length() - 1));
            }
        }
        logger.info("Available Themes : " + Arrays.toString(themes.toArray()));

        registerBuiltinPortalBackends();

        // Install Bennu Portal Dispatcher. It must be programmatically registered, so that is runs after EVERY other filter
        FilterRegistration registration = sce.getServletContext().addFilter("BennuPortalDispatcher", BennuPortalDispatcher.class);
        registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
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
}
