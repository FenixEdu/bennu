package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Verifies the fallback of the /logout hook when the IdP metadata does not advertise a
 * SingleLogoutService endpoint (while {@code saml.slo.enabled = true}): the Spring handler
 * cannot produce a LogoutRequest and writes a bare 401, and the hook must decline instead of
 * claiming the response, so the portal performs its normal logout redirect.
 */
public class SAMLLoginProviderLogoutFallbackTest {

    /**
     * Overrides the registration with one whose IdP has no SLO endpoints, modelling IdP metadata
     * without a SingleLogoutService element.
     */
    @Configuration
    static class NoSloRegistrationConfiguration {

        @Bean
        @Primary
        BennuRelyingPartyRegistrationRepository noSloRegistrationRepository() {
            return new BennuRelyingPartyRegistrationRepository() {
                @Override
                public RelyingPartyRegistration findByRegistrationId(String registrationId) {
                    RelyingPartyRegistration registration = super.findByRegistrationId(registrationId);
                    // The SLO resolver reads the endpoints from the asserting-party (IdP)
                    // details; null both places to model metadata without SingleLogoutService
                    return RelyingPartyRegistration.withRelyingPartyRegistration(registration)
                            .assertingPartyDetails((assertingParty) -> assertingParty
                                    .singleLogoutServiceLocation(null)
                                    .singleLogoutServiceResponseLocation(null))
                            .singleLogoutServiceLocation(null)
                            .singleLogoutServiceResponseLocation(null)
                            .build();
                }
            };
        }
    }

    private static AnnotationConfigWebApplicationContext context;
    private final SAMLLoginProvider provider = new SAMLLoginProvider();

    @BeforeClass
    public static void boot() {
        context = SamlContextSupport.boot(SAML2SecurityConfiguration.class, NoSloRegistrationConfiguration.class);
    }

    @AfterClass
    public static void shutdown() {
        context.close();
    }

    @Test
    public void declinesWhenTheIdpMetadataHasNoSloEndpoint() throws Exception {
        MockHttpServletRequest request = SamlContextSupport.request(context, "GET", "/logout");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(BennuSecurityContextRepository.SAML_IDENTITY_SESSION_ATTRIBUTE,
                new BennuSecurityContextRepository.StoredSamlIdentity(TestSaml.NAME_ID_VALUE,
                        Collections.singletonList(TestSaml.SESSION_INDEX)));
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean handled = provider.logout(request, response);

        assertFalse("Expected the hook to decline when no logout request could be produced", handled);
        assertEquals("Expected the handler's error status to be preserved",
                HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertNull("Expected no redirect from the hook", response.getRedirectedUrl());
        assertNull("Expected no pending logout request cookie",
                response.getCookie(CookieSaml2LogoutRequestRepository.COOKIE_NAME));
    }
}
