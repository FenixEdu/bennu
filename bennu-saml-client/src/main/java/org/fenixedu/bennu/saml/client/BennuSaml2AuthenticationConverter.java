package org.fenixedu.bennu.saml.client;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationTokenConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;

/**
 * Converts the SAML response received at the assertion consumer service into a
 * {@code Saml2AuthenticationToken}.
 *
 * The default Spring Security converter resolves the relying party registration from a
 * {@code {registrationId}} path variable, which the fixed Bennu ACS URL does not have. This
 * converter always resolves the single static registration configured for this module, and
 * delegates to Spring Security's converter for the response decoding (and inflation, for
 * redirect-binding responses).
 *
 * IdP-initiated (unsolicited) responses, which have no stored authentication request, are supported.
 */
public class BennuSaml2AuthenticationConverter implements AuthenticationConverter {

    private final Saml2AuthenticationTokenConverter delegate;

    public BennuSaml2AuthenticationConverter(RelyingPartyRegistrationRepository repository) {
        this.delegate = new Saml2AuthenticationTokenConverter(
                (request, registrationId) -> repository
                        .findByRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID));
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        return this.delegate.convert(request);
    }
}
