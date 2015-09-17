/*
 * Authenticate.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.security;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.i18n.I18NFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;

public class Authenticate {
    private static final Logger logger = LoggerFactory.getLogger(Authenticate.class);

    private static final String LOGGED_USER_ATTRIBUTE = "LOGGED_USER_ATTRIBUTE";

    private static final InheritableThreadLocal<User> loggedUser = new InheritableThreadLocal<>();

    private static Set<UserAuthenticationListener> userAuthenticationListeners;

    /**
     * Logs in the given User, associating it with the browser session for the current user.
     * 
     * @param request
     *            The request that triggered the login
     * @param response
     *            The response associated with the request that triggered the login
     * @param user
     *            The user to log in
     * @return
     *         The logged in user
     * @throws AuthorizationException
     *             If the provided user is {@code null} or if the user has its login expired
     */
    public static User login(HttpServletRequest request, HttpServletResponse response, User user) {
        if (user == null || user.isLoginExpired()) {
            throw AuthorizationException.authenticationFailed();
        }

        HttpSession session = request.getSession();
        loggedUser.set(user);
        session.setAttribute(LOGGED_USER_ATTRIBUTE, user);
        final Locale preferredLocale = user.getProfile().getPreferredLocale();
        if (preferredLocale != null) {
            I18NFilter.updateLocale(preferredLocale, request, response);
        }

        fireLoginListeners(session, user);
        logger.debug("Logged in user: " + user.getUsername());

        return user;
    }

    /**
     * Invalidates the current session, logging out the current user.
     * 
     * Calling this method will cause subsequent calls to {{@link #getUser()} to return {@code null}.
     * 
     * @param request
     *            The request that triggered the logout
     * @param response
     *            The response associated with the requires that triggered the logout
     */
    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute(LOGGED_USER_ATTRIBUTE);
            if (user != null) {
                fireLogoutListeners(session, user);
            }
            session.invalidate();
        }
        loggedUser.set(null);
    }

    public static void mock(User user) {
        loggedUser.set(user);
    }

    public static void unmock() {
        loggedUser.set(null);
    }

    public static User getUser() {
        return loggedUser.get();
    }

    public static boolean isLogged() {
        return loggedUser.get() != null;
    }

    static void updateFromSession(HttpSession session) {
        User user = (User) (session == null ? null : session.getAttribute(LOGGED_USER_ATTRIBUTE));
        if (user != null && FenixFramework.isDomainObjectValid(user)) {
            loggedUser.set(user);
        } else {
            loggedUser.set(null);
        }
    }

    static void clear() {
        loggedUser.set(null);
    }

    public static void addUserAuthenticationListener(UserAuthenticationListener listener) {
        if (userAuthenticationListeners == null) {
            userAuthenticationListeners = new HashSet<>();
        }
        userAuthenticationListeners.add(listener);
    }

    public static void removeUserAuthenticationListener(UserAuthenticationListener listener) {
        if (userAuthenticationListeners != null) {
            userAuthenticationListeners.remove(listener);
        }
    }

    private static void fireLoginListeners(HttpSession session, final User user) {
        if (userAuthenticationListeners != null) {
            for (UserAuthenticationListener listener : userAuthenticationListeners) {
                listener.onLogin(session, user);
            }
        }
    }

    private static void fireLogoutListeners(HttpSession session, final User user) {
        if (userAuthenticationListeners != null) {
            for (UserAuthenticationListener listener : userAuthenticationListeners) {
                listener.onLogout(session, user);
            }
        }
    }
}
