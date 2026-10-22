package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Base64;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.web.HttpSessionSaml2AuthenticationRequestRepository;

/**
 * Verifies the ACS-side converter: the single static registration is resolved regardless of the
 * missing {registrationId} path variable, the response is decoded (and inflated for redirect
 * binding), and a stored authentication request is carried into the token.
 */
public class BennuSaml2AuthenticationConverterTest {

    private RelyingPartyRegistration registration;
    private BennuSaml2AuthenticationConverter converter;

    @Before
    public void setup() {
        BennuRelyingPartyRegistrationRepository repository = new BennuRelyingPartyRegistrationRepository();
        repository.setServletContext(new MockServletContext());
        registration = repository.findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);
        converter = new BennuSaml2AuthenticationConverter(repository);
    }

    private Saml2AuthenticationToken convert(MockHttpServletRequest request) {
        Authentication token = converter.convert(request);
        assertEquals(Saml2AuthenticationToken.class, token.getClass());
        return (Saml2AuthenticationToken) token;
    }

    @Test
    public void convertsPostBoundResponse() {
        String responseXml = TestSaml.samlResponseXml("_resp1", null);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/saml-client/returnFromSAML");
        request.addParameter("SAMLResponse", Base64.getEncoder().encodeToString(responseXml.getBytes()));

        Saml2AuthenticationToken token = convert(request);
        assertEquals(registration, token.getRelyingPartyRegistration());
        assertEquals(responseXml, token.getSaml2Response());
    }

    @Test
    public void convertsAndInflatesRedirectBoundResponse() {
        String responseXml = TestSaml.samlResponseXml("_resp2", null);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/saml-client/returnFromSAML");
        request.addParameter("SAMLResponse", TestSaml.deflateAndBase64(responseXml));

        Saml2AuthenticationToken token = convert(request);
        assertEquals(responseXml, token.getSaml2Response());
    }

    @Test
    public void carriesTheStoredAuthenticationRequestForInResponseToValidation() {
        String authnRequestXml = TestSaml.authnRequestXml("_authnRequest1");
        AbstractSaml2AuthenticationRequest stored = TestSaml.savedAuthenticationRequest(registration, authnRequestXml, null);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/saml-client/returnFromSAML");
        MockHttpServletResponse response = new MockHttpServletResponse();
        new HttpSessionSaml2AuthenticationRequestRepository().saveAuthenticationRequest(stored, request, response);
        request.addParameter("SAMLResponse",
                Base64.getEncoder().encodeToString(TestSaml.samlResponseXml("_resp3", "_authnRequest1").getBytes()));

        AbstractSaml2AuthenticationRequest loaded = convert(request).getAuthenticationRequest();
        // the engine stores the base64-encoded serialized AuthnRequest
        assertEquals(authnRequestXml,
                new String(java.util.Base64.getMimeDecoder().decode(loaded.getSamlRequest()),
                        java.nio.charset.StandardCharsets.UTF_8));
    }

    @Test
    public void returnsNullWithoutASamlResponseParameter() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/saml-client/returnFromSAML");
        assertNull(converter.convert(request));
    }
}
