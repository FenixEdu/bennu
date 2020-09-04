package org.fenixedu.bennu.saml.client;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.xmlsec.config.DecryptionParserPool;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.apache.wss4j.common.saml.OpenSAMLUtil;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

public class SAMLClientSDK {

    private static final SAML2Configuration CONFIG = getNewDefaultConfiguration();

    private static final List<String> AUT_CONTEXT_CLASSES_LDAP =
            new ArrayList<>(Arrays.asList("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport"));

    private static final List<String> AUT_CONTEXT_CLASSES_GOV = new ArrayList<>(
            Arrays.asList("urn:oasis:names:tc:SAML:2.0:ac:classes:SmartcardPKI",
                    "urn:oasis:names:tc:SAML:2.0:ac:classes:MobileTwoFactorContract"));

    // These are the attributes that are necessary for the IDP to work with the GOV IDP
    private static final String[] GOV_REQUIRED_ATTRIBUTES = new String[] { "NIC" };

    private static SAML2Configuration getNewDefaultConfiguration() {
        SAML2Configuration config = new SAML2Configuration(SAMLClientConfiguration.getConfiguration().keystorePath(),
                SAMLClientConfiguration.getConfiguration().keystorePassword(),
                SAMLClientConfiguration.getConfiguration().privateKeyPassword(),
                SAMLClientConfiguration.getConfiguration().identityProviderMetadataPath());

        config.setAuthnRequestBindingType(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
        config.setResponseBindingType(SAMLConstants.SAML2_POST_BINDING_URI);

        config.setAuthnRequestSigned(true);
        config.setWantsResponsesSigned(true);
        config.setMaximumAuthenticationLifetime(3600 * 8);
        // custom SP entity ID
        config.setServiceProviderEntityId(SAMLClientConfiguration.getConfiguration().serviceProviderEntityId());
        config.setForceServiceProviderMetadataGeneration(true);

        config.setServiceProviderMetadataPath(
                new File(SAMLClientConfiguration.getConfiguration().serviceProviderMetadataGenerationDestinationPath())
                        .getAbsolutePath());
        return config;
    }

    private static SAML2Configuration getNewConfiguration(String[] govAttributesToRequire) {
        SAML2Configuration config = getNewDefaultConfiguration();

        Supplier<List<XSAny>> requestExtensions = () -> {
            List<XSAny> extensionList = new ArrayList<>();

            /*
            <samlp:Extensions>
            <RequestedAttributes xmlns="http://autenticacao.cartaodecidadao.pt/atributos">
              <RequestedAttribute Name="http://interop.gov.pt/MDC/Cidadao/NIC" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri" isRequired="true"/>
            </RequestedAttributes>
            <FAAALevel xmlns="http://autenticacao.cartaodecidadao.pt/atributos">3</FAAALevel>
            </samlp:Extensions>
            */

            XSAny requestedAttributes = new XSAnyBuilder()
                    .buildObject(new QName("http://autenticacao.cartaodecidadao.pt/atributos", "RequestedAttributes"));
            extensionList.add(requestedAttributes);

            HashSet<String> attributesToRequire = new HashSet<>(Arrays.asList(GOV_REQUIRED_ATTRIBUTES));
            attributesToRequire.addAll(Arrays.asList(govAttributesToRequire));

            for (String name : attributesToRequire) {
                XSAny requestedAttribute = new XSAnyBuilder()
                        .buildObject(new QName("http://autenticacao.cartaodecidadao.pt/atributos", "RequestedAttribute"));
                requestedAttribute.getUnknownAttributes().put(new QName("Name"), "http://interop.gov.pt/MDC/Cidadao/" + name);
                requestedAttribute.getUnknownAttributes()
                        .put(new QName("NameFormat"), "urn:oasis:names:tc:SAML:2.0:attrname-format:uri");
                requestedAttribute.getUnknownAttributes().put(new QName("isRequired"), "true");
                requestedAttributes.getUnknownXMLObjects().add(requestedAttribute);
            }

            XSAny FAAALevel =
                    new XSAnyBuilder().buildObject(new QName("http://autenticacao.cartaodecidadao.pt/atributos", "FAAALevel"));
            FAAALevel.setTextContent("3");
            extensionList.add(FAAALevel);

            return extensionList;
        };
        config.setAuthnRequestExtensions(requestExtensions);

        return config;
    }

    private static final SAML2Client CLIENT = new SAML2Client(CONFIG);

    static {
        CLIENT.setCallbackUrl(SAMLClientConfiguration.getConfiguration().callbackUrl());

        ConfigurationService.register(DecryptionParserPool.class, new DecryptionParserPool(OpenSAMLUtil.getParserPool()));
    }

    public static SAML2Client getClient() {
        return CLIENT;
    }

    public static SAML2Client getClientForGov(String[] govAttributesToRequire, String callbackUrl) {
        SAML2Configuration config = getNewConfiguration(govAttributesToRequire);
        config.setAuthnContextClassRefs(AUT_CONTEXT_CLASSES_GOV);
        config.setComparisonType("exact"); // ignored by SP but needed for pac4f to use context classes
        SAML2Client client = new SAML2Client(config);
        client.setCallbackUrl(callbackUrl);
        return client;
    }

    public static SAML2Client getClient(String callbackUrl) {
        SAML2Client client = new SAML2Client(CONFIG);
        client.setCallbackUrl(callbackUrl);
        return client;
    }

    public static SAML2Client getClientWithSpecificAuthContextClasses(List<String> authContextClasses,
            String[] govAttributesToRequire, String callbackUrl) {
        SAML2Configuration config;
        if (govAttributesToRequire != null) {
            config = getNewConfiguration(govAttributesToRequire);
        } else {
            config = getNewDefaultConfiguration();
        }

        if (authContextClasses != null) {
            config.setAuthnContextClassRefs(authContextClasses);
            config.setComparisonType("exact"); // ignored by SP but needed for pac4f to use context classes
        }

        SAML2Client client = new SAML2Client(config);
        if (callbackUrl != null) {
            client.setCallbackUrl(callbackUrl);
        } else {
            client.setCallbackUrl(SAMLClientConfiguration.getConfiguration().callbackUrl());
        }
        return client;
    }
}
