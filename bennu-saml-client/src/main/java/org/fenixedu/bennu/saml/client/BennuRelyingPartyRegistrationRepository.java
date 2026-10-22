package org.fenixedu.bennu.saml.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.ServletContext;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.web.context.ServletContextAware;

/**
 * Builds and caches the single {@link RelyingPartyRegistration} that describes this service
 * provider, from the {@code saml.*} configuration keys.
 *
 * The IdP side is derived from the configured IdP metadata (which also provides the verification
 * certificates and, when present, the IdP SLO endpoints). The SP side (entity ID, ACS, SLO and
 * signing/decryption credentials) is derived from configuration, with the entity ID and ACS
 * defaulting to values derived from the application URL.
 *
 * The registration is built when the Spring Security chain is created, at application startup:
 * a broken IdP metadata location or SP credentials fail the deployment instead of the first login
 * attempt. The built registration is cached for the lifetime of the application.
 */
public class BennuRelyingPartyRegistrationRepository
        implements RelyingPartyRegistrationRepository, Iterable<RelyingPartyRegistration>, ServletContextAware {

    private static final Logger logger = LoggerFactory.getLogger(BennuRelyingPartyRegistrationRepository.class);

    private static final Pattern PEM_HEADER = Pattern.compile("-{2,}(BEGIN|END)\\s+[A-Z0-9 ]*-{2,}");

    private final Object lock = new Object();

    private volatile RelyingPartyRegistration registration;

    private String contextPath = "";

    @Override
    public void setServletContext(ServletContext servletContext) {
        String path = servletContext.getContextPath();
        this.contextPath = path == null ? "" : path;
    }

    @Override
    public RelyingPartyRegistration findByRegistrationId(String registrationId) {
        if (!SAML2SecurityConfiguration.REGISTRATION_ID.equals(registrationId)) {
            return null;
        }
        return getRegistration();
    }

    /**
     * Satisfies {@link Iterable} (the 5.8 {@link RelyingPartyRegistrationRepository} does not
     * extend it itself; Spring Security's configurers probe with {@code instanceof Iterable}).
     */
    @Override
    public Iterator<RelyingPartyRegistration> iterator() {
        return Collections.singletonList(getRegistration()).iterator();
    }

    private RelyingPartyRegistration getRegistration() {
        RelyingPartyRegistration cached = this.registration;
        if (cached != null) {
            return cached;
        }
        synchronized (this.lock) {
            if (this.registration == null) {
                this.registration = buildRegistration();
            }
            return this.registration;
        }
    }

    private RelyingPartyRegistration buildRegistration() {
        SAMLClientConfiguration.ConfigurationProperties config = SAMLClientConfiguration.getConfiguration();
        String applicationUrl = stripTrailingSlash(CoreConfiguration.getConfiguration().applicationUrl()) + this.contextPath;

        logger.info("Building SAML service provider registration for entity ID <{}>, IdP metadata <{}>",
                firstNonBlank(config.spEntityId(), applicationUrl), config.idpMetadata());

        RelyingPartyRegistration.Builder builder = RelyingPartyRegistrations.fromMetadataLocation(config.idpMetadata())
                .registrationId(SAML2SecurityConfiguration.REGISTRATION_ID)
                .entityId(firstNonBlank(config.spEntityId(), applicationUrl))
                .assertionConsumerServiceLocation(applicationUrl + SAML2SecurityConfiguration.ASSERTION_CONSUMER_SERVICE_URL)
                .assertionConsumerServiceBinding(Saml2MessageBinding.POST);

        RSAPrivateKey privateKey = parsePrivateKey(config);
        X509Certificate certificate = parseCertificate(config);
        if (privateKey != null && certificate != null) {
            builder.signingX509Credentials(credentials -> credentials
                    .add(Saml2X509Credential.signing(privateKey, certificate)));
            builder.decryptionX509Credentials(credentials -> credentials
                    .add(Saml2X509Credential.decryption(privateKey, certificate)));
        } else {
            logger.warn("No SP certificate/private key configured: AuthnRequests and assertions will not be signed/decrypted. "
                    + "Configure saml.sp.certificate and saml.sp.private-key");
        }

        if (Boolean.TRUE.equals(config.sloEnabled())) {
            builder.singleLogoutServiceLocation(applicationUrl + SAML2SecurityConfiguration.LOGOUT_URL);
            builder.singleLogoutServiceResponseLocation(applicationUrl + SAML2SecurityConfiguration.LOGOUT_URL);
        } else {
            logger.info("SAML Single Logout is disabled (saml.slo.enabled = false)");
        }

        return builder.build();
    }

    private RSAPrivateKey parsePrivateKey(SAMLClientConfiguration.ConfigurationProperties config) {
        String value = config.spPrivateKey();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            byte[] encoded = Base64.getMimeDecoder().decode(stripPemHeaders(value));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            String password = config.spPrivateKeyPassword();
            if (password != null && !password.isEmpty()) {
                keySpec = decryptKeySpec(encoded, password);
            }
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse saml.sp.private-key. The key must be PKCS#8, inline as base64 "
                    + "or PEM (convert PKCS#1 keys with: openssl pkcs8 -topk8 -nocrypt)", e);
        }
    }

    private PKCS8EncodedKeySpec decryptKeySpec(byte[] encoded, String password)
            throws IOException, GeneralSecurityException {
        EncryptedPrivateKeyInfo encrypted = new EncryptedPrivateKeyInfo(encoded);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encrypted.getAlgName());
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
        return encrypted.getKeySpec(key);
    }

    private X509Certificate parseCertificate(SAMLClientConfiguration.ConfigurationProperties config) {
        String value = config.spCertificate();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            byte[] encoded = Base64.getMimeDecoder().decode(stripPemHeaders(value));
            return (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(encoded));
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse saml.sp.certificate", e);
        }
    }

    private static String stripPemHeaders(String value) {
        return PEM_HEADER.matcher(value).replaceAll("");
    }

    private static String stripTrailingSlash(String url) {
        return url != null && url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static String firstNonBlank(String first, String second) {
        return first != null && !first.trim().isEmpty() ? first : second;
    }
}
