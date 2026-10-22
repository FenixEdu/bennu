package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Verifies the /logout hook of the SAML provider: with SLO enabled and a stored SAML identity,
 * it initiates the Single Logout redirect (and stores the pending request in a cookie); without
 * a SAML identity it declines, letting the portal perform the plain local logout.
 */
public class SAMLLoginProviderLogoutTest {

    private static AnnotationConfigWebApplicationContext context;
    private final SAMLLoginProvider provider = new SAMLLoginProvider();

    @BeforeClass
    public static void boot() {
        context = SamlContextSupport.boot(SAML2SecurityConfiguration.class);
    }

    @AfterClass
    public static void shutdown() {
        context.close();
    }

    private MockHttpServletRequest requestWithIdentity() {
        MockHttpServletRequest request = SamlContextSupport.request(context, "GET", "/logout");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(BennuSecurityContextRepository.SAML_IDENTITY_SESSION_ATTRIBUTE,
                new BennuSecurityContextRepository.StoredSamlIdentity(TestSaml.NAME_ID_VALUE,
                        Collections.singletonList(TestSaml.SESSION_INDEX)));
        request.setSession(session);
        return request;
    }

    @Test
    public void initiatesSloWhenTheSessionHoldsASamlIdentity() throws Exception {
        MockHttpServletRequest request = requestWithIdentity();
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean handled = provider.logout(request, response);

        assertTrue("Expected the provider to claim the logout response", handled);
        String location = response.getRedirectedUrl();
        assertNotNull("Expected a redirect to the IdP SLO endpoint", location);
        assertTrue("Expected a redirect to the IdP SLO endpoint, got: " + location,
                location.startsWith(TestSaml.IDP_SLO_REDIRECT_URL));
        assertNotNull("Expected the pending logout request cookie",
                response.getCookie(CookieSaml2LogoutRequestRepository.COOKIE_NAME));
        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
    }

    @Test
    public void declinesWhenTheSessionHasNoSamlIdentity() throws Exception {
        MockHttpServletRequest request = SamlContextSupport.request(context, "GET", "/logout");
        request.setSession(new MockHttpSession());
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(provider.logout(request, response));
        assertNull(response.getRedirectedUrl());
    }

    @Test
    public void declinesWithoutASession() throws Exception {
        MockHttpServletRequest request = SamlContextSupport.request(context, "GET", "/logout");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(provider.logout(request, response));
    }
}
