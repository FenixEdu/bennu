package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;

/**
 * Verifies the username-extraction and callback-redirect logic of the login success handler
 * (the pure parts; the Bennu login itself requires the Fenix domain and is covered by the
 * manual Phase 7 validation).
 */
public class BennuAuthenticationSuccessHandlerTest {

    private final BennuAuthenticationSuccessHandler handler = new BennuAuthenticationSuccessHandler();

    /**
     * A configuration proxy delegating to the real one, with the given overrides. The
     * ConfigurationInvocationHandler values are cached per JVM, so variants are expressed through
     * this proxy instead of configuration changes.
     */
    static SAMLClientConfiguration.ConfigurationProperties config(Map<String, Object> overrides) {
        SAMLClientConfiguration.ConfigurationProperties real = SAMLClientConfiguration.getConfiguration();
        return (SAMLClientConfiguration.ConfigurationProperties) Proxy.newProxyInstance(
                BennuAuthenticationSuccessHandlerTest.class.getClassLoader(),
                new Class<?>[] { SAMLClientConfiguration.ConfigurationProperties.class },
                (proxy, method, args) -> {
                    if (overrides.containsKey(method.getName())) {
                        return overrides.get(method.getName());
                    }
                    try {
                        return method.invoke(real, args);
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
    }

    private Saml2AuthenticatedPrincipal principal(Map<String, Object> attributes) {
        Map<String, java.util.List<Object>> attributeMap = new HashMap<>();
        attributes.forEach((name, value) -> attributeMap.put(name, Collections.singletonList(value)));
        return new DefaultSaml2AuthenticatedPrincipal(TestSaml.NAME_ID_VALUE, attributeMap,
                Collections.singletonList(TestSaml.SESSION_INDEX));
    }

    @Test
    public void extractsTheUsernameFromTheConfiguredAttribute() {
        Saml2AuthenticatedPrincipal principal = principal(Collections.singletonMap(TestSaml.USERNAME_ATTRIBUTE,
                TestSaml.USERNAME_VALUE));
        assertEquals(TestSaml.STRIPPED_USERNAME, handler.extractUsername(principal, config(Collections.emptyMap())));
    }

    @Test
    public void aTargetShapedRelayStateBeatsTheSessionCallback() {
        // a shaped RelayState can only come from the IdP (SP-initiated logins carry a random UUID)
        org.springframework.mock.web.MockHttpSession session = new org.springframework.mock.web.MockHttpSession();
        session.setAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE, "/from-session");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        request.setParameter("RelayState", "https://idp.example/target");

        assertEquals("https://idp.example/target", handler.resolveCallback(request));
        assertNull("The session callback is consumed", session
                .getAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE));
    }

    @Test
    public void aUuidRelayStateLosesToTheSessionCallback() {
        // Spring's stock resolver sends a random UUID: it is not a target, so the session callback
        // (an SP-initiated login) wins
        org.springframework.mock.web.MockHttpSession session = new org.springframework.mock.web.MockHttpSession();
        session.setAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE, "/from-session");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        request.setParameter("RelayState", "7e2f9a6c-0b1d-4c3e-8f2a-9b8c7d6e5f4a");

        assertEquals("/from-session", handler.resolveCallback(request));
    }

    @Test
    public void returnsNullForAUuidRelayStateWithoutSessionCallback() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("RelayState", "7e2f9a6c-0b1d-4c3e-8f2a-9b8c7d6e5f4a");

        assertNull(handler.resolveCallback(request));
    }

    @Test
    public void consumesTheSessionCallbackWhenTheLoginFailsEarly() throws Exception {
        // no username attribute: the handler redirects to the failure URL before logging in
        org.springframework.mock.web.MockHttpSession session = new org.springframework.mock.web.MockHttpSession();
        session.setAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE, "/from-session");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        org.springframework.security.saml2.provider.service.authentication.Saml2Authentication authentication =
                new org.springframework.security.saml2.provider.service.authentication.Saml2Authentication(
                        principal(Collections.emptyMap()), "<Response/>", Collections.emptyList());

        handler.onAuthenticationSuccess(request, new org.springframework.mock.web.MockHttpServletResponse(),
                authentication);

        assertNull("A failed login must not leave its callback behind",
                session.getAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE));
    }

    @Test
    public void returnsNullWithoutSessionCallbackOrRelayState() {
        assertNull(handler.resolveCallback(new MockHttpServletRequest()));
    }

    @Test
    public void keepsTheDomainWhenStripDomainIsDisabled() {
        Saml2AuthenticatedPrincipal principal = principal(Collections.singletonMap(TestSaml.USERNAME_ATTRIBUTE,
                TestSaml.USERNAME_VALUE));
        assertEquals(TestSaml.USERNAME_VALUE,
                handler.extractUsername(principal, config(Collections.singletonMap("usernameStripDomain", false))));
    }

    @Test
    public void usesTheNameIdWhenConfigured() {
        Saml2AuthenticatedPrincipal principal = principal(Collections.emptyMap());
        assertEquals(TestSaml.NAME_ID_VALUE,
                handler.extractUsername(principal, config(Collections.singletonMap("usernameAttribute", "NameID"))));
    }

    @Test
    public void returnsNullWhenTheAttributeIsMissing() {
        Saml2AuthenticatedPrincipal principal = principal(Collections.singletonMap("mail", "user@test.example"));
        assertNull(handler.extractUsername(principal, config(Collections.emptyMap())));
    }

    @Test
    public void redirectsRelativeCallbacksToTheContextPath() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/app");
        assertEquals("/app/some/deep/link",
                handler.redirectTarget(request, "/some/deep/link"));
    }

    @Test
    public void redirectsValidAbsoluteCallbacks() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("");
        assertEquals("https://saml-client.test.example/somewhere",
                handler.redirectTarget(request, "https://saml-client.test.example/somewhere"));
    }

    @Test
    public void discardsInvalidAbsoluteCallbacks() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("");
        assertEquals("https://saml-client.test.example",
                handler.redirectTarget(request, "https://evil.example/phishing"));
    }

    @Test
    public void discardsProtocolRelativeCallbacks() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("");
        // "//host" is protocol-relative: with an empty context path it would redirect off-site
        assertEquals("https://saml-client.test.example",
                handler.redirectTarget(request, "//evil.example/phishing"));
    }

    @Test
    public void discardsBackslashProtocolRelativeCallbacks() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("");
        // "/\host" is normalized to "//host" by some browsers
        assertEquals("https://saml-client.test.example",
                handler.redirectTarget(request, "/\\evil.example/phishing"));
    }

    @Test
    public void discardsTabStrippingCallbacks() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("");
        // browsers strip ASCII tab/newline anywhere in a URL before parsing, which would turn
        // "/\t//host" into a protocol-relative "//host"
        assertEquals("https://saml-client.test.example",
                handler.redirectTarget(request, "/\t//evil.example/phishing"));
        assertEquals("https://saml-client.test.example",
                handler.redirectTarget(request, "/\n//evil.example/phishing"));
        assertEquals("https://saml-client.test.example",
                handler.redirectTarget(request, "/\r//evil.example/phishing"));
    }

    @Test
    public void discardsControlCharacterCallbacks() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("");
        assertEquals("https://saml-client.test.example",
                handler.redirectTarget(request, "/dash\u0000board"));
        assertEquals("https://saml-client.test.example",
                handler.redirectTarget(request, "/dashboard\u007F"));
    }

    @Test
    public void fallsBackToTheApplicationRootWithoutACallback() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("");
        assertEquals("https://saml-client.test.example", handler.redirectTarget(request, null));
        assertEquals("https://saml-client.test.example", handler.redirectTarget(request, "  "));
    }
}
