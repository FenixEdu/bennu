package org.fenixedu.bennu.saml.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.ProxyRestriction;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.Signer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml2.core.OpenSamlInitializationService;
import org.springframework.security.saml2.provider.service.authentication.Saml2PostAuthenticationRequest;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Builds and signs SAML 2.0 messages on behalf of the fictitious test IdP (see
 * src/test/resources/idp-metadata.xml and keys/README.md), and provides small parsing helpers
 * for assertions.
 */
public final class TestSaml {

    public static final String SP_ENTITY_ID = "https://saml-client.test.example";
    public static final String SP_ACS_URL = SP_ENTITY_ID + "/api/saml-client/returnFromSAML";
    public static final String SP_SLO_URL = SP_ENTITY_ID + "/api/saml-client/logout";
    public static final String IDP_ENTITY_ID = "https://idp.test.example/metadata";
    public static final String IDP_SSO_REDIRECT_URL = "https://idp.test.example/sso/redirect";
    public static final String IDP_SLO_REDIRECT_URL = "https://idp.test.example/slo/redirect";
    public static final String IDP_SLO_POST_URL = "https://idp.test.example/slo/post";

    public static final String USERNAME_ATTRIBUTE = "eduPersonPrincipalName";
    public static final String USERNAME_VALUE = "ist12345@test.example";
    public static final String STRIPPED_USERNAME = "ist12345";
    public static final String NAME_ID_VALUE = "idp-user-123";
    public static final String SESSION_INDEX = "session-index-1";

    private static final Pattern PEM_HEADER = Pattern.compile("-{2,}(BEGIN|END)\\s+[A-Z0-9 ]*-{2,}");

    private TestSaml() {
    }

    public static void initializeOpenSaml() {
        OpenSamlInitializationService.initialize();
    }

    public static <T extends XMLObject> T build(QName elementName) {
        initializeOpenSaml();
        return (T) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(elementName)
                .buildObject(elementName);
    }

    public static RSAPrivateKey idpPrivateKey() {
        return readPrivateKey("keys/idp-key-pkcs8");
    }

    public static X509Certificate idpCertificate() {
        return readCertificate("keys/idp-cert");
    }

    public static X509Certificate spCertificate() {
        return readCertificate("keys/sp-cert");
    }

    public static Credential idpCredential() {
        return new BasicX509Credential(idpCertificate(), idpPrivateKey());
    }

    public static <T extends SignableXMLObject> T prepareSignature(T signable) {
        Signature signature = build(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSigningCredential(idpCredential());
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signable.setSignature(signature);
        return signable;
    }

    /**
     * Marshals the signed message and computes the enveloped signature. The signature DOM is
     * attached during marshalling, so the signing must happen afterwards.
     */
    public static String serializeSigned(SignableXMLObject signable) {
        prepareSignature(signable);
        Element element = marshall(signable);
        try {
            Signer.signObject(signable.getSignature());
        } catch (Exception e) {
            throw new IllegalStateException("Could not sign SAML message", e);
        }
        return serialize(element);
    }

    public static Element marshall(XMLObject xmlObject) {
        try {
            Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory()
                    .getMarshaller(xmlObject);
            return marshaller.marshall(xmlObject);
        } catch (Exception e) {
            throw new IllegalStateException("Could not marshal SAML message", e);
        }
    }

    public static String serialize(XMLObject xmlObject) {
        try {
            Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory()
                    .getMarshaller(xmlObject);
            Element element = marshaller.marshall(xmlObject);
            return serialize(element);
        } catch (Exception e) {
            throw new IllegalStateException("Could not serialize SAML message", e);
        }
    }

    public static String serialize(Element element) {
        try {
            javax.xml.transform.Transformer transformer = javax.xml.transform.TransformerFactory.newInstance()
                    .newTransformer();
            java.io.StringWriter writer = new java.io.StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(element),
                    new javax.xml.transform.stream.StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Could not serialize DOM element", e);
        }
    }

    public static Document parse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse XML", e);
        }
    }

    public static String deflateAndBase64(String xml) {
        try {
            java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
            DeflaterOutputStream deflater = new DeflaterOutputStream(bytes,
                    new Deflater(Deflater.DEFLATED, true));
            deflater.write(xml.getBytes(StandardCharsets.UTF_8));
            deflater.finish();
            return Base64.getEncoder().encodeToString(bytes.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Could not deflate SAML message", e);
        }
    }

    public static String inflateAndBase64Decode(String encoded) {
        // delegates to the production inflater so the test exercises the code that runs
        return new String(BennuOpenSamlAuthenticationProvider.inflate(Base64.getMimeDecoder().decode(encoded)),
                StandardCharsets.UTF_8);
    }

    /**
     * A minimal AuthnRequest with the given ID, issued by the SP to the test IdP.
     */
    public static String authnRequestXml(String id) {
        AuthnRequest request = build(AuthnRequest.DEFAULT_ELEMENT_NAME);
        request.setID(id);
        request.setIssueInstant(DateTime.now());
        request.setIssuer(issuer(SP_ENTITY_ID));
        return serialize(request);
    }

    /**
     * A signed Response from the test IdP, with a signed (well-formed) assertion carrying the
     * test attributes. When {@code inResponseTo} is non-null it is set on the subject
     * confirmation data; otherwise the response is unsolicited.
     */
    public static String samlResponseXml(String id, String inResponseTo) {
        return samlResponseXml(id, inResponseTo, null);
    }

    /**
     * Same as {@link #samlResponseXml(String, String)}, additionally setting the
     * {@code InResponseTo} attribute on the {@code <Response>} element itself, which some IdPs
     * do (and which the engine validates independently of the subject confirmation data).
     */
    public static String samlResponseXml(String id, String inResponseTo, String responseInResponseTo) {
        return samlResponseXml(id, inResponseTo, responseInResponseTo, IDP_ENTITY_ID);
    }

    /**
     * Same as {@link #samlResponseXml(String, String, String)}, additionally overriding the
     * assertion's {@code Issuer}, to model a validly signed response whose assertion names a
     * different issuer (what the assertion-level issuer check guards against).
     */
    public static String samlResponseXml(String id, String inResponseTo, String responseInResponseTo,
            String assertionIssuer) {
        return serializeSigned(buildSamlResponse(id, inResponseTo, responseInResponseTo, assertionIssuer, true));
    }

    /**
     * A signed Response like {@link #samlResponseXml(String, String)}, but whose assertion carries
     * no username attribute, modelling an assertion the module cannot map to a Bennu user (the
     * success handler exits before the Bennu login).
     */
    public static String samlResponseXmlWithoutUsernameAttribute(String id, String inResponseTo) {
        return serializeSigned(buildSamlResponse(id, inResponseTo, null, IDP_ENTITY_ID, false));
    }

    /**
     * A signed Response whose assertion IssueInstant is shifted by the given number of minutes
     * (positive into the future), to exercise the IssueInstant freshness check.
     */
    public static String samlResponseXmlWithAssertionIssueInstantOffset(String id, String inResponseTo, int minutes) {
        Response response = buildSamlResponse(id, inResponseTo, null, IDP_ENTITY_ID, true);
        response.getAssertions().get(0).setIssueInstant(DateTime.now().plusMinutes(minutes));
        return serializeSigned(response);
    }

    /**
     * A signed Response with two Bearer confirmations: the built-in one answers the stored request
     * but carries a wrong recipient, a second valid one answers a different request — a mixed
     * failure (one confirmation fails on the recipient, the other on InResponseTo).
     */
    public static String samlResponseXmlWithWrongRecipientAndForeignInResponseTo(String id, String matchingInResponseTo,
            String foreignInResponseTo) {
        Response response = buildSamlResponse(id, matchingInResponseTo, null, IDP_ENTITY_ID, true);
        Subject subject = response.getAssertions().get(0).getSubject();
        subject.getSubjectConfirmations().get(0).getSubjectConfirmationData().setRecipient("https://evil.example/acs");
        subject.getSubjectConfirmations()
                .add(subjectConfirmation(SubjectConfirmation.METHOD_BEARER, foreignInResponseTo));
        return serializeSigned(response);
    }

    /**
     * A signed Response with two Bearer confirmations: the built-in one answers the stored request
     * but is already expired, a second valid one answers a different request — the split the
     * same-confirmation rule rejects (neither confirmation both answers and is valid).
     */
    public static String samlResponseXmlWithSplitBearerConfirmations(String id, String matchingInResponseTo,
            String foreignInResponseTo) {
        Response response = buildSamlResponse(id, matchingInResponseTo, null, IDP_ENTITY_ID, true);
        Subject subject = response.getAssertions().get(0).getSubject();
        // expire well beyond the validator's 5-minute clock skew, so the expiry itself is the
        // reason this confirmation fails
        subject.getSubjectConfirmations().get(0).getSubjectConfirmationData()
                .setNotOnOrAfter(DateTime.now().minusMinutes(10));
        subject.getSubjectConfirmations()
                .add(subjectConfirmation(SubjectConfirmation.METHOD_BEARER, foreignInResponseTo));
        return serializeSigned(response);
    }

    /**
     * A signed Response whose assertion carries a {@code <ProxyRestriction>} condition before the
     * audience restriction — OpenSAML 3.4.6 bails on it as an unknown condition, skipping the
     * conditions after it. The optional audience overrides the assertion's audience restriction.
     */
    public static String samlResponseXmlWithProxyRestriction(String id, String inResponseTo, String audienceUri) {
        Response response = buildSamlResponse(id, inResponseTo, null, IDP_ENTITY_ID, true);
        ProxyRestriction proxyRestriction = build(ProxyRestriction.DEFAULT_ELEMENT_NAME);
        response.getAssertions().get(0).getConditions().getConditions().add(0, proxyRestriction);
        if (audienceUri != null) {
            // point the audience restriction at a foreign entity, to prove the audience check
            // still runs behind the ProxyRestriction condition
            AudienceRestriction restriction = response.getAssertions().get(0).getConditions()
                    .getAudienceRestrictions().get(0);
            restriction.getAudiences().get(0).setAudienceURI(audienceUri);
        }
        return serializeSigned(response);
    }

    /**
     * A signed Response like {@link #samlResponseXml(String, String)}, with an additional subject
     * confirmation of the given method carrying its own InResponseTo, modelling IdPs that add
     * further confirmations to a bearer assertion.
     */
    public static String samlResponseXmlWithExtraConfirmation(String id, String inResponseTo, String method,
            String extraInResponseTo) {
        Response response = buildSamlResponse(id, inResponseTo, null, IDP_ENTITY_ID, true);
        response.getAssertions().get(0).getSubject().getSubjectConfirmations()
                .add(subjectConfirmation(method, extraInResponseTo));
        return serializeSigned(response);
    }

    private static Response buildSamlResponse(String id, String inResponseTo, String responseInResponseTo,
            String assertionIssuer, boolean includeUsernameAttribute) {
        Response response = build(Response.DEFAULT_ELEMENT_NAME);
        response.setID(id);
        response.setIssueInstant(DateTime.now());
        if (responseInResponseTo != null) {
            response.setInResponseTo(responseInResponseTo);
        }
        response.setDestination(SP_ACS_URL);
        response.setIssuer(issuer(IDP_ENTITY_ID));

        Status status = build(Status.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = build(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(StatusCode.SUCCESS);
        status.setStatusCode(statusCode);
        response.setStatus(status);

        Assertion assertion = build(Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID("_assertion" + id);
        assertion.setIssueInstant(DateTime.now());
        assertion.setIssuer(issuer(assertionIssuer));

        Subject subject = build(Subject.DEFAULT_ELEMENT_NAME);
        NameID nameId = build(NameID.DEFAULT_ELEMENT_NAME);
        nameId.setValue(NAME_ID_VALUE);
        nameId.setFormat(NameID.PERSISTENT);
        subject.setNameID(nameId);

        subject.getSubjectConfirmations().add(subjectConfirmation(SubjectConfirmation.METHOD_BEARER, inResponseTo));
        assertion.setSubject(subject);

        Conditions conditions = build(Conditions.DEFAULT_ELEMENT_NAME);
        conditions.setNotBefore(DateTime.now().minusSeconds(300));
        conditions.setNotOnOrAfter(DateTime.now().plusSeconds(300));
        AudienceRestriction restriction = build(AudienceRestriction.DEFAULT_ELEMENT_NAME);
        Audience audience = build(Audience.DEFAULT_ELEMENT_NAME);
        audience.setAudienceURI(SP_ENTITY_ID);
        restriction.getAudiences().add(audience);
        conditions.getAudienceRestrictions().add(restriction);
        assertion.setConditions(conditions);

        AuthnStatement authnStatement = build(AuthnStatement.DEFAULT_ELEMENT_NAME);
        authnStatement.setAuthnInstant(DateTime.now());
        authnStatement.setSessionIndex(SESSION_INDEX);
        AuthnContext authnContext = build(AuthnContext.DEFAULT_ELEMENT_NAME);
        AuthnContextClassRef authnContextClassRef = build(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);
        assertion.getAuthnStatements().add(authnStatement);

        AttributeStatement attributeStatement = build(AttributeStatement.DEFAULT_ELEMENT_NAME);
        if (includeUsernameAttribute) {
            attributeStatement.getAttributes().add(attribute(USERNAME_ATTRIBUTE, USERNAME_VALUE));
        }
        attributeStatement.getAttributes().add(attribute("mail", "user@test.example"));
        assertion.getAttributeStatements().add(attributeStatement);

        response.getAssertions().add(assertion);
        return response;
    }

    private static SubjectConfirmation subjectConfirmation(String method, String inResponseTo) {
        SubjectConfirmation confirmation = build(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        confirmation.setMethod(method);
        SubjectConfirmationData confirmationData = build(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
        confirmationData.setRecipient(SP_ACS_URL);
        confirmationData.setNotOnOrAfter(DateTime.now().plusSeconds(300));
        if (inResponseTo != null) {
            confirmationData.setInResponseTo(inResponseTo);
        }
        confirmation.setSubjectConfirmationData(confirmationData);
        return confirmation;
    }

    /**
     * A signed LogoutRequest from the test IdP to the SP's SLO endpoint.
     */
    public static String logoutRequestXml(String id, String nameIdValue) {
        return serializeSigned(logoutRequest(id, nameIdValue));
    }

    /**
     * An unsigned LogoutRequest from the test IdP (SimpleSAMLphp's default for SLO messages).
     */
    public static String logoutRequestXmlUnsigned(String id, String nameIdValue) {
        return serialize(logoutRequest(id, nameIdValue));
    }

    private static LogoutRequest logoutRequest(String id, String nameIdValue) {
        LogoutRequest request = build(LogoutRequest.DEFAULT_ELEMENT_NAME);
        request.setID(id);
        request.setIssueInstant(DateTime.now());
        request.setDestination(SP_SLO_URL);
        request.setIssuer(issuer(IDP_ENTITY_ID));
        NameID nameId = build(NameID.DEFAULT_ELEMENT_NAME);
        nameId.setValue(nameIdValue);
        nameId.setFormat(NameID.PERSISTENT);
        request.setNameID(nameId);
        return request;
    }

    /**
     * A signed LogoutResponse from the test IdP, completing the SP-initiated logout with the
     * given InResponseTo.
     */
    public static String logoutResponseXml(String id, String inResponseTo) {
        return serializeSigned(logoutResponse(id, inResponseTo));
    }

    /**
     * An unsigned LogoutResponse from the test IdP (SimpleSAMLphp's default for SLO messages).
     */
    public static String logoutResponseXmlUnsigned(String id, String inResponseTo) {
        return serialize(logoutResponse(id, inResponseTo));
    }

    private static LogoutResponse logoutResponse(String id, String inResponseTo) {
        LogoutResponse response = build(LogoutResponse.DEFAULT_ELEMENT_NAME);
        response.setID(id);
        response.setIssueInstant(DateTime.now());
        response.setDestination(SP_SLO_URL);
        response.setInResponseTo(inResponseTo);
        response.setIssuer(issuer(IDP_ENTITY_ID));
        Status status = build(Status.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = build(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(StatusCode.SUCCESS);
        status.setStatusCode(statusCode);
        response.setStatus(status);
        return response;
    }

    /**
     * The authentication request record the SP engine would have stored in the session when
     * redirecting the user to the IdP, used to satisfy InResponseTo validation. The engine stores
     * the base64-encoded serialized AuthnRequest.
     */
    public static Saml2PostAuthenticationRequest savedAuthenticationRequest(RelyingPartyRegistration registration,
            String authnRequestXml, String relayState) {
        return Saml2PostAuthenticationRequest.withRelyingPartyRegistration(registration)
                .samlRequest(Base64.getMimeEncoder().encodeToString(authnRequestXml.getBytes(StandardCharsets.UTF_8)))
                .relayState(relayState)
                .authenticationRequestUri(IDP_SSO_REDIRECT_URL)
                .build();
    }

    public static Issuer issuer(String value) {
        Issuer issuer = build(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue(value);
        return issuer;
    }

    public static Attribute attribute(String name, String value) {
        Attribute attribute = build(Attribute.DEFAULT_ELEMENT_NAME);
        attribute.setName(name);
        attribute.setNameFormat(Attribute.BASIC);
        // OpenSAML 3 has no XSAny-typed AttributeValue: build an xs:string value instead
        XSString attributeValue = new XSStringBuilder().buildObject(AttributeValue.DEFAULT_ELEMENT_NAME,
                XSString.TYPE_NAME);
        attributeValue.setValue(value);
        attribute.getAttributeValues().add(attributeValue);
        return attribute;
    }

    private static RSAPrivateKey readPrivateKey(String classPathLocation) {
        try (InputStream inputStream = new ClassPathResource(classPathLocation).getInputStream()) {
            String pem = new String(readAll(inputStream), StandardCharsets.UTF_8);
            byte[] encoded = Base64.getMimeDecoder().decode(PEM_HEADER.matcher(pem).replaceAll(""));
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (Exception e) {
            throw new IllegalStateException("Could not read test private key " + classPathLocation, e);
        }
    }

    private static X509Certificate readCertificate(String classPathLocation) {
        try (InputStream inputStream = new ClassPathResource(classPathLocation).getInputStream()) {
            String pem = new String(readAll(inputStream), StandardCharsets.UTF_8);
            byte[] encoded = Base64.getMimeDecoder().decode(PEM_HEADER.matcher(pem).replaceAll(""));
            return (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(encoded));
        } catch (Exception e) {
            throw new IllegalStateException("Could not read test certificate " + classPathLocation, e);
        }
    }

    private static byte[] readAll(InputStream inputStream) throws Exception {
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = inputStream.read(buffer)) >= 0) {
            bytes.write(buffer, 0, read);
        }
        return bytes.toByteArray();
    }
}
