package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;

/**
 * Verifies that the single relying party registration is assembled from the configured IdP
 * metadata and the saml.* configuration keys (default configuration: SLO on, credentials set).
 */
public class BennuRelyingPartyRegistrationRepositoryTest {

    private BennuRelyingPartyRegistrationRepository repository;

    @Before
    public void setup() {
        repository = new BennuRelyingPartyRegistrationRepository();
        repository.setServletContext(new MockServletContext());
    }

    @Test
    public void resolvesOnlyTheConfiguredRegistrationId() {
        RelyingPartyRegistration registration = repository.findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);
        assertNotNull(registration);
        assertEquals(SAML2SecurityConfiguration.REGISTRATION_ID, registration.getRegistrationId());
        assertNull(repository.findByRegistrationId("other"));
    }

    @Test
    public void exposesTheRegistrationThroughIterable() {
        List<RelyingPartyRegistration> registrations = new ArrayList<>();
        repository.iterator().forEachRemaining(registrations::add);
        assertEquals(1, registrations.size());
        assertEquals(SAML2SecurityConfiguration.REGISTRATION_ID, registrations.get(0).getRegistrationId());
    }

    @Test
    public void spSideIsBuiltFromConfiguration() {
        RelyingPartyRegistration registration = repository.findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);

        // saml.sp.entityId is not set: defaults to the application URL
        assertEquals(TestSaml.SP_ENTITY_ID, registration.getEntityId());
        assertEquals(TestSaml.SP_ACS_URL, registration.getAssertionConsumerServiceLocation());
        assertEquals(Saml2MessageBinding.POST, registration.getAssertionConsumerServiceBinding());

        // SLO is enabled in the default test configuration
        assertEquals(TestSaml.SP_SLO_URL, registration.getSingleLogoutServiceLocation());
        assertEquals(TestSaml.SP_SLO_URL, registration.getSingleLogoutServiceResponseLocation());

        // the configured certificate/key become both the signing and the decryption credential
        assertEquals(1, registration.getSigningX509Credentials().size());
        assertEquals(1, registration.getDecryptionX509Credentials().size());
        X509Certificate signingCertificate = registration.getSigningX509Credentials().stream().findFirst()
                .map(Saml2X509Credential::getCertificate).orElse(null);
        assertNotNull(signingCertificate);
        assertEquals(TestSaml.spCertificate(), signingCertificate);
    }

    @Test
    public void idpSideIsBuiltFromMetadata() {
        RelyingPartyRegistration registration = repository.findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);

        assertEquals(TestSaml.IDP_ENTITY_ID, registration.getAssertingPartyDetails().getEntityId());
        assertEquals(TestSaml.IDP_SSO_REDIRECT_URL, registration.getAssertingPartyDetails().getSingleSignOnServiceLocation());
        assertEquals(Saml2MessageBinding.REDIRECT, registration.getAssertingPartyDetails().getSingleSignOnServiceBinding());
        assertEquals(TestSaml.IDP_SLO_REDIRECT_URL, registration.getAssertingPartyDetails().getSingleLogoutServiceLocation());

        X509Certificate verificationCertificate = registration.getAssertingPartyDetails()
                .getVerificationX509Credentials().stream().findFirst().map(Saml2X509Credential::getCertificate)
                .orElse(null);
        assertNotNull(verificationCertificate);
        assertEquals(TestSaml.idpCertificate(), verificationCertificate);
    }

    @Test
    public void contextPathIsIncludedInSpUrls() {
        MockServletContext servletContext = new MockServletContext();
        servletContext.setContextPath("/app");
        repository.setServletContext(servletContext);

        RelyingPartyRegistration registration = repository.findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);
        assertEquals(TestSaml.SP_ENTITY_ID + "/app", registration.getEntityId());
        assertEquals(TestSaml.SP_ENTITY_ID + "/app" + SAML2SecurityConfiguration.ASSERTION_CONSUMER_SERVICE_URL,
                registration.getAssertionConsumerServiceLocation());
        assertEquals(TestSaml.SP_ENTITY_ID + "/app" + SAML2SecurityConfiguration.LOGOUT_URL,
                registration.getSingleLogoutServiceLocation());
    }
}
