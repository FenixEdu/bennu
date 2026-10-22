package org.fenixedu.bennu.saml.client.variant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.fenixedu.bennu.saml.client.BennuRelyingPartyRegistrationRepository;
import org.fenixedu.bennu.saml.client.SAML2SecurityConfiguration;
import org.fenixedu.bennu.saml.client.TestSaml;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;

/**
 * Runs against configuration-variant.properties (saml.slo.enabled=false, no SP credentials):
 * verifies the registration is still built, without SLO endpoints or credentials.
 */
public class BennuRelyingPartyRegistrationRepositoryVariantTest {

    private RelyingPartyRegistration registration;

    @Before
    public void setup() {
        BennuRelyingPartyRegistrationRepository repository = new BennuRelyingPartyRegistrationRepository();
        repository.setServletContext(new MockServletContext());
        registration = repository.findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);
    }

    @Test
    public void registrationIsStillBuiltWithoutCredentialsOrSlo() {
        assertEquals(SAML2SecurityConfiguration.REGISTRATION_ID, registration.getRegistrationId());
        assertEquals(TestSaml.SP_ENTITY_ID, registration.getEntityId());
        assertEquals(TestSaml.SP_ACS_URL, registration.getAssertionConsumerServiceLocation());
        assertEquals(TestSaml.IDP_ENTITY_ID, registration.getAssertingPartyDetails().getEntityId());
    }

    @Test
    public void sloEndpointsAreNotAdvertisedWhenSloIsDisabled() {
        assertNull(registration.getSingleLogoutServiceLocation());
        assertNull(registration.getSingleLogoutServiceResponseLocation());
    }

    @Test
    public void noCredentialsAreConfigured() {
        assertTrue(registration.getSigningX509Credentials().isEmpty());
        assertTrue(registration.getDecryptionX509Credentials().isEmpty());
    }
}
