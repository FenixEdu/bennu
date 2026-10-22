package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.context.HttpRequestResponseHolder;

/**
 * Verifies the read-only bridge between Bennu's session and the SAML engine: it reconstructs the
 * SAML authentication for the Single Logout machinery and never writes anything to the session.
 */
public class BennuSecurityContextRepositoryTest {

    private final BennuSecurityContextRepository repository = new BennuSecurityContextRepository();

    private MockHttpServletRequest requestWithIdentity() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(BennuSecurityContextRepository.SAML_IDENTITY_SESSION_ATTRIBUTE,
                new BennuSecurityContextRepository.StoredSamlIdentity(TestSaml.NAME_ID_VALUE,
                        Collections.singletonList(TestSaml.SESSION_INDEX)));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        return request;
    }

    @Test
    public void emptyContextWithoutASession() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertFalse(repository.containsContext(request));
        SecurityContext context = repository.loadContext(request).get();
        assertNull(context.getAuthentication());
    }

    @Test
    public void loadsTheStoredSamlIdentity() {
        MockHttpServletRequest request = requestWithIdentity();

        assertTrue(repository.containsContext(request));
        SecurityContext context = repository.loadContext(request).get();

        assertEquals(Saml2Authentication.class, context.getAuthentication().getClass());
        Saml2Authentication authentication = (Saml2Authentication) context.getAuthentication();
        assertEquals(TestSaml.NAME_ID_VALUE, authentication.getName());

        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
        assertEquals(SAML2SecurityConfiguration.REGISTRATION_ID, principal.getRelyingPartyRegistrationId());
        assertEquals(Collections.singletonList(TestSaml.SESSION_INDEX), principal.getSessionIndexes());
    }

    @Test
    public void loadContextThroughTheRequestResponseHolder() {
        MockHttpServletRequest request = requestWithIdentity();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, new MockHttpServletResponse());
        SecurityContext context = repository.loadContext(holder);
        assertEquals(TestSaml.NAME_ID_VALUE, context.getAuthentication().getName());
    }

    @Test
    public void saveContextNeverWritesToTheSession() {
        MockHttpServletRequest request = requestWithIdentity();
        MockHttpSession session = (MockHttpSession) request.getSession();

        repository.saveContext(new SecurityContextImpl(), request, new MockHttpServletResponse());

        assertFalse(java.util.Collections.list(session.getAttributeNames())
                .contains("SPRING_SECURITY_CONTEXT"));
        assertEquals(1, java.util.Collections.list(session.getAttributeNames()).size());
    }

    @Test
    public void containsContextIsFalseWithoutTheIdentityAttribute() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());
        assertFalse(repository.containsContext(request));
        assertNull(repository.loadContext(request).get().getAuthentication());
    }
}
