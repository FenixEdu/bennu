package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.w3c.dom.Document;

/**
 * Verifies the enrichment of the generated SP metadata (Organization, ContactPerson and
 * AttributeConsumingService) with the saml.organization.*, saml.contact.* and saml.service.name
 * configuration values.
 */
public class BennuSaml2MetadataPostProcessorTest {

    private RelyingPartyRegistration registration;
    private Document metadata;
    private XPath xpath;

    @Before
    public void generateMetadata() throws Exception {
        BennuRelyingPartyRegistrationRepository repository = new BennuRelyingPartyRegistrationRepository();
        repository.setServletContext(new MockServletContext());
        this.registration = repository.findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);

        OpenSamlMetadataResolver resolver = new OpenSamlMetadataResolver();
        resolver.setEntityDescriptorCustomizer(new BennuSaml2MetadataPostProcessor());
        String xml = resolver.resolve(registration);

        this.metadata = TestSaml.parse(xml);
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    private String evaluate(String expression) throws Exception {
        return (String) xpath.evaluate(expression, metadata, XPathConstants.STRING);
    }

    @Test
    public void entityDescriptorCarriesTheSpIdentity() throws Exception {
        assertEquals(TestSaml.SP_ENTITY_ID, evaluate("//*[local-name()='EntityDescriptor']/@entityID"));
        assertEquals(TestSaml.SP_ACS_URL, evaluate("//*[local-name()='AssertionConsumerService'][1]/@Location"));
    }

    @Test
    public void spSloEndpointIsAdvertised() throws Exception {
        assertEquals(TestSaml.SP_SLO_URL, evaluate("//*[local-name()='SingleLogoutService'][1]/@Location"));
    }

    @Test
    public void organizationIsEnriched() throws Exception {
        assertEquals("Test Institution", evaluate("//*[local-name()='OrganizationName']/text()"));
        assertEquals("en",
                evaluate("//*[local-name()='OrganizationName']/@*[local-name()='lang']"));
        assertEquals("Test Institution Display", evaluate("//*[local-name()='OrganizationDisplayName']/text()"));
        assertEquals("https://www.test.example", evaluate("//*[local-name()='OrganizationURL']/text()"));
    }

    @Test
    public void contactPersonIsEnriched() throws Exception {
        assertEquals("technical", evaluate("//*[local-name()='ContactPerson']/@contactType"));
        assertEquals("Jane", evaluate("//*[local-name()='GivenName']/text()"));
        assertEquals("Doe", evaluate("//*[local-name()='SurName']/text()"));
        assertEquals("saml-admin@test.example", evaluate("//*[local-name()='EmailAddress']/text()"));
    }

    @Test
    public void attributeConsumingServiceRequestsTheUsernameAttribute() throws Exception {
        assertEquals("1", evaluate("count(//*[local-name()='AttributeConsumingService'])"));
        assertEquals("true", evaluate("//*[local-name()='AttributeConsumingService']/@isDefault"));
        assertEquals("0", evaluate("//*[local-name()='AttributeConsumingService']/@index"));
        assertEquals("Test Service", evaluate("//*[local-name()='ServiceName']/text()"));
        assertEquals("eduPersonPrincipalName", evaluate("//*[local-name()='RequestedAttribute']/@Name"));
    }

    @Test
    public void spCredentialsAreAdvertised() throws Exception {
        // one signing and one encryption KeyDescriptor, from the configured SP credentials
        assertEquals("signing", evaluate("//*[local-name()='KeyDescriptor'][1]/@use"));
        assertNotNull(evaluate("//*[local-name()='KeyDescriptor'][1]//*[local-name()='X509Certificate']"));
    }
}
