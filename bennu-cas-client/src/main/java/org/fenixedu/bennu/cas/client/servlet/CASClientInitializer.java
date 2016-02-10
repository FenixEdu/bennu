package org.fenixedu.bennu.cas.client.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.cas.client.CASLoginProvider;
import org.fenixedu.bennu.portal.servlet.PortalLoginServlet;

@WebListener
public class CASClientInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        PortalLoginServlet.registerProvider(new CASLoginProvider());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
