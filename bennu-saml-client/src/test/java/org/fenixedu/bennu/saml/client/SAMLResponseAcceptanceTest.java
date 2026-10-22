package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationException;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2PostAuthenticationRequest;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;

/**
 * The full SAML protocol round trip without a live IdP: a signed Response crafted by the test
 * IdP (TestSaml) is accepted by the engine, with the registration produced from the configured
 * metadata and SP credentials. Also asserts that tampered and unsolicited responses are handled
 * according to the SAML validation rules.
 */
public class SAMLResponseAcceptanceTest {

    private RelyingPartyRegistration registration;
    private OpenSaml4AuthenticationProvider provider;

    @Before
    public void setup() {
        BennuRelyingPartyRegistrationRepository repository = new BennuRelyingPartyRegistrationRepository();
        repository.setServletContext(new MockServletContext());
        this.registration = repository.findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);
        this.provider = new OpenSaml4AuthenticationProvider();
    }

    private Saml2AuthenticationToken token(String responseId, String inResponseTo, String requestId) {
        Saml2PostAuthenticationRequest stored = null;
        if (requestId != null) {
            stored = TestSaml.savedAuthenticationRequest(registration, TestSaml.authnRequestXml(requestId), null);
        }
        return new Saml2AuthenticationToken(registration, TestSaml.samlResponseXml(responseId, inResponseTo), stored);
    }

    @Test
    public void acceptsASignedResponseFromTheConfiguredIdp() {
        Authentication authentication = provider.authenticate(token("_respOk", "_authnRequest1", "_authnRequest1"));

        assertEquals(TestSaml.NAME_ID_VALUE, authentication.getName());
        org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal principal =
                (org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal) authentication
                        .getPrincipal();
        assertEquals(SAML2SecurityConfiguration.REGISTRATION_ID, principal.getRelyingPartyRegistrationId());
        assertEquals(TestSaml.USERNAME_VALUE, principal.getFirstAttribute(TestSaml.USERNAME_ATTRIBUTE));
        assertEquals(Collections.singletonList(TestSaml.SESSION_INDEX), principal.getSessionIndexes());
    }

    @Test
    public void acceptsAnUnsolicitedResponseWithoutInResponseTo() {
        Authentication authentication = provider.authenticate(token("_respUnsolicited", null, null));
        assertEquals(TestSaml.NAME_ID_VALUE, authentication.getName());
    }

    @Test
    public void rejectsAResponseWhoseInResponseToHasNoStoredRequest() {
        try {
            provider.authenticate(token("_respOrphan", "_unknownRequest", null));
            fail("Expected a Saml2AuthenticationException for an unmatched InResponseTo");
        } catch (Saml2AuthenticationException e) {
            // expected: InResponseTo does not match any stored authentication request
        }
    }

    @Test
    public void rejectsATamperedResponse() {
        String responseXml = TestSaml.samlResponseXml("_respTampered", "_authnRequest1");
        String tamperedXml = responseXml.replace(TestSaml.USERNAME_VALUE, "hacker@test.example");
        org.junit.Assert.assertFalse("Tampering should change the payload", tamperedXml.equals(responseXml));
        Saml2AuthenticationToken tampered = new Saml2AuthenticationToken(registration, tamperedXml,
                TestSaml.savedAuthenticationRequest(registration, TestSaml.authnRequestXml("_authnRequest1"), null));
        try {
            provider.authenticate(tampered);
            fail("Expected a Saml2AuthenticationException for a tampered response");
        } catch (Saml2AuthenticationException e) {
            // expected: signature validation failure
        }
    }

    @Test
    public void rejectsAResponseFromAnUnknownIssuer() {
        // the token's registration is bound to the configured IdP; craft a response whose issuer
        // does not match by pointing the destination check at a wrong audience instead
        String responseXml = TestSaml.samlResponseXml("_respForeign", null);
        org.w3c.dom.Document document = TestSaml.parse(responseXml);
        org.w3c.dom.NodeList issuers = document.getElementsByTagNameNS("*", "Issuer");
        issuers.item(0).getFirstChild().setNodeValue("https://evil.example/metadata");
        String foreignXml = TestSaml.serialize(document.getDocumentElement());

        try {
            provider.authenticate(new Saml2AuthenticationToken(registration, foreignXml, null));
            fail("Expected a Saml2AuthenticationException for an unknown issuer");
        } catch (Saml2AuthenticationException e) {
            // expected: issuer mismatch (and/or signature failure after re-serialization)
        }
    }

    @Test
    public void storedAuthenticationRequestCarriesTheAuthnRequestId() {
        // sanity-check the fixture used for InResponseTo validation
        AuthnRequest request = null;
        try {
            request = (AuthnRequest) org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport
                    .getUnmarshallerFactory().getUnmarshaller(AuthnRequest.DEFAULT_ELEMENT_NAME)
                    .unmarshall(TestSaml.parse(TestSaml.authnRequestXml("_reqX")).getDocumentElement());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        assertNotNull(request);
        assertEquals("_reqX", request.getID());
    }
}
