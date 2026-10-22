package org.fenixedu.bennu.saml.client;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.login.LoginProvider;
import org.fenixedu.bennu.saml.client.servlet.SAMLClientInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.authentication.logout.OpenSaml4LogoutRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2RelyingPartyInitiatedLogoutSuccessHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Initiates the SAML login by redirecting the user to the Spring Security authentication request
 * endpoint ({@code /saml2/authenticate/{registrationId}}), which forwards the user to the IdP.
 *
 * The PortalLoginServlet callback is carried through the IdP round trip via RelayState, with a
 * session-based fallback for IdPs that do not echo the RelayState back.
 */
public class SAMLLoginProvider implements LoginProvider {

    private static final Logger logger = LoggerFactory.getLogger(SAMLLoginProvider.class);

    @Override
    public void showLogin(final HttpServletRequest request, final HttpServletResponse response, String callback)
            throws IOException, ServletException {
        Authenticate.logout(request, response);

        boolean hasCallback = callback != null && !callback.trim().isEmpty();
        if (hasCallback) {
            request.getSession(true).setAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE, callback);
        }

        String authenticationUrl = request.getContextPath() + SAML2SecurityConfiguration.AUTHENTICATION_REQUEST_URL;
        if (hasCallback) {
            authenticationUrl += "?callback=" + URLEncoder.encode(callback, StandardCharsets.UTF_8.name());
        }
        logger.debug("Redirecting to SAML IdP via {}", authenticationUrl);
        response.sendRedirect(authenticationUrl);
    }

    @Override
    public String getKey() {
        return "saml";
    }

    @Override
    public String getName() {
        return "SAML";
    }

    @Override
    public boolean isEnabled() {
        return SAMLClientConfiguration.getConfiguration().samlEnabled();
    }

    @Override
    public boolean logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SAMLClientConfiguration.ConfigurationProperties config = SAMLClientConfiguration.getConfiguration();
        if (!Boolean.TRUE.equals(config.sloEnabled())) {
            return false;
        }
        HttpSession session = request.getSession(false);
        BennuSecurityContextRepository.StoredSamlIdentity identity = session == null ? null
                : (BennuSecurityContextRepository.StoredSamlIdentity) session
                        .getAttribute(BennuSecurityContextRepository.SAML_IDENTITY_SESSION_ATTRIBUTE);
        if (identity == null) {
            // not a SAML session (or SLO state gone): plain local logout is enough
            return false;
        }

        BennuRelyingPartyRegistrationRepository repository = registrationRepository(request);
        if (repository == null) {
            return false;
        }

        // Resolve, store (cookie) and redirect: the IdP Single Logout round trip. The caller
        // terminates the local session right after — the pending request survives in the cookie.
        Saml2RelyingPartyInitiatedLogoutSuccessHandler logoutHandler = new Saml2RelyingPartyInitiatedLogoutSuccessHandler(
                new OpenSaml4LogoutRequestResolver(new DefaultRelyingPartyRegistrationResolver(repository)));
        logoutHandler.setLogoutRequestRepository(new CookieSaml2LogoutRequestRepository(repository));
        logoutHandler.onLogoutSuccess(request, response,
                BennuSecurityContextRepository.reconstruct(identity));
        if (response.getStatus() >= HttpServletResponse.SC_BAD_REQUEST) {
            // The handler could not produce a logout request (e.g. IdP metadata without a SLO
            // endpoint) and wrote an error status: decline, so the caller sends its normal
            // logout redirect instead of leaving the user on a bare error page.
            logger.warn("IdP Single Logout could not be initiated (HTTP {}); falling back to the local logout",
                    response.getStatus());
            return false;
        }
        logger.debug("Redirecting user to the IdP Single Logout endpoint");
        return true;
    }

    private BennuRelyingPartyRegistrationRepository registrationRepository(HttpServletRequest request) {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext(),
                SAMLClientInitializer.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (context == null) {
            return null;
        }
        try {
            return context.getBean(BennuRelyingPartyRegistrationRepository.class);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }
}
