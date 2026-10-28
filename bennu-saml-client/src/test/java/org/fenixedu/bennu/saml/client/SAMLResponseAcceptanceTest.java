package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.core.Saml2ErrorCodes;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationException;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2PostAuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.Saml2RedirectAuthenticationRequest;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;

/**
 * The full SAML protocol round trip without a live IdP: a signed Response crafted by the test
 * IdP (TestSaml) is accepted by the engine, with the registration produced from the configured
 * metadata and SP credentials. Also asserts that tampered and unsolicited responses are handled
 * according to the SAML validation rules.
 */
public class SAMLResponseAcceptanceTest {

    private RelyingPartyRegistration registration;
    private BennuOpenSamlAuthenticationProvider provider;

    @Before
    public void setup() {
        BennuRelyingPartyRegistrationRepository repository = new BennuRelyingPartyRegistrationRepository();
        repository.setServletContext(new MockServletContext());
        this.registration = repository.findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);
        this.provider = new BennuOpenSamlAuthenticationProvider();
    }

    private Saml2AuthenticationToken token(String responseId, String inResponseTo, String requestId) {
        Saml2PostAuthenticationRequest stored = null;
        if (requestId != null) {
            stored = TestSaml.savedAuthenticationRequest(registration, TestSaml.authnRequestXml(requestId), null);
        }
        return new Saml2AuthenticationToken(registration, TestSaml.samlResponseXml(responseId, inResponseTo), stored);
    }

    private Saml2AuthenticationToken tokenWithResponseInResponseTo(String responseId, String inResponseTo,
            String responseInResponseTo, String requestId) {
        Saml2PostAuthenticationRequest stored = null;
        if (requestId != null) {
            stored = TestSaml.savedAuthenticationRequest(registration, TestSaml.authnRequestXml(requestId), null);
        }
        return new Saml2AuthenticationToken(registration,
                TestSaml.samlResponseXml(responseId, inResponseTo, responseInResponseTo), stored);
    }

    private Saml2AuthenticationToken tokenWithExtraConfirmation(String responseId, String inResponseTo, String method,
            String extraInResponseTo, String requestId) {
        Saml2PostAuthenticationRequest stored = null;
        if (requestId != null) {
            stored = TestSaml.savedAuthenticationRequest(registration, TestSaml.authnRequestXml(requestId), null);
        }
        return new Saml2AuthenticationToken(registration,
                TestSaml.samlResponseXmlWithExtraConfirmation(responseId, inResponseTo, method, extraInResponseTo),
                stored);
    }

    private Saml2AuthenticationToken tokenWithSplitBearerConfirmations(String responseId, String matchingInResponseTo,
            String foreignInResponseTo) {
        return new Saml2AuthenticationToken(registration,
                TestSaml.samlResponseXmlWithSplitBearerConfirmations(responseId, matchingInResponseTo,
                        foreignInResponseTo),
                TestSaml.savedAuthenticationRequest(registration, TestSaml.authnRequestXml(matchingInResponseTo), null));
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
            assertEquals(Saml2ErrorCodes.INVALID_IN_RESPONSE_TO, e.getError().getErrorCode());
        }
    }

    @Test
    public void acceptsAResponseWhenAtLeastOneBearerConfirmationMatches() {
        // the OpenSAML 4 semantics: a Bearer confirmation is satisfied when at least one of them
        // matches the stored request, even if a further one does not
        Authentication authentication = provider.authenticate(tokenWithExtraConfirmation("_respMulti", "_authnRequest1",
                SubjectConfirmation.METHOD_BEARER, "_otherRequest", "_authnRequest1"));
        assertEquals(TestSaml.NAME_ID_VALUE, authentication.getName());
    }

    @Test
    public void acceptsAResponseWithAMismatchingNonBearerConfirmation() {
        // the InResponseTo binding is checked on Bearer confirmations only: a sender-vouches
        // confirmation carrying a different InResponseTo does not reject the response
        Authentication authentication = provider.authenticate(tokenWithExtraConfirmation("_respMixed",
                "_authnRequest1", SubjectConfirmation.METHOD_SENDER_VOUCHES, "_otherRequest", "_authnRequest1"));
        assertEquals(TestSaml.NAME_ID_VALUE, authentication.getName());
    }

    @Test
    public void rejectsAResponseWhoseBearerConfirmationsDoNotMatchTheStoredRequest() {
        try {
            provider.authenticate(token("_respBearerMismatch", "_otherRequest", "_authnRequest1"));
            fail("Expected a Saml2AuthenticationException for a non-matching Bearer InResponseTo");
        } catch (Saml2AuthenticationException e) {
            assertEquals(Saml2ErrorCodes.INVALID_IN_RESPONSE_TO, e.getError().getErrorCode());
        }
    }

    @Test
    public void rejectsAResponseWhoseResponseLevelInResponseToHasNoStoredRequest() {
        // the InResponseTo is present only on the <Response> element, not on the subject
        // confirmation data
        try {
            provider.authenticate(tokenWithResponseInResponseTo("_respOrphanResponseLevel", null, "_unknownRequest", null));
            fail("Expected a Saml2AuthenticationException for an unmatched response-level InResponseTo");
        } catch (Saml2AuthenticationException e) {
            assertEquals(Saml2ErrorCodes.INVALID_IN_RESPONSE_TO, e.getError().getErrorCode());
        }
    }

    @Test
    public void rejectsAResponseWhoseResponseLevelInResponseToMismatchesTheStoredRequest() {
        try {
            provider.authenticate(tokenWithResponseInResponseTo("_respMismatchResponseLevel", "_authnRequest1",
                    "_otherRequest", "_authnRequest1"));
            fail("Expected a Saml2AuthenticationException for a mismatched response-level InResponseTo");
        } catch (Saml2AuthenticationException e) {
            assertEquals(Saml2ErrorCodes.INVALID_IN_RESPONSE_TO, e.getError().getErrorCode());
        }
    }

    @Test
    public void acceptsAResponseWithARedirectBindingStoredRequest() {
        // the production decode path inflates redirect-binding stored requests; the other tests
        // all store POST requests, so this exercises the branch that actually runs for this IdP
        Saml2RedirectAuthenticationRequest stored = Saml2RedirectAuthenticationRequest
                .withRelyingPartyRegistration(registration)
                .samlRequest(TestSaml.deflateAndBase64(TestSaml.authnRequestXml("_authnRequest1")))
                .authenticationRequestUri(TestSaml.IDP_SSO_REDIRECT_URL)
                .build();
        Authentication authentication = provider.authenticate(new Saml2AuthenticationToken(registration,
                TestSaml.samlResponseXml("_respRedirect", "_authnRequest1"), stored));
        assertEquals(TestSaml.NAME_ID_VALUE, authentication.getName());
    }

    @Test
    public void rejectsACorruptStoredRequestAsMalformedRequestData() {
        // not deflated: inflating fails, which is malformed request data, not an internal error
        Saml2RedirectAuthenticationRequest stored = Saml2RedirectAuthenticationRequest
                .withRelyingPartyRegistration(registration)
                .samlRequest(java.util.Base64.getEncoder().encodeToString(
                        TestSaml.authnRequestXml("_authnRequest1").getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .authenticationRequestUri(TestSaml.IDP_SSO_REDIRECT_URL)
                .build();
        try {
            provider.authenticate(new Saml2AuthenticationToken(registration,
                    TestSaml.samlResponseXml("_respCorrupt", "_authnRequest1"), stored));
            fail("Expected a Saml2AuthenticationException for a corrupt stored request");
        } catch (Saml2AuthenticationException e) {
            assertEquals(Saml2ErrorCodes.MALFORMED_REQUEST_DATA, e.getError().getErrorCode());
        }
    }

    @Test
    public void acceptsAnUnsolicitedResponseWithABlankInResponseTo() {
        // OpenSAML 4 treats a blank InResponseTo as absent
        Authentication authentication = provider.authenticate(token("_respBlank", "", null));
        assertEquals(TestSaml.NAME_ID_VALUE, authentication.getName());
    }

    @Test
    public void rejectsAResponseWhoseRedirectBindingStoredRequestMismatches() {
        Saml2RedirectAuthenticationRequest stored = Saml2RedirectAuthenticationRequest
                .withRelyingPartyRegistration(registration)
                .samlRequest(TestSaml.deflateAndBase64(TestSaml.authnRequestXml("_authnRequest1")))
                .authenticationRequestUri(TestSaml.IDP_SSO_REDIRECT_URL)
                .build();
        try {
            provider.authenticate(new Saml2AuthenticationToken(registration,
                    TestSaml.samlResponseXml("_respRedirectMismatch", "_otherRequest"), stored));
            fail("Expected a Saml2AuthenticationException for a mismatched redirect-binding stored request");
        } catch (Saml2AuthenticationException e) {
            assertEquals(Saml2ErrorCodes.INVALID_IN_RESPONSE_TO, e.getError().getErrorCode());
        }
    }

    @Test
    public void rejectsAResponseWhoseAssertionIssuerIsNotTheIdp() {
        // validly signed by the test IdP, but the assertion names another issuer
        Saml2AuthenticationToken foreign = new Saml2AuthenticationToken(registration,
                TestSaml.samlResponseXml("_respForeignAssertion", null, null, "https://evil.example/metadata"), null);
        try {
            provider.authenticate(foreign);
            fail("Expected a Saml2AuthenticationException for an assertion issuer that is not the configured IdP");
        } catch (Saml2AuthenticationException e) {
            assertEquals(Saml2ErrorCodes.INVALID_ISSUER, e.getError().getErrorCode());
        }
    }

    @Test
    public void rejectsAResponseWhereOnlyAnExpiredConfirmationAnswersTheRequest() {
        // bearer confirmation A answers the stored request but is expired, B is valid but answers
        // a different request: neither confirmation both answers and is valid, so the response is
        // rejected — and since A fails on time (not InResponseTo), the mixed failure is reported
        // as an invalid assertion
        try {
            provider.authenticate(tokenWithSplitBearerConfirmations("_respSplit", "_authnRequest1", "_otherRequest"));
            fail("Expected a Saml2AuthenticationException when only an expired confirmation answers the request");
        } catch (Saml2AuthenticationException e) {
            assertEquals(Saml2ErrorCodes.INVALID_ASSERTION, e.getError().getErrorCode());
        }
    }

    @Test
    public void reportsInvalidAssertionWhenAConfirmationFailsForAnotherReason() {
        // one confirmation answers the stored request but carries a wrong recipient, the other is
        // valid but answers a different request: a mixed failure is reported as invalid_assertion
        try {
            provider.authenticate(new Saml2AuthenticationToken(registration,
                    TestSaml.samlResponseXmlWithWrongRecipientAndForeignInResponseTo("_respMixed", "_authnRequest1",
                            "_otherRequest"),
                    TestSaml.savedAuthenticationRequest(registration, TestSaml.authnRequestXml("_authnRequest1"), null)));
            fail("Expected a Saml2AuthenticationException for a mixed confirmation failure");
        } catch (Saml2AuthenticationException e) {
            assertEquals(Saml2ErrorCodes.INVALID_ASSERTION, e.getError().getErrorCode());
        }
    }

    @Test
    public void acceptsAnAssertionWithAProxyRestrictionCondition() {
        // OpenSAML 3.4.6 has no ProxyRestriction condition validator and fails the assertion as an
        // unknown condition; Bennu's validator mirrors OpenSAML 4.3.2's, which permits it
        Authentication authentication = provider.authenticate(new Saml2AuthenticationToken(registration,
                TestSaml.samlResponseXmlWithProxyRestriction("_respProxy", null, null), null));
        assertEquals(TestSaml.NAME_ID_VALUE, authentication.getName());
    }

    @Test
    public void rejectsAnAssertionIssuedTooLongAgo() {
        // OpenSAML 4 rejects an assertion older than its lifetime plus clock skew (about ten
        // minutes) even when the IdP's NotOnOrAfter is still in the future
        try {
            provider.authenticate(new Saml2AuthenticationToken(registration,
                    TestSaml.samlResponseXmlWithAssertionIssueInstantOffset("_respStale", null, -30), null));
            fail("Expected a Saml2AuthenticationException for an assertion issued 30 minutes ago");
        } catch (Saml2AuthenticationException e) {
            assertEquals(Saml2ErrorCodes.INVALID_ASSERTION, e.getError().getErrorCode());
        }
    }

    @Test
    public void rejectsAnAssertionIssuedInTheFuture() {
        try {
            provider.authenticate(new Saml2AuthenticationToken(registration,
                    TestSaml.samlResponseXmlWithAssertionIssueInstantOffset("_respFuture", null, 10), null));
            fail("Expected a Saml2AuthenticationException for an assertion issued in the future");
        } catch (Saml2AuthenticationException e) {
            assertEquals(Saml2ErrorCodes.INVALID_ASSERTION, e.getError().getErrorCode());
        }
    }

    @Test
    public void rejectsAnAssertionWithAProxyRestrictionAndAWrongAudience() {
        // the ProxyRestriction pass must not swallow the audience restriction that follows it
        try {
            provider.authenticate(new Saml2AuthenticationToken(registration,
                    TestSaml.samlResponseXmlWithProxyRestriction("_respProxyEvil", null, "https://evil.example/metadata"),
                    null));
            fail("Expected a Saml2AuthenticationException for a wrong audience behind a ProxyRestriction");
        } catch (Saml2AuthenticationException e) {
            assertEquals(Saml2ErrorCodes.INVALID_ASSERTION, e.getError().getErrorCode());
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
