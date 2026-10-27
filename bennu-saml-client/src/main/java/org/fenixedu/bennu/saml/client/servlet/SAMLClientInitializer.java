package org.fenixedu.bennu.saml.client.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.portal.servlet.PortalLoginServlet;
import org.fenixedu.bennu.saml.client.SAML2SecurityConfiguration;
import org.fenixedu.bennu.saml.client.SAMLClientConfiguration;
import org.fenixedu.bennu.saml.client.SAMLDisabledConfiguration;
import org.fenixedu.bennu.saml.client.SAMLLoginProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.saml2.core.OpenSamlInitializationService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * Initializes the SAML client:
 *
 * <ul>
 * <li>Registers the {@link SAMLLoginProvider} with the portal.</li>
 * <li>Owns the module's {@link WebApplicationContext}, required by the statically registered
 * {@link DelegatingFilterProxy} (see the module's web-fragment). The context is published under a
 * module-owned attribute, so it never conflicts with an application-provided root
 * {@link WebApplicationContext}.</li>
 * <li>When SAML is disabled ({@code saml.enabled = false}), registers the pass-through
 * configuration only, so that requests to the SAML endpoints are served by the disabled-404
 * guard.</li>
 * </ul>
 */
@WebListener
public class SAMLClientInitializer implements ServletContextListener {

    /**
     * The module-owned servlet context attribute under which the SAML {@link WebApplicationContext}
     * is published. Referenced by the {@code contextAttribute} init-param of the
     * {@link DelegatingFilterProxy} declared in the module's web-fragment.
     */
    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE =
            "org.fenixedu.bennu.saml.client.WEB_APPLICATION_CONTEXT_ATTRIBUTE";

    private static final Logger logger = LoggerFactory.getLogger(SAMLClientInitializer.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        PortalLoginServlet.registerProvider(new SAMLLoginProvider());

        ServletContext servletContext = sce.getServletContext();
        boolean enabled = Boolean.TRUE.equals(SAMLClientConfiguration.getConfiguration().samlEnabled());

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(servletContext);
        context.register(enabled ? SAML2SecurityConfiguration.class : SAMLDisabledConfiguration.class);
        servletContext.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
        context.refresh();
        OpenSamlInitializationService.initialize();
        logger.info("SAML client initialized ({})", enabled ? "enabled" : "disabled");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext,
                WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (context instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) context).close();
        }
        servletContext.removeAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }
}
