package org.fenixedu.bennu.saml.client;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * A read-only {@link SecurityContextRepository} that bridges Bennu's session with the Spring
 * Security SAML engine.
 *
 * It never writes a Spring {@link SecurityContext} to the session, so Spring Security never
 * competes with Bennu's own authentication state (which is owned by
 * {@link org.fenixedu.bennu.core.security.Authenticate}).
 *
 * On load, it reconstructs a {@link Saml2Authentication} from the SAML identity stored in the
 * session at login time (see {@link BennuAuthenticationSuccessHandler}). This is required for the
 * SP-initiated Single Logout machinery, which needs to know the NameID and session indexes
 * originally used with the IdP.
 */
public class BennuSecurityContextRepository implements SecurityContextRepository {

    /**
     * Session attribute holding the {@link StoredSamlIdentity} of the currently SAML-authenticated
     * user.
     */
    public static final String SAML_IDENTITY_SESSION_ATTRIBUTE =
            BennuSecurityContextRepository.class.getName() + ".SAML_IDENTITY";

    /**
     * Session attribute holding the callback requested at {@code /login}: the primary channel for
     * the login callback, written by the authentication-request customizer from the
     * {@code ?callback=} parameter. An IdP-initiated target-shaped RelayState wins over it (see
     * {@link BennuAuthenticationSuccessHandler#resolveCallback}).
     */
    public static final String CALLBACK_SESSION_ATTRIBUTE =
            BennuSecurityContextRepository.class.getName() + ".CALLBACK";

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return buildContext(requestResponseHolder.getRequest());
    }

    @Override
    public Supplier<SecurityContext> loadContext(HttpServletRequest request) {
        SecurityContext context = buildContext(request);
        return () -> context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        // Intentionally not saving: Spring Security must never write authentication state into
        // Bennu's session.
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return storedIdentity(request) != null;
    }

    private SecurityContext buildContext(HttpServletRequest request) {
        StoredSamlIdentity identity = storedIdentity(request);
        if (identity == null) {
            return new SecurityContextImpl();
        }
        return new SecurityContextImpl(reconstruct(identity));
    }

    /**
     * Rebuilds the {@link Saml2Authentication} for the Single Logout machinery from the identity
     * stored in the session at login time (the NameID and session indexes originally used with
     * the IdP).
     */
    public static Saml2Authentication reconstruct(StoredSamlIdentity identity) {
        DefaultSaml2AuthenticatedPrincipal principal = new DefaultSaml2AuthenticatedPrincipal(identity.getNameId(),
                Collections.emptyMap(), identity.getSessionIndexes());
        principal.setRelyingPartyRegistrationId(SAML2SecurityConfiguration.REGISTRATION_ID);
        // Saml2Authentication requires a non-empty response; the bridge identity is reconstructed
        // from the session and carries no SAML response payload
        return new Saml2Authentication(principal, "n/a", Collections.emptyList());
    }

    private StoredSamlIdentity storedIdentity(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (StoredSamlIdentity) session.getAttribute(SAML_IDENTITY_SESSION_ATTRIBUTE);
    }

    /**
     * The minimal SAML identity data required to reconstruct the SAML authentication for the
     * Single Logout flow.
     */
    public static class StoredSamlIdentity implements Serializable {

        private static final long serialVersionUID = 1L;

        private final String nameId;
        private final List<String> sessionIndexes;

        public StoredSamlIdentity(String nameId, List<String> sessionIndexes) {
            this.nameId = nameId;
            this.sessionIndexes = sessionIndexes == null ? Collections.emptyList() : sessionIndexes;
        }

        public String getNameId() {
            return this.nameId;
        }

        public List<String> getSessionIndexes() {
            return this.sessionIndexes;
        }
    }
}
