package org.fenixedu.bennu.saml.client;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.saml2.core.Saml2Error;
import org.springframework.security.saml2.core.Saml2ErrorCodes;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationException;

/**
 * Verifies that a failed SAML login ends its flow: the stored callback is dropped, so it cannot
 * redirect a later IdP-initiated login.
 */
public class BennuAuthenticationFailureHandlerTest {

    @Test
    public void dropsTheSessionCallback() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE, "/deep/link");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);

        new BennuAuthenticationFailureHandler().onAuthenticationFailure(request, new MockHttpServletResponse(),
                new Saml2AuthenticationException(new Saml2Error(Saml2ErrorCodes.INVALID_ASSERTION, "test")));

        assertNull(session.getAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE));
    }
}
