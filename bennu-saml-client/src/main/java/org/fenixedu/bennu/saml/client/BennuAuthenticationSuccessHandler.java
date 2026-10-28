package org.fenixedu.bennu.saml.client;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fenixedu.bennu.core.domain.Singleton;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.servlet.PortalLoginServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

/**
 * Logs the authenticated user into Bennu and redirects to the requested callback.
 *
 * <ul>
 * <li>Extracts the username from the configured SAML attribute ({@code saml.username.attribute}),
 * optionally stripping the {@code @domain} part ({@code saml.username.stripDomain}).</li>
 * <li>Provisions the user when missing, if {@code saml.autoCreateUsers} is enabled.</li>
 * <li>Authenticates the user through Bennu's {@link Authenticate#login}.</li>
 * <li>Stores the SAML identity (NameID + session indexes) in the session for the Single Logout
 * flow.</li>
 * <li>Redirects to the callback: the one stored in the session at login (an IdP-initiated
 * target-shaped RelayState wins over it), validated by {@link PortalLoginServlet#validateCallback}.
 * </li>
 * </ul>
 */
public class BennuAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(BennuAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        Saml2Authentication samlAuthentication = (Saml2Authentication) authentication;
        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) samlAuthentication.getPrincipal();

        // resolved (and the session callback consumed) before any early return, so a failed login
        // never leaves a stale callback behind for a later IdP-initiated login
        String callback = resolveCallback(request);

        SAMLClientConfiguration.ConfigurationProperties config = SAMLClientConfiguration.getConfiguration();
        String username = extractUsername(principal, config);
        if (username == null) {
            logger.warn("SAML authentication succeeded but the username attribute {} was not present in the assertion",
                    config.usernameAttribute());
            response.sendRedirect(request.getContextPath() + config.loginFailedUrl());
            return;
        }

        try {
            loginUser(username, config, request, response);
        } catch (Exception e) {
            // e.getMessage() alone can be null (and JIT fast-throw erodes the message of
            // exceptions thrown repeatedly): log the exception class and full stack trace so
            // production failures are diagnosable
            logger.warn("SAML authentication failed for user {}: {}", username, e.toString(), e);
            response.sendRedirect(request.getContextPath() + config.loginFailedUrl());
            return;
        }

        storeSamlIdentity(request, principal, samlAuthentication.getName());

        String target = redirectTarget(request, callback);
        logger.debug("Logged in user {} via SAML, redirecting to {}", username, target);
        response.sendRedirect(target);
    }

    String extractUsername(Saml2AuthenticatedPrincipal principal,
            SAMLClientConfiguration.ConfigurationProperties config) {
        String attributeName = config.usernameAttribute();
        String username;
        if ("NameID".equals(attributeName)) {
            username = principal.getName();
        } else {
            List<Object> values = principal.getAttribute(attributeName);
            username = values == null || values.isEmpty() ? null : String.valueOf(values.get(0));
        }
        if (username != null && Boolean.TRUE.equals(config.usernameStripDomain())) {
            int at = username.indexOf('@');
            if (at >= 0) {
                username = username.substring(0, at);
            }
        }
        return username == null || username.trim().isEmpty() ? null : username.trim();
    }

    /**
     * Provision and log the user in, inside an explicit Fenix transaction. The SAML endpoints are
     * served by this module's filter chain, which deployments may order outside bennu-core's
     * {@code CloseTransactionFilter} — domain access without an open transaction throws.
     */
    @Atomic(mode = TxMode.WRITE)
    private void loginUser(String username, SAMLClientConfiguration.ConfigurationProperties config,
            HttpServletRequest request, HttpServletResponse response) {
        User user = findOrCreateUser(username, config);
        Authenticate.login(request, response, user, "SAML Authentication");
    }

    private User findOrCreateUser(String username, SAMLClientConfiguration.ConfigurationProperties config) {
        if (Boolean.TRUE.equals(config.autoCreateUsers())) {
            return Singleton.getInstance(() -> User.findByUsername(username), () -> {
                logger.info("Created new user for {}", username);
                return new User(username, new UserProfile("Unknown", "User", null, null, null));
            });
        }
        return User.findByUsername(username);
    }

    private void storeSamlIdentity(HttpServletRequest request, Saml2AuthenticatedPrincipal principal, String nameId) {
        HttpSession session = request.getSession(true);
        session.setAttribute(BennuSecurityContextRepository.SAML_IDENTITY_SESSION_ATTRIBUTE,
                new BennuSecurityContextRepository.StoredSamlIdentity(nameId, principal.getSessionIndexes()));
    }

    String resolveCallback(HttpServletRequest request) {
        // A target-shaped RelayState (starts with / or contains ://) can only come from the IdP
        // itself — SP-initiated logins always carry Spring's random UUID — so it wins over the
        // session callback, which is always consumed. Remaining case: an IdP-initiated login
        // without a shaped RelayState after an abandoned SP-initiated login still uses the stale
        // session callback; it is validated by redirectTarget, and the authentication-request
        // customizer clears it at the start of every new SP-initiated login.
        String callback = null;
        String relayState = request.getParameter("RelayState");
        if (relayState != null && (relayState.startsWith("/") || relayState.contains("://"))) {
            callback = relayState;
        }
        HttpSession session = request.getSession(false);
        String sessionCallback = null;
        if (session != null) {
            sessionCallback = (String) session.getAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE);
            session.removeAttribute(BennuSecurityContextRepository.CALLBACK_SESSION_ATTRIBUTE);
        }
        return callback != null ? callback : sessionCallback;
    }

    String redirectTarget(HttpServletRequest request, String callback) {
        if (callback != null && !callback.trim().isEmpty()) {
            callback = callback.trim();
            if (isSafeRelativeCallback(callback)) {
                return request.getContextPath() + callback;
            }
            if (PortalLoginServlet.validateCallback(callback)) {
                return callback;
            }
            logger.warn("Discarding invalid SAML login callback: {}", callback);
        }
        return CoreConfiguration.getConfiguration().applicationUrl() + request.getContextPath();
    }

    /**
     * Whether a callback can be safely treated as a same-origin relative URL. The callback must
     * start with a single {@code /} (never the protocol-relative {@code "//"} or {@code "/\"}),
     * and must not contain control characters: browsers strip ASCII tab/newline anywhere in a
     * URL before parsing, which could turn an accepted-looking path ({@code "/\t//host"}) into
     * a protocol-relative {@code "//host"}, and treat {@code "\"} as {@code "/"} in special-scheme
     * URLs. A same-site path has no business containing control characters, so they are all
     * rejected.
     */
    private static boolean isSafeRelativeCallback(String callback) {
        if (!callback.startsWith("/") || (callback.length() > 1
                && (callback.charAt(1) == '/' || callback.charAt(1) == '\\'))) {
            return false;
        }
        for (int i = 0; i < callback.length(); i++) {
            char c = callback.charAt(i);
            if (c < 0x20 || c == 0x7F) {
                return false;
            }
        }
        return true;
    }
}
