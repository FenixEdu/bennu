package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Boots the module-owned Spring context against a mocked servlet environment and drives the real
 * filter chain for the metadata and authentication-request endpoints, plus the ACS failure path.
 */
public class SAML2SecurityConfigurationTest {

    private static AnnotationConfigWebApplicationContext context;
    private static Filter filterChain;

    @BeforeClass
    public static void boot() {
        context = SamlContextSupport.boot(SAML2SecurityConfiguration.class);
        filterChain = SamlContextSupport.filterChain(context);
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
    public void authenticationRequestCarriesTheCallbackThroughRelayState() throws Exception {
        MockHttpServletRequest request = SamlContextSupport.request("GET", "/saml2/authenticate/bennu");
        request.addParameter("callback", "https://saml-client.test.example/deep/link");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        java.net.URI location = new java.net.URI(response.getRedirectedUrl());
        java.util.Map<String, String> parameters = queryParameters(location.getRawQuery());
        assertEquals("https://saml-client.test.example/deep/link", parameters.get("RelayState"));
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
