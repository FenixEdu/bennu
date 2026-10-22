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
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.opensaml.core.xml.XMLObject;
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
        try {
            byte[] deflated = Base64.getMimeDecoder().decode(encoded);
            Inflater inflater = new Inflater(true);
            inflater.setInput(deflated);
            java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            while (!inflater.finished()) {
                int read = inflater.inflate(buffer);
                if (read == 0 && inflater.needsInput()) {
                    break;
                }
                bytes.write(buffer, 0, read);
            }
            inflater.end();
            return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not inflate SAML message", e);
        }
    }

    /**
     * A minimal AuthnRequest with the given ID, issued by the SP to the test IdP.
     */
    public static String authnRequestXml(String id) {
        AuthnRequest request = build(AuthnRequest.DEFAULT_ELEMENT_NAME);
        request.setID(id);
        request.setIssueInstant(Instant.now());
        request.setIssuer(issuer(SP_ENTITY_ID));
        return serialize(request);
    }

    /**
     * A signed Response from the test IdP, with a signed (well-formed) assertion carrying the
     * test attributes. When {@code inResponseTo} is non-null it is set on the subject
     * confirmation data; otherwise the response is unsolicited.
     */
    public static String samlResponseXml(String id, String inResponseTo) {
        Response response = build(Response.DEFAULT_ELEMENT_NAME);
        response.setID(id);
        response.setIssueInstant(Instant.now());
        response.setDestination(SP_ACS_URL);
        response.setIssuer(issuer(IDP_ENTITY_ID));

        Status status = build(Status.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = build(StatusCode.DEFAULT_ELEMENT_NAME);
        statusCode.setValue(StatusCode.SUCCESS);
        status.setStatusCode(statusCode);
        response.setStatus(status);

        Assertion assertion = build(Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID("_assertion" + id);
        assertion.setIssueInstant(Instant.now());
        assertion.setIssuer(issuer(IDP_ENTITY_ID));

        Subject subject = build(Subject.DEFAULT_ELEMENT_NAME);
        NameID nameId = build(NameID.DEFAULT_ELEMENT_NAME);
        nameId.setValue(NAME_ID_VALUE);
        nameId.setFormat(NameID.PERSISTENT);
        subject.setNameID(nameId);

        SubjectConfirmation confirmation = build(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        confirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
        SubjectConfirmationData confirmationData = build(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
        confirmationData.setRecipient(SP_ACS_URL);
        confirmationData.setNotOnOrAfter(Instant.now().plusSeconds(300));
        if (inResponseTo != null) {
            confirmationData.setInResponseTo(inResponseTo);
        }
        confirmation.setSubjectConfirmationData(confirmationData);
        subject.getSubjectConfirmations().add(confirmation);
        assertion.setSubject(subject);

        Conditions conditions = build(Conditions.DEFAULT_ELEMENT_NAME);
        conditions.setNotBefore(Instant.now().minusSeconds(300));
        conditions.setNotOnOrAfter(Instant.now().plusSeconds(300));
        AudienceRestriction restriction = build(AudienceRestriction.DEFAULT_ELEMENT_NAME);
        Audience audience = build(Audience.DEFAULT_ELEMENT_NAME);
        audience.setURI(SP_ENTITY_ID);
        restriction.getAudiences().add(audience);
        conditions.getAudienceRestrictions().add(restriction);
        assertion.setConditions(conditions);

        AuthnStatement authnStatement = build(AuthnStatement.DEFAULT_ELEMENT_NAME);
        authnStatement.setAuthnInstant(Instant.now());
        authnStatement.setSessionIndex(SESSION_INDEX);
        AuthnContext authnContext = build(AuthnContext.DEFAULT_ELEMENT_NAME);
        AuthnContextClassRef authnContextClassRef = build(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);
        assertion.getAuthnStatements().add(authnStatement);

        AttributeStatement attributeStatement = build(AttributeStatement.DEFAULT_ELEMENT_NAME);
        attributeStatement.getAttributes().add(attribute(USERNAME_ATTRIBUTE, USERNAME_VALUE));
        attributeStatement.getAttributes().add(attribute("mail", "user@test.example"));
        assertion.getAttributeStatements().add(attributeStatement);

        response.getAssertions().add(assertion);
        return serializeSigned(response);
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
        request.setIssueInstant(Instant.now());
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
        response.setIssueInstant(Instant.now());
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
        AttributeValue attributeValue = build(AttributeValue.DEFAULT_ELEMENT_NAME);
        attributeValue.setTextContent(value);
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
