package org.fenixedu.bennu.core.security;

import javax.servlet.http.HttpSession;

import org.fenixedu.bennu.core.domain.User;

/**
 * Listener of user login/logout events. Runs synchronously with the login.
 * 
 * @see Authenticate#addUserAuthenticationListener(UserAuthenticationListener)
 * @see Authenticate#removeUserAuthenticationListener(UserAuthenticationListener)
 */
public interface UserAuthenticationListener {
    public void onLogin(HttpSession session, User user);

    public void onLogout(HttpSession session, User user);
}
