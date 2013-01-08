/*
 * @(#)Authenticate.java
 * 
 * Copyright 2009 Instituto Superior Tecnico Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 * https://fenix-ashes.ist.utl.pt/
 * 
 * This file is part of the Bennu Web Application Infrastructure.
 * 
 * The Bennu Web Application Infrastructure is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Bennu is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Bennu. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.security;

import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.service.Service;

/**
 * 
 * @author Paulo Abrantes
 * @author Luis Cruz
 * @author Pedro Santos
 */
public class Authenticate {
	private static final Logger logger = LoggerFactory.getLogger(Authenticate.class);

	public static User authenticate(HttpSession session, String username, String password) {
		User user = internalAuthenticate(username, password);
		session.setAttribute(SetUserViewFilter.USER_SESSION_ATTRIBUTE, new SessionUserWrapper(user));

		for (AuthenticationListener listener : AuthenticationListener.LOGIN_LISTNERS) {
			listener.afterLogin(user);
		}

		logger.info("Logged in user: " + user.getUsername());
		return user;
	}

	@Service
	private static final User internalAuthenticate(String username, String password) {
		User user = User.findByUsername(username);
		final String check = ConfigurationManager.getProperty("check.login.password");
		if (check != null && Boolean.parseBoolean(check)) {
			if (user == null || user.getPassword() == null || !user.matchesPassword(password)) {
				throw new DomainException("BennuResources", "error.authentication.failed");
			}
		}
		if (user == null) {
			user = new User(username);
		}

		return user;
	}

	public static void logout(HttpSession session) {
		final SessionUserWrapper wrapper = (SessionUserWrapper) session.getAttribute(SetUserViewFilter.USER_SESSION_ATTRIBUTE);
		if (wrapper != null) {
			wrapper.getUser().setLastLogoutDateTime(new DateTime());
		}
		UserView.setSessionUserWrapper(null);
		session.invalidate();
	}
}
