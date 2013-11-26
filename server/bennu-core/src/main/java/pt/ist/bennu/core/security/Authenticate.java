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
package pt.ist.bennu.core.security;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.AuthorizationException;
import pt.ist.bennu.core.domain.groups.DynamicGroup;
import pt.ist.bennu.core.domain.groups.UserGroup;
import pt.ist.bennu.core.util.CoreConfiguration;
import pt.ist.bennu.core.util.CoreConfiguration.ConfigurationProperties;
import pt.ist.bennu.core.util.TransactionalThread;
import pt.ist.dsi.commons.i18n.I18N;
import pt.ist.fenixframework.Atomic;

public class Authenticate {
    private static final Logger logger = LoggerFactory.getLogger(Authenticate.class);

    private static final String USER_SESSION_ATTRIBUTE = "USER_SESSION_ATTRIBUTE";

    private static final InheritableThreadLocal<UserSession> wrapper = new InheritableThreadLocal<>();

    private static Set<AuthenticationListener> authenticationListeners;

    public static UserSession login(HttpSession session, String username, String password, boolean checkPassword) {
        UserSession user = internalLogin(username, password, checkPassword);
        session.setAttribute(USER_SESSION_ATTRIBUTE, user);
        final Locale preferredLocale = user.getUser().getPreferredLocale();
        if (preferredLocale != null) {
            I18N.setLocale(session, preferredLocale);
        }

        fireLoginListeners(user.getUser());
        logger.debug("Logged in user: " + user.getUsername());

        return user;
    }

    @Atomic
    private static UserSession internalLogin(String username, String password, boolean checkPassword) {
        User user = User.findByUsername(username);
        if (checkPassword && CoreConfiguration.getConfiguration().checkLoginPassword()) {
            if (user == null || user.getPassword() == null || !user.matchesPassword(password)) {
                throw AuthorizationException.authenticationFailed();
            }
        }
        if (user == null) {
            if (CoreConfiguration.casConfig().isCasEnabled() || Bennu.getInstance().getUsersSet().isEmpty()) {
                user = new User(username);
            } else {
                throw AuthorizationException.authenticationFailed();
            }
        }

        UserSession userWrapper = new UserSession(user);
        setUser(userWrapper);
        if (Bennu.getInstance().getUsersSet().size() == 1) {
            DynamicGroup.initialize("managers", UserGroup.getInstance(user));
            logger.info("Bootstrapped #managers group to user: " + user.getUsername());
        }
        return userWrapper;
    }

    public static void logout(HttpSession session) {
        if (session != null) {
            final UserSession userWrapper = (UserSession) session.getAttribute(USER_SESSION_ATTRIBUTE);
            if (userWrapper != null) {
                internalLogout(userWrapper.getUser());
            }
            session.removeAttribute(USER_SESSION_ATTRIBUTE);
            session.invalidate();
        }
        setUser(null);
    }

    @Atomic
    private static void internalLogout(User user) {
        user.setLastLogoutDateTime(new DateTime());
    }

    public static void mock(User user) {
        setUser(new UserSession(user));
    }

    public static void unmock() {
        setUser(null);
    }

    public static UserSession getUserSession() {
        return wrapper.get();
    }

    public static User getUser() {
        return wrapper.get() != null ? wrapper.get().getUser() : null;
    }

    public static boolean isLogged() {
        return wrapper.get() != null;
    }

    static void setUser(UserSession user) {
        wrapper.set(user);
    }

    public static String getPrivateConstantForDigestCalculation() {
        return wrapper.get() != null ? wrapper.get().getPrivateConstantForDigestCalculation() : null;
    }

    static void updateFromSession(HttpSession session) {
        UserSession user = (UserSession) (session == null ? null : session.getAttribute(USER_SESSION_ATTRIBUTE));
        if (user != null) {
            final DateTime lastLogoutDateTime = user.getLastLogoutDateTime();
            if (lastLogoutDateTime == null || user.getUserCreationDateTime().isAfter(lastLogoutDateTime)) {
                wrapper.set(user);
                logger.trace("Set thread's user to: {}", user.getUsername());
            } else {
                wrapper.set(null);
            }
        } else {
            wrapper.set(null);
        }
    }

    public static void addAuthenticationListener(AuthenticationListener listener) {
        if (authenticationListeners == null) {
            authenticationListeners = new HashSet<>();
        }
        authenticationListeners.add(listener);
    }

    public static void removeAuthenticationListener(AuthenticationListener listener) {
        if (authenticationListeners != null) {
            authenticationListeners.remove(listener);
        }
    }

    private static void fireLoginListeners(final User user) {
        if (authenticationListeners != null) {
            for (final AuthenticationListener listener : authenticationListeners) {
                final TransactionalThread thread = new TransactionalThread() {
                    @Override
                    public void transactionalRun() {
                        listener.afterLogin(user);
                    }
                };
                thread.start();
            }
        }
    }
}
