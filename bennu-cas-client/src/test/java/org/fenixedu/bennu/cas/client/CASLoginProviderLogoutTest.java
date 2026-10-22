package org.fenixedu.bennu.cas.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

/**
 * Verifies the /logout hook of the CAS provider: only sessions authenticated through CAS (marked
 * with {@link CASLoginProvider#CAS_SESSION_ATTRIBUTE} at login time) are redirected to the CAS
 * server logout page; every other session is declined, leaving the plain local logout (and the
 * portal's own redirect) to the caller. This keeps CAS from claiming logouts of sessions owned
 * by other providers or the application itself.
 */
public class CASLoginProviderLogoutTest {

    private final CASLoginProvider provider = new CASLoginProvider();

    private static MockHttpServletRequest request() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/logout");
        request.setSession(new MockHttpSession());
        return request;
    }

    @Test
    public void redirectsToTheCasServerLogoutPageForACasSession() throws Exception {
        MockHttpServletRequest request = request();
        request.getSession().setAttribute(CASLoginProvider.CAS_SESSION_ATTRIBUTE, Boolean.TRUE);
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean handled = provider.logout(request, response);

        assertTrue("Expected the provider to claim the logout response", handled);
        assertEquals("https://cas.test.example/cas/logout", response.getRedirectedUrl());
    }

    @Test
    public void declinesForASessionAuthenticatedByAnotherMechanism() throws Exception {
        MockHttpServletRequest request = request(); // session without the CAS marker
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(provider.logout(request, response));
        assertNull(response.getRedirectedUrl());
    }

    @Test
    public void declinesWithoutASession() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/logout");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(provider.logout(request, response));
    }
}
