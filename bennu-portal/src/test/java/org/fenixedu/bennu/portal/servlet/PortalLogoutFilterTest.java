package org.fenixedu.bennu.portal.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.portal.login.LoginProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

/**
 * Verifies the provider-aware /logout flow: providers get a best-effort logout hook, failures are
 * ignored, the local session is always terminated, and the portal redirect is skipped only when
 * a provider handled the response.
 */
public class PortalLogoutFilterTest {

    private final PortalLogoutFilter filter = new PortalLogoutFilter();

    @Before
    public void clearRegistry() throws Exception {
        Field field = PortalLoginServlet.class.getDeclaredField("providers");
        field.setAccessible(true);
        ((ConcurrentMap<?, ?>) field.get(null)).clear();
    }

    private static class FakeProvider implements LoginProvider {
        private final String key;
        private final Boolean claimResponse;
        private final RuntimeException failure;
        private boolean logoutCalled;

        FakeProvider(String key, Boolean claimResponse, RuntimeException failure) {
            this.key = key;
            this.claimResponse = claimResponse;
            this.failure = failure;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getName() {
            return this.key;
        }

        @Override
        public void showLogin(HttpServletRequest request, HttpServletResponse response, String callback) {
            // not used by the logout flow
        }

        @Override
        public boolean logout(HttpServletRequest request, HttpServletResponse response) {
            this.logoutCalled = true;
            if (this.failure != null) {
                throw this.failure;
            }
            if (Boolean.TRUE.equals(this.claimResponse)) {
                response.setHeader("X-Provider-Handled", "true");
                return true;
            }
            return false;
        }
    }

    private MockHttpServletRequest requestWithSession() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/logout");
        request.setContextPath("");
        request.setSession(new MockHttpSession());
        return request;
    }

    @Test
    public void logoutsWithoutProvidersRedirectsToTheRoot() throws Exception {
        MockHttpServletRequest request = requestWithSession();
        MockHttpSession session = (MockHttpSession) request.getSession();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, null);

        assertTrue("Expected the local session to be invalidated", session.isInvalid());
        assertEquals("/", response.getRedirectedUrl());
    }

    @Test
    public void nonClaimingProviderDoesNotChangeTheFlow() throws Exception {
        PortalLoginServlet.registerProvider(new FakeProvider("plain", false, null));
        MockHttpServletRequest request = requestWithSession();
        MockHttpSession session = (MockHttpSession) request.getSession();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, null);

        assertTrue(session.isInvalid());
        assertEquals("/", response.getRedirectedUrl());
    }

    @Test
    public void claimingProviderOwnsTheResponseButLocalLogoutStillHappens() throws Exception {
        PortalLoginServlet.registerProvider(new FakeProvider("claiming", true, null));
        MockHttpServletRequest request = requestWithSession();
        MockHttpSession session = (MockHttpSession) request.getSession();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, null);

        assertTrue(session.isInvalid());
        assertEquals("Expected the provider response to be preserved", "true",
                response.getHeader("X-Provider-Handled"));
        assertNull("Expected no portal redirect after a claiming provider", response.getRedirectedUrl());
    }

    @Test
    public void failingProviderIsIgnored() throws Exception {
        PortalLoginServlet.registerProvider(new FakeProvider("broken", null, new IllegalStateException("boom")));
        MockHttpServletRequest request = requestWithSession();
        MockHttpSession session = (MockHttpSession) request.getSession();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, null);

        assertTrue("Provider failure must not prevent the local logout", session.isInvalid());
        assertEquals("/", response.getRedirectedUrl());
    }

    @Test
    public void claimingProviderWinsOverAFailingOne() throws Exception {
        PortalLoginServlet.registerProvider(new FakeProvider("broken", null, new IllegalStateException("boom")));
        PortalLoginServlet.registerProvider(new FakeProvider("claiming", true, null));
        MockHttpServletRequest request = requestWithSession();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, null);

        assertEquals("true", response.getHeader("X-Provider-Handled"));
        assertNull(response.getRedirectedUrl());
    }
}
