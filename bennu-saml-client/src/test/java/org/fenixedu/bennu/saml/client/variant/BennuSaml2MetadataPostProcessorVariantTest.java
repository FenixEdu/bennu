package org.fenixedu.bennu.saml.client.variant;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.fenixedu.bennu.saml.client.BennuRelyingPartyRegistrationRepository;
import org.fenixedu.bennu.saml.client.BennuSaml2MetadataPostProcessor;
import org.fenixedu.bennu.saml.client.SAML2SecurityConfiguration;
import org.fenixedu.bennu.saml.client.TestSaml;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.w3c.dom.Document;

/**
 * Runs against configuration-variant.properties (no enrichment keys set): the generated metadata
 * must not contain Organization, ContactPerson or AttributeConsumingService elements.
 */
public class BennuSaml2MetadataPostProcessorVariantTest {

    private Document metadata;

    @Before
    public void generateMetadata() {
        BennuRelyingPartyRegistrationRepository repository = new BennuRelyingPartyRegistrationRepository();
        repository.setServletContext(new MockServletContext());
        RelyingPartyRegistration registration = repository.findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);

        OpenSamlMetadataResolver resolver = new OpenSamlMetadataResolver();
        resolver.setEntityDescriptorCustomizer(new BennuSaml2MetadataPostProcessor());
        this.metadata = TestSaml.parse(resolver.resolve(registration));
    }

    private int count(String localName) {
        return metadata.getDocumentElement().getElementsByTagNameNS("*", localName).getLength();
    }

    @Test
    public void baseMetadataIsStillGenerated() {
        assertEquals(TestSaml.SP_ENTITY_ID, metadata.getDocumentElement().getAttribute("entityID"));
        assertEquals(1, count("AssertionConsumerService"));
    }

    @Test
    public void optionalElementsAreAbsent() {
        assertEquals(0, count("Organization"));
        assertEquals(0, count("ContactPerson"));
        assertEquals(0, count("AttributeConsumingService"));
    }
}
