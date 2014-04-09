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

import javax.servlet.http.HttpSession;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.UserGroup;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class Authenticate {
    private static final Logger logger = LoggerFactory.getLogger(Authenticate.class);

    public static final String LOGGED_USER_ATTRIBUTE = "LOGGED_USER_ATTRIBUTE";

    private static final InheritableThreadLocal<User> loggedUser = new InheritableThreadLocal<>();

    private static Set<UserAuthenticationListener> userAuthenticationListeners;

    /**
     * Login user with the specified username, intended for use with an external user authentication mechanism, local password is
     * ignored. For password based authentication use: {@link #login(HttpSession, String, String)}.
     * 
     * @param session session on with to log the user
     * @param username username of the user to login
     * @return user user logged in.
     */
    public static User login(HttpSession session, String username) {
        return internalLogin(session, username, null, false);
    }

    /**
     * Login user with the specified username and password, intended for password based authentication. For external
     * authentication mechanisms use: {@link #login(HttpSession, String)}.
     * 
     * @param session session on with to log the user
     * @param username username of the user to login
     * @param password password of the user to login
     * @return user user logged in.
     */
    public static User login(HttpSession session, String username, String password) {
        return internalLogin(session, username, password, true);
    }

    private static User internalLogin(HttpSession session, String username, String password, boolean checkPassword) {
        User user = User.findByUsername(username);
        if (checkPassword && !CoreConfiguration.getConfiguration().developmentMode()) {
            if (user == null || !user.matchesPassword(password)) {
                throw AuthorizationException.authenticationFailed();
            }
        }
        if (user == null) {
            if (CoreConfiguration.casConfig().isCasEnabled() || Bennu.getInstance().getUserSet().isEmpty()) {
                user = attemptBootstrapUser(username);
            } else {
                throw AuthorizationException.authenticationFailed();
            }
        }
        if (user.isLoginExpired()) {
            throw AuthorizationException.authenticationFailed();
        }

        loggedUser.set(user);
        session.setAttribute(LOGGED_USER_ATTRIBUTE, user);
        final Locale preferredLocale = user.getPreferredLocale();
        if (preferredLocale != null) {
            I18N.setLocale(session, preferredLocale);
        }

        fireLoginListeners(session, user);
        logger.debug("Logged in user: " + user.getUsername());

        return user;
    }

    @Atomic(mode = TxMode.WRITE)
    private static User attemptBootstrapUser(String username) {
        try {
            User user = User.findByUsername(username);
            if (user != null) {
                return user;
            }
            if (CoreConfiguration.casConfig().isCasEnabled() || Bennu.getInstance().getUserSet().isEmpty()) {
                user = new User(username);
                DynamicGroup managers = DynamicGroup.get("managers");
                if (!managers.exists()) {
                    loggedUser.set(user);
                    managers.changeGroup(UserGroup.of(user));
                    logger.info("Bootstrapped #managers group to user: " + user.getUsername());
                }
                return user;
            }
            throw AuthorizationException.authenticationFailed();
        } finally {
            loggedUser.set(null);
        }
    }

    public static void logout(HttpSession session) {
        if (session != null) {
            User user = (User) session.getAttribute(LOGGED_USER_ATTRIBUTE);
            if (user != null) {
                fireLogoutListeners(session, user);
            }
            session.removeAttribute(LOGGED_USER_ATTRIBUTE);
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
        if (user != null) {
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
