package pt.ist.bennu.portal.rest;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.bennu.core.rest.serializer.JsonAwareResource;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.portal.domain.MenuItem;

@WebListener
public class PortalInitializer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(PortalInitializer.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.trace("Initialize");
        JsonAwareResource.setDefault(MenuItem.class, MenuItemAdapter.class);
        JsonAwareResource.setDefault(VirtualHost.class, HostAdapter.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
