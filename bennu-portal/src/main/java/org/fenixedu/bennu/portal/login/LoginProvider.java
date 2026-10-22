package org.fenixedu.bennu.portal.login;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.security.Authenticate;

/**
 * Login Providers allow users to select the way they prefer to authenticate into the application.
 * 
 * After the user has been successfuly identified, providers may use the
 * {@link Authenticate#login(HttpServletRequest, HttpServletResponse, org.fenixedu.bennu.core.domain.User, String)} method to authenticate
 * the user with the application.
 * 
 * Upon login, the provider is expected to redirect the user to the desired callback URL. If login is unsuccessful, the user
 * should still be redirected, only with the added parameter "login_failed" set to "true".
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 */
public interface LoginProvider {

    /**
     * Presents the user with a page on which he can authenticate with this application.
     * 
     * Typical implementations will either present a manual login page, or redirect to an external service
     * (like Twitter, Google, GitHub or a CAS instance), so that authentication is performed by a third-party service, and later
     * validated by the provider.
     * 
     * @param request
     *            The request that originated the login request
     * @param response
     *            The response associated with the login request
     * @param callback
     *            The URL for which to redirect the user upon login. May be {@code null}. This callback was already validated, so
     *            that is does not redirect the user to an external location
     * @throws IOException
     *             If an exception occurs when processing the request
     * @throws ServletException
     *             If an exception occurs when processing the request
     */
    public void showLogin(HttpServletRequest request, HttpServletResponse response, String callback) throws IOException,
            ServletException;

    /**
     * Returns the key associated with this provider. This key should be unique across all providers.
     * 
     * This key should be a simple, url-safe string, as it will be used for URL parameters.
     * 
     * @return
     *         This provider's key
     */
    public String getKey();

    /**
     * Returns the human-readable name of this provider. This will be presented to the end user when choosing a provider to login
     * with.
     * 
     * @return
     *         The name of this provider
     */
    public String getName();

    /**
     * Returns an optional path for the logo associated with this provider. This will be presented to the end user when choosing a
     * provider to login with. If empty, a generic icon will be shown.
     * 
     * The returned path must be an absolute path to the image.
     * 
     * By default, return an empty {@link Optional}.
     * 
     * @return
     *         The optional location of this provider's logo
     */
    public default Optional<String> getIconPath() {
        return Optional.empty();
    }

    /**
     * Returns whether this provider is currently enabled.
     *
     * This mechanism allows for runtime configuration of providers.
     *
     * @return Whether this provider is enabled
     */
    public default boolean isEnabled() {
        return true;
    }

    /**
     * Best-effort provider-specific logout action, invoked by the portal's /logout endpoint
     * before the caller terminates the local session. Gives providers the chance to terminate
     * their own sessions (e.g. redirect the user agent to the Identity Provider's Single Logout
     * endpoint).
     *
     * Implementations must never throw: failures are logged and ignored by the caller, which
     * always terminates the local session afterwards. If this method writes a response (e.g. a
     * redirect), it must return {@code true} so the caller does not send its own response; in
     * that case the caller still terminates the local session.
     *
     * @param request
     *            The logout request
     * @param response
     *            The response associated with the logout request
     * @return Whether this provider handled the response (the caller will then skip its own
     *         redirect; the local session is always terminated)
     */
    public default boolean logout(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        return false;
    }

}
