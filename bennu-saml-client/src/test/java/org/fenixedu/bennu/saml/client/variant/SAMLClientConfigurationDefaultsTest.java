package org.fenixedu.bennu.saml.client.variant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.fenixedu.bennu.saml.client.SAMLClientConfiguration;
import org.junit.Test;

/**
 * Runs against configuration-variant.properties, where every optional saml.* key is left unset:
 * asserts the documented default values and the null return of the optional keys.
 */
public class SAMLClientConfigurationDefaultsTest {

    private SAMLClientConfiguration.ConfigurationProperties config() {
        return SAMLClientConfiguration.getConfiguration();
    }

    @Test
    public void behaviorDefaults() {
        assertEquals(Boolean.FALSE, config().samlEnabled());
        assertEquals("eduPersonPrincipalName", config().usernameAttribute());
        assertEquals(Boolean.TRUE, config().usernameStripDomain());
        assertEquals(Boolean.TRUE, config().autoCreateUsers());
        assertEquals(Boolean.FALSE, config().sloEnabled());
        // unsigned SLO messages are only tolerated when explicitly opted out (SimpleSAMLphp
        // test environments); the default requires signatures
        assertEquals(Boolean.TRUE, config().sloRequireSignedMessages());
        assertEquals("/login?logout", config().logoutSuccessUrl());
        assertEquals("/login?login_failed=true", config().loginFailedUrl());
        assertEquals("technical", config().contactType());
        assertEquals("en", config().organizationLang());
    }

    @Test
    public void optionalKeysDefaultToNull() {
        assertNull(config().spEntityId());
        assertNull(config().spCertificate());
        assertNull(config().spPrivateKey());
        assertNull(config().spPrivateKeyPassword());
        assertNull(config().organizationName());
        assertNull(config().organizationDisplayName());
        assertNull(config().organizationUrl());
        assertNull(config().serviceName());
        assertNull(config().contactGivenName());
        assertNull(config().contactSurname());
        assertNull(config().contactEmail());
    }

    @Test
    public void configuredKeysAreRead() {
        assertEquals("classpath:idp-metadata.xml", config().idpMetadata());
    }
}
