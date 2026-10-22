package org.fenixedu.bennu.saml.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.saml2.core.Saml2Error;
import org.springframework.security.saml2.core.Saml2ErrorCodes;
import org.springframework.security.saml2.provider.service.authentication.logout.OpenSamlLogoutRequestValidator;
import org.springframework.security.saml2.provider.service.authentication.logout.OpenSamlLogoutResponseValidator;
import org.springframework.security.saml2.provider.service.authentication.logout.Saml2LogoutRequestValidator;
import org.springframework.security.saml2.provider.service.authentication.logout.Saml2LogoutRequestValidatorParameters;
import org.springframework.security.saml2.provider.service.authentication.logout.Saml2LogoutResponseValidator;
import org.springframework.security.saml2.provider.service.authentication.logout.Saml2LogoutResponseValidatorParameters;
import org.springframework.security.saml2.provider.service.authentication.logout.Saml2LogoutValidatorResult;

/**
 * Delegating validator for SAML Single Logout messages that can tolerate unsigned messages.
 *
 * Spring Security 5.8 requires every SLO message to be signed (either an XML signature or a
 * signed redirect query). SimpleSAMLphp does not sign logout messages by default, so its
 * deployments need {@code saml.slo.requireSignedMessages = false}: signature errors are dropped
 * while every other validation (issuer, destination, subject, InResponseTo, status) still
 * applies. That leniency is an explicit opt-out; the default requires signatures.
 */
public class BennuSaml2LogoutMessageValidator implements Saml2LogoutRequestValidator, Saml2LogoutResponseValidator {

    private final boolean requireSignedMessages;
    private final OpenSamlLogoutRequestValidator requestDelegate = new OpenSamlLogoutRequestValidator();
    private final OpenSamlLogoutResponseValidator responseDelegate = new OpenSamlLogoutResponseValidator();

    public BennuSaml2LogoutMessageValidator(boolean requireSignedMessages) {
        this.requireSignedMessages = requireSignedMessages;
    }

    @Override
    public Saml2LogoutValidatorResult validate(Saml2LogoutRequestValidatorParameters parameters) {
        return filter(this.requestDelegate.validate(parameters), this.requireSignedMessages);
    }

    @Override
    public Saml2LogoutValidatorResult validate(Saml2LogoutResponseValidatorParameters parameters) {
        return filter(this.responseDelegate.validate(parameters), this.requireSignedMessages);
    }

    static Saml2LogoutValidatorResult filter(Saml2LogoutValidatorResult result, boolean requireSignedMessages) {
        if (requireSignedMessages || !result.hasErrors()) {
            return result;
        }
        List<Saml2Error> kept = new ArrayList<>();
        for (Saml2Error error : result.getErrors()) {
            if (!Saml2ErrorCodes.INVALID_SIGNATURE.equals(error.getErrorCode())) {
                kept.add(error);
            }
        }
        return Saml2LogoutValidatorResult.withErrors().errors(errors -> errors.addAll(kept)).build();
    }
}
