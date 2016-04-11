package org.fenixedu.bennu.core.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.domain.User;

/**
 * Listener of user login/logout events. Runs synchronously with the login.
 *
 * @see Authenticate#addUserAuthenticationListener(UserAuthenticationListener)
 * @see Authenticate#removeUserAuthenticationListener(UserAuthenticationListener)
 */
public interface UserAuthenticationListener {
    void onLogin(HttpServletRequest request, HttpServletResponse response, User user);

    void onLogout(HttpServletRequest request, HttpServletResponse response, User user);
}
