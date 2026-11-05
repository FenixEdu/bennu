package org.fenixedu.bennu.saml.client.unconfigured;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import javax.servlet.ServletContextEvent;

import org.fenixedu.bennu.saml.client.SAMLClientConfiguration;
import org.fenixedu.bennu.saml.client.SAMLLoginProvider;
import org.fenixedu.bennu.saml.client.servlet.SAMLClientInitializer;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Runs against configuration-unconfigured.properties (saml.enabled = true, no other saml.* key):
 * an enabled but unconfigured SAML client is an invalid configuration — the initializer must fail
 * the deployment with a clear error instead of starting a webapp whose SAML login cannot work.
 */
public class SAMLClientInitializerUnconfiguredTest {

    @Test
    public void enabledButUnconfiguredFailsTheDeployment() {
        SAMLClientConfiguration.ConfigurationProperties config = SAMLClientConfiguration.getConfiguration();
        assertTrue("Expected saml.enabled = true in this configuration", config.samlEnabled());
        assertNull("Expected no saml.idp.metadata in this configuration", config.idpMetadata());

        MockServletContext servletContext = new MockServletContext();
        SAMLClientInitializer initializer = new SAMLClientInitializer();

        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> initializer.contextInitialized(new ServletContextEvent(servletContext)));
        assertTrue("Expected the failure to point at the missing saml.idp.metadata",
                failure.getMessage().contains("saml.idp.metadata"));

        assertNull("Expected nothing to be published under the module attribute",
                WebApplicationContextUtils.getWebApplicationContext(servletContext,
                        SAMLClientInitializer.WEB_APPLICATION_CONTEXT_ATTRIBUTE));
        assertFalse("Expected the engine to be marked unavailable", SAMLClientInitializer.samlEngineAvailable());
        assertFalse("Expected the SAML login provider to be disabled", new SAMLLoginProvider().isEnabled());
    }
}
