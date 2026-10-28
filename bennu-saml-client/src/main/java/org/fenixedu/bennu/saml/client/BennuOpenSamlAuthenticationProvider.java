package org.fenixedu.bennu.saml.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.ConditionValidator;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.SubjectConfirmationValidator;
import org.opensaml.saml.saml2.assertion.impl.AudienceRestrictionConditionValidator;
import org.opensaml.saml.saml2.assertion.impl.BearerSubjectConfirmationValidator;
import org.opensaml.saml.saml2.assertion.impl.DelegationRestrictionConditionValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.OneTimeUse;
import org.opensaml.saml.saml2.core.ProxyRestriction;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.core.Saml2Error;
import org.springframework.security.saml2.core.Saml2ErrorCodes;
import org.springframework.security.saml2.core.Saml2ResponseValidatorResult;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.OpenSamlAuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationException;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * The OpenSAML 3 engine used by the SAML filter chain, with the behaviours of the OpenSAML 4
 * provider that the OpenSAML 3 default configuration lacks restored:
 *
 * <ul>
 * <li>the {@code SessionIndex} values of the assertion's {@code AuthnStatement}s are carried
 * into the {@link DefaultSaml2AuthenticatedPrincipal}, where
 * {@link BennuAuthenticationSuccessHandler} reads them to build the stored identity;</li>
 * <li>assertion validation runs on Bennu's own {@link SAML20AssertionValidator} — a copy of the
 * one Spring assembles for the stock provider, plus a {@code ProxyRestriction} condition
 * validator (absent from OpenSAML 3.4.6, which fails the assertion as an unknown condition), a
 * Bearer validator carrying OpenSAML 4's InResponseTo rule, and IssueInstant freshness
 * (OpenSAML 4 validateBasicData);</li>
 * <li>the assertion {@code Issuer} must be the configured IdP's entity ID, which the OpenSAML 3
 * default validator does not compare;</li>
 * <li>the {@code InResponseTo} of the {@code <Response>} element itself is checked against the
 * stored request, so SP-initiated responses cannot be replayed across flows; unsolicited
 * (IdP-initiated) responses carry no InResponseTo and remain accepted; the stored request is
 * only decoded when there is an InResponseTo to compare.</li>
 * </ul>
 *
 * {@link OpenSamlAuthenticationProvider} is final, so this class delegates.
 */
public class BennuOpenSamlAuthenticationProvider implements AuthenticationProvider {

    // OpenSAML 4 carries the expected InResponseTo in the validation context under its own
    // parameter; OpenSAML 3.4.6 has no such parameter, so Bennu owns the key
    private static final String VALID_IN_RESPONSE_TO = "bennu.saml.SubjectConfirmation.ValidInResponseTo";

    // OpenSAML 4.3.2's DEFAULT_LIFETIME; Spring never sets the lifetime validation parameter
    private static final long ASSERTION_LIFETIME_MILLIS = Duration.ofMinutes(5).toMillis();

    private final OpenSamlAuthenticationProvider delegate = new OpenSamlAuthenticationProvider();

    // composed converters are stateless: build them once, not per authentication
    private final Converter<OpenSamlAuthenticationProvider.ResponseToken, Saml2Authentication> defaultResponseAuthenticationConverter =
            OpenSamlAuthenticationProvider.createDefaultResponseAuthenticationConverter();

    private static final SAML20AssertionValidator BENNU_ASSERTION_VALIDATOR = createBennuAssertionValidator();

    public BennuOpenSamlAuthenticationProvider() {
        this.delegate.setResponseAuthenticationConverter(this::convertWithSessionIndexes);
        this.delegate.setAssertionValidator(this::validateAssertion);
    }

    private Saml2Authentication convertWithSessionIndexes(OpenSamlAuthenticationProvider.ResponseToken responseToken) {
        Saml2Authentication authentication = this.defaultResponseAuthenticationConverter.convert(responseToken);
        if (!(authentication.getPrincipal() instanceof DefaultSaml2AuthenticatedPrincipal)) {
            return authentication;
        }
        DefaultSaml2AuthenticatedPrincipal principal = (DefaultSaml2AuthenticatedPrincipal) authentication.getPrincipal();
        List<String> sessionIndexes = new ArrayList<>();
        Assertion assertion = CollectionUtils.firstElement(responseToken.getResponse().getAssertions());
        if (assertion != null) {
            for (AuthnStatement statement : assertion.getAuthnStatements()) {
                if (StringUtils.hasText(statement.getSessionIndex())) {
                    sessionIndexes.add(statement.getSessionIndex());
                }
            }
        }
        // the principal built by the default converter carries an immutable empty session index
        // list, so build an enriched copy
        DefaultSaml2AuthenticatedPrincipal enriched = new DefaultSaml2AuthenticatedPrincipal(principal.getName(),
                principal.getAttributes(), sessionIndexes);
        enriched.setRelyingPartyRegistrationId(principal.getRelyingPartyRegistrationId());
        return new Saml2Authentication(enriched, authentication.getSaml2Response(), authentication.getAuthorities());
    }

    /**
     * Assertion validation on Bennu's own OpenSAML validator (see {@link #BENNU_ASSERTION_VALIDATOR}),
     * plus the issuer and response-level InResponseTo checks the OpenSAML 3 defaults lack.
     *
     * <p>
     * The stored authentication request is decoded at most once per assertion, when either the
     * response or the assertion itself carries an InResponseTo — the same shape
     * {@code OpenSaml4AuthenticationProvider} uses (its createValidationContext decodes it per
     * assertion carrying an InResponseTo).
     */
    private Saml2ResponseValidatorResult validateAssertion(OpenSamlAuthenticationProvider.AssertionToken assertionToken) {
        String responseInResponseTo = responseInResponseTo(assertionToken);
        boolean assertionCarriesInResponseTo = assertionContainsInResponseTo(assertionToken.getAssertion());
        AuthnRequest storedRequest = null;
        if (responseInResponseTo != null || assertionCarriesInResponseTo) {
            storedRequest = parseStoredRequest(assertionToken.getToken().getAuthenticationRequest());
        }
        Saml2ResponseValidatorResult result = validateWithBennuValidator(assertionToken, storedRequest);
        result = result.concat(checkIssuer(assertionToken));
        if (responseInResponseTo != null) {
            result = result.concat(checkInResponseTo(responseInResponseTo, storedRequest));
        }
        return result;
    }

    /**
     * The assertion {@code Issuer} must name the configured IdP. The OpenSAML 3 default
     * validator does not compare it (the OpenSAML 4 provider does, through the
     * {@code ValidIssuers} validation parameter), so an assertion from a validly signed response
     * that names a different issuer would otherwise be accepted.
     */
    private Saml2ResponseValidatorResult checkIssuer(OpenSamlAuthenticationProvider.AssertionToken assertionToken) {
        String issuer = assertionToken.getAssertion().getIssuer() == null ? null
                : assertionToken.getAssertion().getIssuer().getValue();
        String expected = assertionToken.getToken().getRelyingPartyRegistration().getAssertingPartyDetails()
                .getEntityId();
        if (expected.equals(issuer)) {
            return Saml2ResponseValidatorResult.success();
        }
        return Saml2ResponseValidatorResult.failure(new Saml2Error(Saml2ErrorCodes.INVALID_ISSUER,
                "Assertion issuer [" + issuer + "] is not the configured asserting party [" + expected + "]"));
    }

    private Saml2ResponseValidatorResult checkInResponseTo(String inResponseTo, AuthnRequest storedRequest) {
        if (!StringUtils.hasText(inResponseTo)) {
            return Saml2ResponseValidatorResult.success();
        }
        if (storedRequest == null) {
            return Saml2ResponseValidatorResult.failure(new Saml2Error(Saml2ErrorCodes.INVALID_IN_RESPONSE_TO,
                    "The response contained an InResponseTo attribute [" + inResponseTo
                            + "] but no saved authentication request was found"));
        }
        if (!inResponseTo.equals(storedRequest.getID())) {
            return Saml2ResponseValidatorResult.failure(new Saml2Error(Saml2ErrorCodes.INVALID_IN_RESPONSE_TO,
                    "The InResponseTo attribute [" + inResponseTo + "] does not match the ID of the authentication"
                            + " request [" + storedRequest.getID() + "]"));
        }
        return Saml2ResponseValidatorResult.success();
    }

    /**
     * The InResponseTo of the {@code <Response>} element, or {@code null} when absent or when the
     * assertion being validated is not the response's first one (the response-level check runs
     * once, so multi-assertion responses do not produce duplicate errors).
     */
    private static String responseInResponseTo(OpenSamlAuthenticationProvider.AssertionToken assertionToken) {
        Assertion assertion = assertionToken.getAssertion();
        if (!(assertion.getParent() instanceof Response)) {
            return null;
        }
        Response response = (Response) assertion.getParent();
        if (CollectionUtils.firstElement(response.getAssertions()) != assertion) {
            return null;
        }
        return StringUtils.hasText(response.getInResponseTo()) ? response.getInResponseTo() : null;
    }

    /**
     * Assertion validation on {@link #BENNU_ASSERTION_VALIDATOR} with the same error-code mapping
     * shape the stock provider uses (any exception becomes an {@code invalid_assertion} result),
     * except that a rejected assertion none of whose Bearer confirmations answers the stored
     * request is reported as {@code invalid_in_response_to} (see {@link #assertionErrorCode}).
     */
    private Saml2ResponseValidatorResult validateWithBennuValidator(
            OpenSamlAuthenticationProvider.AssertionToken assertionToken, AuthnRequest storedRequest) {
        Assertion assertion = assertionToken.getAssertion();
        ValidationContext context = createValidationContext(assertionToken, storedRequest);
        try {
            if (BENNU_ASSERTION_VALIDATOR.validate(assertion, context) == ValidationResult.VALID) {
                return Saml2ResponseValidatorResult.success();
            }
            return Saml2ResponseValidatorResult.failure(new Saml2Error(assertionErrorCode(assertion, storedRequest),
                    failureDescription(assertion, context.getValidationFailureMessage())));
        } catch (Exception ex) {
            return Saml2ResponseValidatorResult.failure(
                    new Saml2Error(Saml2ErrorCodes.INVALID_ASSERTION, failureDescription(assertion, ex.getMessage())));
        }
    }

    /**
     * {@code invalid_in_response_to} when the assertion carries Bearer InResponseTo values and
     * none of them is the stored request's ID (or there is no stored request); otherwise
     * {@code invalid_assertion}. When some confirmation does answer the request, the rejection
     * has another cause (time, recipient, conditions) and is reported as such.
     */
    private static String assertionErrorCode(Assertion assertion, AuthnRequest storedRequest) {
        if (assertion.getSubject() == null) {
            return Saml2ErrorCodes.INVALID_ASSERTION;
        }
        String expected = storedRequest == null ? null : storedRequest.getID();
        boolean carriesInResponseTo = false;
        for (SubjectConfirmation confirmation : assertion.getSubject().getSubjectConfirmations()) {
            String inResponseTo = bearerInResponseTo(confirmation);
            if (inResponseTo == null) {
                continue;
            }
            if (inResponseTo.equals(expected)) {
                return Saml2ErrorCodes.INVALID_ASSERTION;
            }
            carriesInResponseTo = true;
        }
        return carriesInResponseTo ? Saml2ErrorCodes.INVALID_IN_RESPONSE_TO : Saml2ErrorCodes.INVALID_ASSERTION;
    }

    /**
     * The trimmed InResponseTo of a Bearer confirmation, or {@code null} when the confirmation is
     * not Bearer or carries no (or a blank) InResponseTo — OpenSAML 4 treats a blank value as
     * absent.
     */
    private static String bearerInResponseTo(SubjectConfirmation confirmation) {
        if (!SubjectConfirmation.METHOD_BEARER.equals(confirmation.getMethod())) {
            return null;
        }
        SubjectConfirmationData data = confirmation.getSubjectConfirmationData();
        if (data == null || !StringUtils.hasText(data.getInResponseTo())) {
            return null;
        }
        return data.getInResponseTo().trim();
    }

    private static String failureDescription(Assertion assertion, String reason) {
        String responseId = assertion.getParent() instanceof Response ? ((Response) assertion.getParent()).getID() : null;
        return String.format("Invalid assertion [%s] for SAML response [%s]: %s", assertion.getID(), responseId, reason);
    }

    private static ValidationContext createValidationContext(
            OpenSamlAuthenticationProvider.AssertionToken assertionToken, AuthnRequest storedRequest) {
        RelyingPartyRegistration registration = assertionToken.getToken().getRelyingPartyRegistration();
        Map<String, Object> params = new HashMap<>();
        // mirrors OpenSamlAuthenticationProvider.createValidationContext
        params.put(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES,
                Collections.singleton(registration.getEntityId()));
        params.put(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS,
                Collections.singleton(registration.getAssertionConsumerServiceLocation()));
        // mirrors OpenSaml4AuthenticationProvider.createValidationContext: the InResponseTo
        // expectation is only populated when a stored request is available
        if (storedRequest != null) {
            params.put(VALID_IN_RESPONSE_TO, storedRequest.getID());
        }
        return new ValidationContext(params);
    }

    /**
     * Whether any subject confirmation of the assertion (of any method) carries an InResponseTo.
     * Mirrors the OpenSAML 4 provider's gate for populating the InResponseTo expectation.
     */
    private static boolean assertionContainsInResponseTo(Assertion assertion) {
        if (assertion.getSubject() == null) {
            return false;
        }
        for (SubjectConfirmation confirmation : assertion.getSubject().getSubjectConfirmations()) {
            SubjectConfirmationData data = confirmation.getSubjectConfirmationData();
            if (data != null && StringUtils.hasText(data.getInResponseTo())) {
                return true;
            }
        }
        return false;
    }

    /**
     * A copy of the validator Spring assembles for the stock provider
     * ({@code OpenSamlAuthenticationProvider.SAML20AssertionValidators}), with three additions:
     * a {@code ProxyRestriction} condition validator (OpenSAML 3.4.6 ships none and fails the
     * assertion as an unknown condition; OpenSAML 4.3.2's validator permits the condition), a
     * Bearer subject confirmation validator carrying OpenSAML 4's InResponseTo rule, and
     * IssueInstant freshness (OpenSAML 4's validateBasicData). Signature validation is left to
     * the engine's own signature validator, exactly as in Spring's copy.
     */
    private static SAML20AssertionValidator createBennuAssertionValidator() {
        Collection<ConditionValidator> conditions = new ArrayList<>();
        conditions.add(new AudienceRestrictionConditionValidator());
        conditions.add(new DelegationRestrictionConditionValidator());
        conditions.add(new ConditionValidator() {
            @Override
            public QName getServicedCondition() {
                return OneTimeUse.DEFAULT_ELEMENT_NAME;
            }

            @Override
            public ValidationResult validate(Condition condition, Assertion assertion, ValidationContext context) {
                // applications should validate their own OneTimeUse conditions (mirrors Spring)
                return ValidationResult.VALID;
            }
        });
        conditions.add(new ConditionValidator() {
            @Override
            public QName getServicedCondition() {
                return ProxyRestriction.DEFAULT_ELEMENT_NAME;
            }

            @Override
            public ValidationResult validate(Condition condition, Assertion assertion, ValidationContext context) {
                // OpenSAML 4.3.2's ProxyRestrictionConditionValidator permits the condition
                return ValidationResult.VALID;
            }
        });
        Collection<SubjectConfirmationValidator> subjects = new ArrayList<>();
        subjects.add(new BennuBearerSubjectConfirmationValidator());
        return new SAML20AssertionValidator(conditions, subjects, new ArrayList<>(), null, null) {
            @Override
            protected ValidationResult validateSignature(Assertion token, ValidationContext context) {
                return ValidationResult.VALID;
            }

            /**
             * OpenSAML 4.3.2's validateBasicData checks the version and then the IssueInstant,
             * before the signature, conditions and subject checks; OpenSAML 3.4.6 checks only the
             * version at that point. Mirror the missing freshness rule (see
             * {@link #validateIssueInstant}).
             */
            @Override
            protected ValidationResult validateVersion(Assertion assertion, ValidationContext context)
                    throws org.opensaml.saml.common.assertion.AssertionValidationException {
                ValidationResult result = super.validateVersion(assertion, context);
                if (result != ValidationResult.VALID) {
                    return result;
                }
                return validateIssueInstant(assertion, context);
            }
        };
    }

    /**
     * OpenSAML 4.3.2's validateIssueInstant rules: the IssueInstant is required, must not be
     * further than the clock skew into the future, and must not be older than the assertion
     * lifetime plus skew (5 + 5 minutes). OpenSAML 3.4.6 lacks the check, and without it a
     * captured response can be replayed until the IdP's NotOnOrAfter expires.
     */
    private static ValidationResult validateIssueInstant(Assertion assertion, ValidationContext context) {
        DateTime issueInstant = assertion.getIssueInstant();
        if (issueInstant == null) {
            context.setValidationFailureMessage(String.format(
                    "Assertion '%s' did not contain the required IssueInstant", assertion.getID()));
            return ValidationResult.INVALID;
        }
        long skew = SAML20AssertionValidator.getClockSkew(context);
        long now = System.currentTimeMillis();
        if (issueInstant.getMillis() > now + skew) {
            context.setValidationFailureMessage("Assertion IssueInstant was invalid, issued in future");
            return ValidationResult.INVALID;
        }
        if (issueInstant.getMillis() + skew + ASSERTION_LIFETIME_MILLIS < now) {
            context.setValidationFailureMessage("Assertion IssueInstant was invalid, expired");
            return ValidationResult.INVALID;
        }
        return ValidationResult.VALID;
    }

    private static class BennuBearerSubjectConfirmationValidator extends BearerSubjectConfirmationValidator {

        @Override
        protected ValidationResult validateAddress(SubjectConfirmation confirmation, Assertion assertion,
                ValidationContext context) {
            // applications should validate their own addresses - gh-7514 (mirrors Spring's copy)
            return ValidationResult.VALID;
        }

        /**
         * Runs after OpenSAML has validated NotBefore/NotOnOrAfter/Recipient on the SAME
         * confirmation, so the InResponseTo rule below composes with them per confirmation — the
         * OpenSAML 4 semantics the stock OpenSAML 3.4.6 validator lacks.
         */
        @Override
        protected ValidationResult doValidate(SubjectConfirmation confirmation, Assertion assertion,
                ValidationContext context) throws org.opensaml.saml.common.assertion.AssertionValidationException {
            ValidationResult result = super.doValidate(confirmation, assertion, context);
            if (result != ValidationResult.VALID) {
                return result;
            }
            String inResponseTo = bearerInResponseTo(confirmation);
            if (inResponseTo == null) {
                // OpenSAML 4.3.2's validateInResponseTo: InResponseTo is not a required field
                // (Spring never sets InResponseToRequired), a missing or blank value is valid
                return ValidationResult.VALID;
            }
            String expected = (String) context.getStaticParameters().get(VALID_IN_RESPONSE_TO);
            if (expected == null) {
                // the assertion carries an InResponseTo but there is no stored request to match:
                // inconclusive — it still fails the assertion when no other confirmation is met
                return ValidationResult.INDETERMINATE;
            }
            return expected.equals(inResponseTo) ? ValidationResult.VALID : ValidationResult.INVALID;
        }
    }

    private static AuthnRequest parseStoredRequest(AbstractSaml2AuthenticationRequest storedRequest) {
        if (storedRequest == null || !StringUtils.hasText(storedRequest.getSamlRequest())) {
            return null;
        }
        try {
            // decoding (Base64, inflate) inside the try: a corrupt stored request is malformed
            // request data, not an internal error
            String xml = decodeSamlRequest(storedRequest);
            Element element = XMLObjectProviderRegistrySupport.getParserPool()
                    .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))).getDocumentElement();
            Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory()
                    .getUnmarshaller(AuthnRequest.DEFAULT_ELEMENT_NAME);
            return (AuthnRequest) unmarshaller.unmarshall(element);
        } catch (Exception ex) {
            throw new Saml2AuthenticationException(new Saml2Error(Saml2ErrorCodes.MALFORMED_REQUEST_DATA,
                    "Failed to deserialize the associated authentication request [" + ex.getMessage() + "]"), ex);
        }
    }

    private static String decodeSamlRequest(AbstractSaml2AuthenticationRequest storedRequest) {
        byte[] decoded = Base64.getMimeDecoder().decode(storedRequest.getSamlRequest());
        if (storedRequest.getBinding() == Saml2MessageBinding.REDIRECT) {
            decoded = inflate(decoded);
        }
        return new String(decoded, StandardCharsets.UTF_8);
    }

    // package-private: reused by TestSaml so the tests exercise the production inflater
    static byte[] inflate(byte[] deflated) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Inflater inflater = new Inflater(true);
        try (InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(bytes, inflater)) {
            // the same construction Spring's own Saml2Utils.samlInflate uses
            inflaterOutputStream.write(deflated);
            inflaterOutputStream.finish();
        } catch (IOException ex) {
            throw new IllegalStateException("Could not inflate SAML message", ex);
        } finally {
            inflater.end();
        }
        return bytes.toByteArray();
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        return this.delegate.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return this.delegate.supports(authentication);
    }
}
