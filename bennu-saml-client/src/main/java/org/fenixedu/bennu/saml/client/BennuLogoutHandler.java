package org.fenixedu.bennu.saml.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.security.Authenticate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * Logs the user out of Bennu (invalidating the session and firing the Bennu logout listeners)
 * before Spring Security redirects to the IdP to complete the Single Logout.
 *
 * The SAML endpoints run inside bennu-core's request transaction (see this module's
 * web-fragment ordering, which places the fragment after bennu-core's
 * {@code CloseTransactionFilter}): logout listeners can therefore touch the domain like they do
 * for any other request.
 */
public class BennuLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Authenticate.logout(request, response);
    }
}
