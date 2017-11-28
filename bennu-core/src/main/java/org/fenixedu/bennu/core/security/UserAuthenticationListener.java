package org.fenixedu.bennu.core.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.domain.AuthenticationContext;
import org.fenixedu.bennu.core.domain.User;

/**
 * Listener of user login/logout events. Runs synchronously with the login.
 *
 * @see Authenticate#addUserAuthenticationListener(UserAuthenticationListener)
 * @see Authenticate#removeUserAuthenticationListener(UserAuthenticationListener)
 */
public interface UserAuthenticationListener {

    default void onLogin(HttpServletRequest request, HttpServletResponse response, AuthenticationContext context) {
        onLogin(request, response, context.getUser());
    }

    default void onLogout(HttpServletRequest request, HttpServletResponse response, AuthenticationContext context) {
        onLogout(request, response, context.getUser());
    }

    default void onLogin(HttpServletRequest request, HttpServletResponse response, User user) {
        
    }

    default void onLogout(HttpServletRequest request, HttpServletResponse response, User user) {
        
    }

}
