package org.fenixedu.bennu.saml.client.servlet;

import org.fenixedu.bennu.portal.servlet.PortalLoginServlet;
import org.fenixedu.bennu.saml.client.SAMLLoginProvider;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SAMLClientInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        PortalLoginServlet.registerProvider(new SAMLLoginProvider());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
