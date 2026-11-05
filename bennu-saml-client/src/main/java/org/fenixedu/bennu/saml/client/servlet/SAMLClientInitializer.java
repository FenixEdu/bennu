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
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * Initializes the SAML client:
 *
 * <ul>
 * <li>Owns the module's {@link WebApplicationContext}, required by the statically registered
 * {@link DelegatingFilterProxy} (see the module's web-fragment). The context is published under a
 * module-owned attribute, so it never conflicts with an application-provided root
 * {@link WebApplicationContext}.</li>
 * <li>When SAML is disabled ({@code saml.enabled = false}), registers the pass-through
 * configuration only, so that requests to the SAML endpoints are served by the disabled-404 guard
 * and {@link #samlEngineAvailable()} is false.</li>
 * <li>When SAML is enabled ({@code saml.enabled = true}), the configuration must be complete and
 * valid: an enabled but unconfigured client ({@code saml.idp.metadata} missing) or a broken one
 * (unreachable IdP metadata, invalid SP credentials) fails the webapp startup — a broken SAML
 * configuration never comes up looking like a working webapp without SAML.</li>
 * <li>Registers the {@link SAMLLoginProvider} with the portal only when the SAML engine is actually
 * available.</li>
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

    /**
     * Active profile set on the module-owned Spring context (and only there), so that
     * {@link SamlModuleOwnedContextCondition} can tell it apart from application contexts: the
     * SAML configurations must never be processed by application component scans.
     */
    public static final String MODULE_OWNED_CONTEXT_PROFILE = "saml-module-owned-context";

    private static final Logger logger = LoggerFactory.getLogger(SAMLClientInitializer.class);

    private static volatile boolean engineAvailable = false;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        SAMLClientConfiguration.ConfigurationProperties config = SAMLClientConfiguration.getConfiguration();
        boolean enabled = Boolean.TRUE.equals(config.samlEnabled());

        if (enabled && !StringUtils.hasText(config.idpMetadata())) {
            // fail fast: an enabled but unconfigured SAML client is an invalid configuration
            throw new IllegalStateException("SAML client is enabled but saml.idp.metadata is not configured: the "
                    + "webapp cannot start with an invalid SAML configuration (set saml.idp.metadata or disable "
                    + "the SAML client with saml.enabled = false)");
        }
        if (enabled) {
            try {
                publishContext(servletContext, SAML2SecurityConfiguration.class);
                OpenSamlInitializationService.initialize();
                engineAvailable = true;
                PortalLoginServlet.registerProvider(new SAMLLoginProvider());
                logger.info("SAML client initialized (enabled)");
                return;
            } catch (RuntimeException e) {
                // fail the deployment: a broken SAML configuration (unreachable IdP metadata, invalid
                // SP credentials) must not come up as a webapp whose SAML login cannot work
                logger.error("SAML client failed to initialize: failing the webapp startup", e);
                throw e;
            }
        }
        publishDisabledContext(servletContext);
        logger.info("SAML client initialized (disabled)");
    }

    /**
     * Whether the SAML engine context was successfully booted. Only true when SAML is enabled and
     * its configuration is valid; {@link SAMLLoginProvider} consults it so that the portal never
     * offers a SAML login that cannot work.
     */
    public static boolean samlEngineAvailable() {
        return engineAvailable;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        engineAvailable = false;
        ServletContext servletContext = sce.getServletContext();
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext,
                WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (context instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) context).close();
        }
        servletContext.removeAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

    private void publishDisabledContext(ServletContext servletContext) {
        publishContext(servletContext, SAMLDisabledConfiguration.class);
    }

    private void publishContext(ServletContext servletContext, Class<?> configurationClass) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(servletContext);
        context.getEnvironment().setActiveProfiles(MODULE_OWNED_CONTEXT_PROFILE);
        context.register(configurationClass);
        try {
            context.refresh();
        } catch (RuntimeException e) {
            discardFailedContext(context, servletContext);
            throw e;
        }
        servletContext.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
    }

    /**
     * Best-effort cleanup after a failed context boot: release the beans already created and make
     * sure nothing is left published under the module attribute, so that a failed deployment cannot
     * leak the webapp classloader. Failures during the cleanup itself are logged and ignored — the
     * original boot failure is the one that matters, and it is rethrown by the caller.
     */
    private void discardFailedContext(AnnotationConfigWebApplicationContext context, ServletContext servletContext) {
        try {
            context.close();
        } catch (Throwable e) {
            logger.warn("Failed to close the SAML context after a failed boot", e);
        }
        try {
            servletContext.removeAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        } catch (Throwable e) {
            logger.warn("Failed to clear the SAML context attribute after a failed boot", e);
        }
    }
}
