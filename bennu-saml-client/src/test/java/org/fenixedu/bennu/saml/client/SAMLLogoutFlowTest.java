package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.saml2.provider.service.authentication.logout.Saml2LogoutRequest;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.w3c.dom.Document;

/**
 * Drives the full Single Logout flows through the real filter chain: SP-initiated logout,
 * IdP-initiated logout requests and the concluding LogoutResponse. The pending logout request
 * travels in a short-lived cookie (CookieSaml2LogoutRequestRepository) so that it survives the
 * local session termination performed by BennuLogoutHandler.
 */
public class SAMLLogoutFlowTest {

    private static AnnotationConfigWebApplicationContext context;
    private static javax.servlet.Filter filterChain;

    @BeforeClass
    public static void boot() {
        context = SamlContextSupport.boot(SAML2SecurityConfiguration.class);
        filterChain = SamlContextSupport.filterChain(context);
    }

    @AfterClass
    public static void shutdown() {
        context.close();
    }

    private static MockHttpServletRequest requestWithIdentity(String method, String uri) {
        MockHttpServletRequest request = SamlContextSupport.request(method, uri);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(BennuSecurityContextRepository.SAML_IDENTITY_SESSION_ATTRIBUTE,
                new BennuSecurityContextRepository.StoredSamlIdentity(TestSaml.NAME_ID_VALUE,
                        Collections.singletonList(TestSaml.SESSION_INDEX)));
        request.setSession(session);
        return request;
    }

    private static Saml2LogoutRequest loadPendingLogoutRequest(MockHttpServletResponse response) {
        javax.servlet.http.Cookie responseCookie = response
                .getCookie(CookieSaml2LogoutRequestRepository.COOKIE_NAME);
        assertNotNull("Expected the pending logout request to be stored in a cookie", responseCookie);
        MockHttpServletRequest carrier = new MockHttpServletRequest();
        carrier.setContextPath("");
        carrier.setCookies(responseCookie);
        return new CookieSaml2LogoutRequestRepository(context.getBean(BennuRelyingPartyRegistrationRepository.class))
                .loadLogoutRequest(carrier);
    }

    private static Map<String, String> queryParameters(String rawQuery) {
        Map<String, String> parameters = new HashMap<>();
        for (String pair : rawQuery.split("&")) {
            int equals = pair.indexOf('=');
            parameters.put(decode(pair.substring(0, equals)), decode(pair.substring(equals + 1)));
        }
        return parameters;
    }

    /**
     * Percent-decoding that keeps literal '+' intact (the engine writes base64 values with '+' as
     * a legal query character, so decoding them as spaces would corrupt them).
     */
    private static String decode(String value) {
        return java.net.URLDecoder.decode(value.replace("+", "%2B"), java.nio.charset.StandardCharsets.UTF_8);
    }

    @Test
    public void spInitiatedLogoutRedirectsToTheIdpAndInvalidatesTheSession() throws Exception {
        MockHttpServletRequest request = requestWithIdentity("POST", "/api/saml-client/logout");
        MockHttpSession session = (MockHttpSession) request.getSession();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        String location = response.getRedirectedUrl();
        assertTrue("Expected a redirect to the IdP SLO endpoint, got: " + location,
                location.startsWith(TestSaml.IDP_SLO_REDIRECT_URL));

        Map<String, String> parameters = queryParameters(new java.net.URI(location).getRawQuery());
        String logoutRequestXml = TestSaml.inflateAndBase64Decode(parameters.get("SAMLRequest"));
        Document document = TestSaml.parse(logoutRequestXml);
        assertEquals("LogoutRequest", document.getDocumentElement().getLocalName());
        assertEquals(TestSaml.NAME_ID_VALUE,
                document.getDocumentElement().getElementsByTagNameNS("*", "NameID").item(0).getTextContent());
        assertEquals(TestSaml.SESSION_INDEX,
                document.getDocumentElement().getElementsByTagNameNS("*", "SessionIndex").item(0).getTextContent());
        assertEquals(TestSaml.SP_ENTITY_ID,
                document.getDocumentElement().getElementsByTagNameNS("*", "Issuer").item(0).getTextContent());

        // BennuLogoutHandler must have invalidated the original session, and the pending logout
        // request must have been stored in a cookie (it has to survive the local logout)
        assertTrue("Expected the Bennu session to be invalidated", session.isInvalid());
        Saml2LogoutRequest saved = loadPendingLogoutRequest(response);
        assertNotNull("Expected the logout request to be stored for InResponseTo validation", saved);
    }

    @Test
    public void processesIdpInitiatedLogoutRequests() throws Exception {
        MockHttpServletRequest request = requestWithIdentity("POST", "/api/saml-client/logout");
        MockHttpSession session = (MockHttpSession) request.getSession();
        request.addParameter("SAMLRequest",
                Base64.getEncoder().encodeToString(TestSaml.logoutRequestXml("_idpLogout1", TestSaml.NAME_ID_VALUE)
                        .getBytes()));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        String location = response.getRedirectedUrl();
        assertTrue("Expected a LogoutResponse redirect to the IdP SLO endpoint, got: " + location,
                location.startsWith(TestSaml.IDP_SLO_REDIRECT_URL));

        Map<String, String> parameters = queryParameters(new java.net.URI(location).getRawQuery());
        Document document = TestSaml.parse(TestSaml.inflateAndBase64Decode(parameters.get("SAMLResponse")));
        assertEquals("LogoutResponse", document.getDocumentElement().getLocalName());
        assertEquals("_idpLogout1",
                document.getDocumentElement().getAttribute("InResponseTo"));
        assertTrue("Expected the Bennu session to be invalidated", session.isInvalid());
    }

    @Test
    public void processesTheLogoutResponseCompletingSpInitiatedLogout() throws Exception {
        // step 1: SP-initiated logout, which stores the pending logout request in a cookie
        MockHttpServletRequest logoutRequest = requestWithIdentity("POST", "/api/saml-client/logout");
        MockHttpServletResponse logoutResponse = new MockHttpServletResponse();
        filterChain.doFilter(logoutRequest, logoutResponse, new MockFilterChain());
        assertEquals(HttpServletResponse.SC_FOUND, logoutResponse.getStatus());

        javax.servlet.http.Cookie pendingCookie = logoutResponse
                .getCookie(CookieSaml2LogoutRequestRepository.COOKIE_NAME);
        assertNotNull(pendingCookie);
        Map<String, String> redirectParameters = queryParameters(
                new java.net.URI(logoutResponse.getRedirectedUrl()).getRawQuery());
        Saml2LogoutRequest saved = loadPendingLogoutRequest(logoutResponse);
        assertNotNull(saved);
        // the SP-initiated LogoutRequest is stored redirect-bound (deflated and base64-encoded)
        String pendingLogoutRequestId = TestSaml
                .parse(TestSaml.inflateAndBase64Decode(saved.getSamlRequest()))
                .getDocumentElement().getAttribute("ID");

        // step 2: the IdP confirms the logout (echoing RelayState); the pending request must
        // match by InResponseTo
        MockHttpServletRequest responseRequest = SamlContextSupport.request("POST", "/api/saml-client/logout");
        responseRequest.setCookies(pendingCookie);
        responseRequest.addParameter("RelayState", redirectParameters.get("RelayState"));
        responseRequest.addParameter("SAMLResponse", Base64.getEncoder()
                .encodeToString(TestSaml.logoutResponseXml("_idpLogoutResponse", pendingLogoutRequestId).getBytes()));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(responseRequest, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        assertEquals("/login?logout", response.getRedirectedUrl());
    }

    /**
     * SimpleSAMLphp does not sign SLO messages by default; the default (lenient) validator
     * configuration must still complete the SP-initiated logout round trip.
     */
    @Test
    public void acceptsAnUnsignedLogoutResponseCompletingSpInitiatedLogout() throws Exception {
        // step 1: SP-initiated logout, which stores the pending logout request in a cookie
        MockHttpServletRequest logoutRequest = requestWithIdentity("POST", "/api/saml-client/logout");
        MockHttpServletResponse logoutResponse = new MockHttpServletResponse();
        filterChain.doFilter(logoutRequest, logoutResponse, new MockFilterChain());
        assertEquals(HttpServletResponse.SC_FOUND, logoutResponse.getStatus());

        javax.servlet.http.Cookie pendingCookie = logoutResponse
                .getCookie(CookieSaml2LogoutRequestRepository.COOKIE_NAME);
        assertNotNull(pendingCookie);
        Saml2LogoutRequest saved = loadPendingLogoutRequest(logoutResponse);
        assertNotNull(saved);
        String pendingLogoutRequestId = TestSaml
                .parse(TestSaml.inflateAndBase64Decode(saved.getSamlRequest()))
                .getDocumentElement().getAttribute("ID");

        // step 2: the IdP confirms the logout with an UNSIGNED LogoutResponse
        MockHttpServletRequest responseRequest = SamlContextSupport.request("POST", "/api/saml-client/logout");
        responseRequest.setCookies(pendingCookie);
        responseRequest.addParameter("SAMLResponse", Base64.getEncoder()
                .encodeToString(TestSaml.logoutResponseXmlUnsigned("_idpUnsignedResponse", pendingLogoutRequestId)
                        .getBytes()));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(responseRequest, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        assertEquals("/login?logout", response.getRedirectedUrl());
    }

    @Test
    public void acceptsAnUnsignedIdpInitiatedLogoutRequest() throws Exception {
        MockHttpServletRequest request = requestWithIdentity("POST", "/api/saml-client/logout");
        MockHttpSession session = (MockHttpSession) request.getSession();
        request.addParameter("SAMLRequest",
                Base64.getEncoder().encodeToString(TestSaml
                        .logoutRequestXmlUnsigned("_idpUnsignedLogout", TestSaml.NAME_ID_VALUE).getBytes()));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        String location = response.getRedirectedUrl();
        assertTrue("Expected a LogoutResponse redirect to the IdP SLO endpoint, got: " + location,
                location.startsWith(TestSaml.IDP_SLO_REDIRECT_URL));
        assertTrue("Expected the Bennu session to be invalidated", session.isInvalid());
    }

    @Test
    public void rejectsALogoutResponseWithAnUnknownInResponseTo() throws Exception {
        MockHttpServletRequest request = SamlContextSupport.request("POST", "/api/saml-client/logout");
        request.addParameter("SAMLResponse", Base64.getEncoder()
                .encodeToString(TestSaml.logoutResponseXml("_orphanResponse", "_noPendingLogout").getBytes()));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filterChain.doFilter(request, response, new MockFilterChain());

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }
}
