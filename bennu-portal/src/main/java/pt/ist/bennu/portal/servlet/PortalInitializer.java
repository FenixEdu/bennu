package pt.ist.bennu.portal.servlet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
        logger.trace("Initialize");
        for (String themePath : sce.getServletContext().getResourcePaths("/themes/")) {
            themes.add(themePath.substring("/themes/".length(), themePath.length() - 1));
        }
        logger.info("Available Themes : " + Arrays.toString(themes.toArray()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    public static Set<String> getThemes() {
        return Collections.unmodifiableSet(themes);
    }
}
