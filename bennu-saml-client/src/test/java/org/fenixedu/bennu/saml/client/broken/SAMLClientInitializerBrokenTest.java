package org.fenixedu.bennu.saml.client.broken;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
 * Runs against configuration-broken.properties (saml.enabled = true, IdP metadata present, invalid
 * SP private key): the engine boot fails while building the filter chain — the initializer must
 * fail the deployment instead of starting a webapp whose SAML endpoints are silently dead.
 */
public class SAMLClientInitializerBrokenTest {

    @Test
    public void enabledButBrokenFailsTheDeployment() {
        SAMLClientConfiguration.ConfigurationProperties config = SAMLClientConfiguration.getConfiguration();
        assertTrue("Expected saml.enabled = true in this configuration", config.samlEnabled());
        assertNotNull("Expected saml.idp.metadata to be set in this configuration", config.idpMetadata());
        assertNotNull("Expected a (broken) saml.sp.private-key in this configuration", config.spPrivateKey());

        MockServletContext servletContext = new MockServletContext();
        SAMLClientInitializer initializer = new SAMLClientInitializer();

        RuntimeException failure = assertThrows(RuntimeException.class,
                () -> initializer.contextInitialized(new ServletContextEvent(servletContext)));
        assertTrue("Expected the failure to carry the invalid SP private key in its cause chain",
                chainContains(failure, "Could not parse saml.sp.private-key"));

        assertNull("Expected nothing to be published under the module attribute",
                WebApplicationContextUtils.getWebApplicationContext(servletContext,
                        SAMLClientInitializer.WEB_APPLICATION_CONTEXT_ATTRIBUTE));
        assertFalse("Expected the engine to be marked unavailable", SAMLClientInitializer.samlEngineAvailable());
        assertFalse("Expected the SAML login provider to be disabled", new SAMLLoginProvider().isEnabled());
    }

    private static boolean chainContains(Throwable failure, String marker) {
        for (Throwable t = failure; t != null; t = t.getCause()) {
            String message = t.getMessage();
            if (message != null && message.contains(marker)) {
                return true;
            }
        }
        return false;
    }
}
