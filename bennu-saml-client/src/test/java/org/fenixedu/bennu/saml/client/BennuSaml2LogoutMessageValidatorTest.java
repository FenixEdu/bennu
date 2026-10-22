package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.security.saml2.core.Saml2Error;
import org.springframework.security.saml2.core.Saml2ErrorCodes;
import org.springframework.security.saml2.provider.service.authentication.logout.Saml2LogoutValidatorResult;

/**
 * Verifies the signature-leniency filtering of {@link BennuSaml2LogoutMessageValidator}: when
 * {@code saml.slo.requireSignedMessages = false} (an explicit opt-out; the default requires
 * signatures), only signature errors are dropped and every other validation error still fails
 * the message; when strict, nothing is dropped.
 */
public class BennuSaml2LogoutMessageValidatorTest {

    private static final Saml2Error MISSING_SIGNATURE = new Saml2Error(Saml2ErrorCodes.INVALID_SIGNATURE,
            "Missing signature algorithm for object [_test]");
    private static final Saml2Error WRONG_DESTINATION = new Saml2Error(Saml2ErrorCodes.INVALID_DESTINATION,
            "Invalid destination for object [_test]");

    @Test
    public void lenientFilteringDropsOnlySignatureErrors() {
        Saml2LogoutValidatorResult mixed = Saml2LogoutValidatorResult.withErrors(MISSING_SIGNATURE, WRONG_DESTINATION)
                .build();

        Saml2LogoutValidatorResult filtered = BennuSaml2LogoutMessageValidator.filter(mixed, false);

        assertTrue("Non-signature errors must survive the lenient filtering", filtered.hasErrors());
        assertEquals(Arrays.asList(WRONG_DESTINATION), new ArrayList<>(filtered.getErrors()));
    }

    @Test
    public void lenientFilteringAcceptsAMessageWhoseOnlyErrorsAreSignatureErrors() {
        Saml2LogoutValidatorResult signatureOnly = Saml2LogoutValidatorResult.withErrors(MISSING_SIGNATURE).build();

        Saml2LogoutValidatorResult filtered = BennuSaml2LogoutMessageValidator.filter(signatureOnly, false);

        assertFalse("A message failing only signature validation must be accepted", filtered.hasErrors());
    }

    @Test
    public void strictModeKeepsEveryError() {
        Saml2LogoutValidatorResult mixed = Saml2LogoutValidatorResult.withErrors(MISSING_SIGNATURE, WRONG_DESTINATION)
                .build();

        Saml2LogoutValidatorResult filtered = BennuSaml2LogoutMessageValidator.filter(mixed, true);

        assertTrue(filtered.hasErrors());
        assertEquals(Arrays.asList(MISSING_SIGNATURE, WRONG_DESTINATION), new ArrayList<>(filtered.getErrors()));
    }

    @Test
    public void successfulResultsPassThroughUnchanged() {
        Saml2LogoutValidatorResult success = Saml2LogoutValidatorResult.success();

        assertEquals(success, BennuSaml2LogoutMessageValidator.filter(success, false));
        assertEquals(success, BennuSaml2LogoutMessageValidator.filter(success, true));
    }
}
