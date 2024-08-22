package org.fenixedu.bennu.portal.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.qubit.terra.portal.domain.menus.MenuVisibility;
import org.fenixedu.bennu.core.servlet.ExceptionHandlerFilter;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.fenixframework.Atomic;

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

        migrateMenus();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private void migrateMenus() {
        /**
         * This logic can be removed after there is a deploy in all instances and visible field in MenuItem is removed
         * #qubIT-Omnis-6137
         *
         */
        migrateMenuItemVisibility(PortalConfiguration.getInstance().getMenu());
    }

    private void registerBuiltinPortalBackends() {
        PortalBackendRegistry.registerPortalBackend(new RedirectPortalBackend());
        PortalBackendRegistry.registerPortalBackend(new ForwarderPortalBackend());
    }

    private void migrateMenuItemVisibility(MenuItem item) {
        if (item == null) {
            return;
        }

        if (item.getItemVisibility() == null) {
            item.setItemVisibility(item.getVisible() ? MenuVisibility.ALL : MenuVisibility.INVISIBLE);
        }

        Set<MenuItem> children = item.isMenuContainer() ? item.getAsMenuContainer().getOrderedChild() : Set.of();
        for (MenuItem child : children) {
            migrateMenuItemVisibility(child);
        }
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
