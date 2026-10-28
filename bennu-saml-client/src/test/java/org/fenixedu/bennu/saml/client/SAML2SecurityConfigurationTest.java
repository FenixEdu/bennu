package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Boots the module-owned Spring context against a mocked servlet environment and drives the real
 * filter chain for the metadata and authentication-request endpoints, the ACS failure path, and
 * the full login round trip (asserting which provider is wired and which publisher receives the
 * authentication events).
 */
public class SAML2SecurityConfigurationTest {

    private static AnnotationConfigWebApplicationContext context;
    private static Filter filterChain;
    private static RecordingAuthenticationEventPublisher eventPublisher;

    @BeforeClass
    public static void boot() {
        context = SamlContextSupport.boot(RecordingEventPublisherConfiguration.class, SAML2SecurityConfiguration.class);
        filterChain = SamlContextSupport.filterChain(context);
        eventPublisher = context.getBean(RecordingAuthenticationEventPublisher.class);
    }

    @AfterClass
    public static void shutdown() {
        context.close();
    }

    @Test
    public void exposesTheSpringSecurityFilterChainAndTheRegistrationRepository() {
        assertTrue(context.getBean("springSecurityFilterChain") instanceof FilterChainProxy);
        assertNotNull(context.getBean(RelyingPartyRegistrationRepository.class));
    }

    @Test
    public void servesTheGeneratedMetadata() throws Exception {
        MockHttpServletRequest request = SamlContextSupport.request("GET", "/api/saml-client/metadata");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String content = response.getContentAsString();
        org.w3c.dom.Document document = TestSaml.parse(content);
        assertEquals(TestSaml.SP_ENTITY_ID, document.getDocumentElement().getAttribute("entityID"));
        assertTrue("Metadata should advertise the ACS",
                content.contains(SAML2SecurityConfiguration.ASSERTION_CONSUMER_SERVICE_URL));
        assertTrue("Metadata should advertise the SP signing certificate",
                content.contains(java.util.Base64.getEncoder().encodeToString(
                        TestSaml.spCertificate().getEncoded()).substring(0, 40)));
    }

    @Test
    public void redirectsTheAuthenticationRequestToTheIdp() throws Exception {
        MockHttpServletRequest request = SamlContextSupport.request("GET", "/saml2/authenticate/bennu");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        String location = response.getRedirectedUrl();
        assertTrue("Expected a redirect to the IdP SSO endpoint, got: " + location,
                location.startsWith(TestSaml.IDP_SSO_REDIRECT_URL));
        assertTrue("Expected a signed redirect (SigAlg)", location.contains("SigAlg="));
        assertTrue("Expected a redirect signature", location.contains("Signature="));
    }

    @Test
    public void theAuthenticationRequestEndpointKeepsTheCallbackInTheSession() throws Exception {
        // the callback travels in the session (written by the authentication-request customizer);
        // the RelayState stays at Spring's stock default, a random opaque value
        MockHttpServletRequest request = SamlContextSupport.request(context, "GET", "/saml2/authenticate/bennu");
        request.addParameter("callback", "https://saml-client.test.example/deep/link");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        org.springframework.mock.web.MockHttpSession session =
                (org.springframework.mock.web.MockHttpSession) request.getSession();
        assertEquals("https://saml-client.test.example/deep/link",
                session.getAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE));
        java.net.URI location = new java.net.URI(response.getRedirectedUrl());
        String relayState = queryParameters(location.getRawQuery()).get("RelayState");
        assertNotNull("Expected a RelayState on the redirect", relayState);
        assertTrue("RelayState is opaque", !"https://saml-client.test.example/deep/link".equals(relayState));
    }

    @Test
    public void anAuthenticationRequestWithoutCallbackClearsAStaleSessionCallback() throws Exception {
        org.springframework.mock.web.MockHttpSession session = new org.springframework.mock.web.MockHttpSession();
        session.setAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE,
                "https://saml-client.test.example/stale");
        MockHttpServletRequest request = SamlContextSupport.request(context, "GET", "/saml2/authenticate/bennu");
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        assertNull("The stale callback is cleared at the start of every new SP-initiated login",
                session.getAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE));
    }

    @Test
    public void authenticationRequestIsAProperAuthnRequest() throws Exception {
        MockHttpServletRequest request = SamlContextSupport.request("GET", "/saml2/authenticate/bennu");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        java.net.URI location = new java.net.URI(response.getRedirectedUrl());
        String samlRequest = TestSaml.inflateAndBase64Decode(queryParameters(location.getRawQuery()).get("SAMLRequest"));
        org.w3c.dom.Document document = TestSaml.parse(samlRequest);
        assertEquals("AuthnRequest", document.getDocumentElement().getLocalName());
        assertEquals(TestSaml.SP_ENTITY_ID,
                document.getDocumentElement()
                        .getElementsByTagNameNS("*", "Issuer").item(0).getTextContent());
    }

    @Test
    public void assertionConsumerRejectsGarbageResponses() throws Exception {
        MockHttpServletRequest request = SamlContextSupport.request("POST", "/api/saml-client/returnFromSAML");
        request.addParameter("SAMLResponse", "not-base64-saml");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        assertEquals("/login?login_failed=true", response.getRedirectedUrl());
    }

    @Test
    public void assertionConsumerRejectsMissingResponses() throws Exception {
        MockHttpServletRequest request = SamlContextSupport.request("POST", "/api/saml-client/returnFromSAML");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        assertEquals("/login?login_failed=true", response.getRedirectedUrl());
    }

    @Test
    public void samlLoginPublishesItsSuccessEventOnTheApplicationContextPublisher() throws Exception {
        // the response carries no username attribute, so the success handler exits before the
        // Bennu login (which needs the Fenix domain, unavailable in this mocked environment); the
        // success event is published by the wired ProviderManager before the handler runs either way
        int successesBefore = eventPublisher.successes.size();

        MockHttpServletResponse response = roundTrip(TestSaml.samlResponseXmlWithoutUsernameAttribute("_respWired", null));

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        assertEquals("/login?login_failed=true", response.getRedirectedUrl());
        assertTrue("Expected a success event on the application context's publisher",
                eventPublisher.successes.size() > successesBefore);
        Authentication event = eventPublisher.successes.get(successesBefore);
        assertTrue("Expected the success event to carry the SAML authentication",
                event instanceof Saml2Authentication);
        assertEquals(TestSaml.NAME_ID_VALUE, event.getName());
        assertTrue("Expected the event to carry the request details (client IP, session id)",
                event.getDetails() instanceof org.springframework.security.web.authentication.WebAuthenticationDetails);
    }

    @Test
    public void theWiredProviderRejectsInResponseToMismatchesTheStockOneWouldAccept() throws Exception {
        // the response answers the request the chain itself generated, but carries a foreign
        // InResponseTo: the stock OpenSAML 3 provider has no InResponseTo binding and would
        // authenticate it, so the failure (and its published event) proves the Bennu provider is
        // the one wired into the chain
        int failuresBefore = eventPublisher.failures.size();

        MockHttpServletResponse response = roundTrip(TestSaml.samlResponseXml("_respWiredMismatch", "_otherRequest"));

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        assertEquals("/login?login_failed=true", response.getRedirectedUrl());
        assertTrue("Expected a failure event on the application context's publisher",
                eventPublisher.failures.size() > failuresBefore);
    }

    /**
     * Drives the SP-initiated login round trip through the real filter chain: fetches the signed
     * AuthnRequest redirect (which stores the request in the session), then posts the given
     * response from the test IdP against the same session.
     */
    private MockHttpServletResponse roundTrip(String responseXml) throws Exception {
        MockHttpSession session = new MockHttpSession();

        MockHttpServletRequest authnRequest = SamlContextSupport.request(context, "GET", "/saml2/authenticate/bennu");
        authnRequest.setSession(session);
        MockHttpServletResponse authnResponse = new MockHttpServletResponse();
        filterChain.doFilter(authnRequest, authnResponse, new MockFilterChain());
        assertEquals(HttpServletResponse.SC_FOUND, authnResponse.getStatus());

        return postToAcs(session, responseXml);
    }

    private MockHttpServletResponse postToAcs(MockHttpSession session, String responseXml) throws Exception {
        MockHttpServletRequest acsRequest = SamlContextSupport.request(context, "POST",
                SAML2SecurityConfiguration.ASSERTION_CONSUMER_SERVICE_URL);
        acsRequest.setSession(session);
        acsRequest.addParameter("SAMLResponse", java.util.Base64.getEncoder()
                .encodeToString(responseXml.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        MockHttpServletResponse acsResponse = new MockHttpServletResponse();
        filterChain.doFilter(acsRequest, acsResponse, new MockFilterChain());
        return acsResponse;
    }

    @Configuration
    static class RecordingEventPublisherConfiguration {

        @Bean
        RecordingAuthenticationEventPublisher recordingAuthenticationEventPublisher() {
            return new RecordingAuthenticationEventPublisher();
        }
    }

    /**
     * An app-defined publisher bean, to assert that the wired manager resolves the application
     * context's own {@link AuthenticationEventPublisher} instead of always creating a fresh
     * default one.
     */
    static class RecordingAuthenticationEventPublisher implements AuthenticationEventPublisher {

        final List<Authentication> successes = new ArrayList<>();
        final List<Authentication> failures = new ArrayList<>();

        @Override
        public void publishAuthenticationSuccess(Authentication authentication) {
            this.successes.add(authentication);
        }

        @Override
        public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
            this.failures.add(authentication);
        }
    }

    private static java.util.Map<String, String> queryParameters(String rawQuery) {
        java.util.Map<String, String> parameters = new java.util.HashMap<>();
        for (String pair : rawQuery.split("&")) {
            int equals = pair.indexOf('=');
            String name = java.net.URLDecoder.decode(pair.substring(0, equals),
                    java.nio.charset.StandardCharsets.UTF_8);
            String value = java.net.URLDecoder.decode(pair.substring(equals + 1),
                    java.nio.charset.StandardCharsets.UTF_8);
            parameters.put(name, value);
        }
        return parameters;
    }
}
