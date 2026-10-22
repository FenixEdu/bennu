package org.fenixedu.bennu.saml.client;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

public class SAMLClientConfiguration {

    @ConfigurationManager(description = "Bennu SAML Client Configuration")
    public static interface ConfigurationProperties {

        @ConfigurationProperty(key = "saml.enabled", defaultValue = "false", description = "Whether the SAML client is enabled")
        public Boolean samlEnabled();

        @ConfigurationProperty(key = "saml.username.attribute", defaultValue = "eduPersonPrincipalName",
                description = "The SAML assertion attribute that holds the Bennu username")
        public String usernameAttribute();

        @ConfigurationProperty(key = "saml.username.stripDomain", defaultValue = "true",
                description = "Whether to strip the @domain part from the received username attribute value")
        public Boolean usernameStripDomain();

        @ConfigurationProperty(key = "saml.sp.entityId",
                description = "The SP entity ID. Defaults to the application URL. Set this to the entity ID registered at "
                        + "your IdP before migrating from a previous release")
        public String spEntityId();

        @ConfigurationProperty(key = "saml.sp.certificate",
                description = "The SP X.509 certificate, inline (base64 or PEM). Used both to sign requests and to decrypt "
                        + "assertions, together with saml.sp.private-key")
        public String spCertificate();

        @ConfigurationProperty(key = "saml.sp.private-key",
                description = "The SP private key, inline (base64 or PEM, PKCS#8). Used both to sign requests and to decrypt "
                        + "assertions, together with saml.sp.certificate")
        public String spPrivateKey();

        @ConfigurationProperty(key = "saml.sp.private-key.password",
                description = "The password of saml.sp.private-key, only required for encrypted PKCS#8 keys")
        public String spPrivateKeyPassword();

        @ConfigurationProperty(key = "saml.idp.metadata",
                description = "The IdP metadata location. Supports http(s)://, classpath: and file: URLs")
        public String idpMetadata();

        @ConfigurationProperty(key = "saml.organization.name",
                description = "The organization name, emitted into the generated SP metadata (md:OrganizationName)")
        public String organizationName();

        @ConfigurationProperty(key = "saml.organization.displayName",
                description = "The organization display name, emitted into the generated SP metadata (md:OrganizationDisplayName)")
        public String organizationDisplayName();

        @ConfigurationProperty(key = "saml.organization.url",
                description = "The organization URL, emitted into the generated SP metadata (md:OrganizationURL)")
        public String organizationUrl();

        @ConfigurationProperty(key = "saml.organization.lang", defaultValue = "en",
                description = "The xml:lang of the organization metadata elements")
        public String organizationLang();

        @ConfigurationProperty(key = "saml.service.name",
                description = "The service name, emitted into the generated SP metadata (md:ServiceName). When set, the "
                        + "metadata also requests the saml.username.attribute (md:RequestedAttribute)")
        public String serviceName();

        @ConfigurationProperty(key = "saml.contact.type", defaultValue = "technical",
                description = "The contact type, emitted into the generated SP metadata (md:ContactPerson). "
                        + "One of: technical, support, administrative, billing, other")
        public String contactType();

        @ConfigurationProperty(key = "saml.contact.givenName",
                description = "The contact given name, emitted into the generated SP metadata (md:ContactPerson/md:GivenName)")
        public String contactGivenName();

        @ConfigurationProperty(key = "saml.contact.surname",
                description = "The contact surname, emitted into the generated SP metadata (md:ContactPerson/md:SurName)")
        public String contactSurname();

        @ConfigurationProperty(key = "saml.contact.email",
                description = "The contact email, emitted into the generated SP metadata (md:ContactPerson/md:EmailAddress)")
        public String contactEmail();

        @ConfigurationProperty(key = "saml.autoCreateUsers", defaultValue = "true",
                description = "Whether to create a Bennu user automatically when the authenticated user does not exist")
        public Boolean autoCreateUsers();

        @ConfigurationProperty(key = "saml.slo.enabled", defaultValue = "false",
                description = "Whether to enable SP-initiated SAML Single Logout, served at /api/saml-client/logout. "
                        + "Requires the IdP metadata to advertise a SingleLogoutService endpoint")
        public Boolean sloEnabled();

        @ConfigurationProperty(key = "saml.slo.requireSignedMessages", defaultValue = "true",
                description = "Whether SLO messages (LogoutRequest/LogoutResponse) must be signed. SimpleSAMLphp "
                        + "does not sign them by default; set to false for such IdPs (recommended only in "
                        + "test environments)")
        public Boolean sloRequireSignedMessages();

        @ConfigurationProperty(key = "saml.logoutSuccessUrl", defaultValue = "/login?logout",
                description = "Where to redirect the user after the Single Logout completes (context-relative by "
                        + "default; absolute URLs are also accepted)")
        public String logoutSuccessUrl();

        @ConfigurationProperty(key = "saml.loginFailedUrl", defaultValue = "/login?login_failed=true",
                description = "The context-relative URL to redirect to when the SAML authentication fails")
        public String loginFailedUrl();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

}
