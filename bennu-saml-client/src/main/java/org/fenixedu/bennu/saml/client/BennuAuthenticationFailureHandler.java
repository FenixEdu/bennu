package org.fenixedu.bennu.saml.client;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * Redirects to the configured failure URL ({@code saml.loginFailedUrl}) when the SAML
 * authentication fails, always logging the reason.
 */
public class BennuAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(BennuAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        // the login flow ends here: drop its callback so it cannot redirect a later
        // IdP-initiated login
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE);
        }
        logger.warn("SAML authentication failed: {}", exception.getMessage());
        logger.debug("SAML authentication failure details", exception);
        String failureUrl = SAMLClientConfiguration.getConfiguration().loginFailedUrl();
        response.sendRedirect(request.getContextPath() + failureUrl);
    }
}
