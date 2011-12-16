/*
 * @(#)AuthenticationAction.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu Web Application Infrastructure.
 *
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   Bennu is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Bennu. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.presentationTier.actions;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import myorg.applicationTier.Authenticate;
import myorg.applicationTier.Authenticate.UserView;
import myorg.applicationTier.AuthenticationListner;
import myorg.domain.VirtualHost;
import myorg.domain.exceptions.DomainException;
import myorg.domain.scheduler.TransactionalThread;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.Config.CasConfig;
import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixWebFramework.servlets.filters.I18NFilter;
import pt.ist.fenixWebFramework.servlets.filters.SetUserViewFilter;
import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.utl.ist.fenix.tools.util.i18n.Language;

@Mapping(path = "/authenticationAction")
@Forwards( {
    @Forward(name = "redirect", path = "/", redirect = true),
    @Forward(name = "forward", path = "/home.do?method=firstPage")
})
public class AuthenticationAction extends ContextBaseAction {

    public static void login(final HttpServletRequest request, final String username, final String password, final boolean checkPassword) {
	final UserView user = Authenticate.authenticate(username, password, checkPassword);
	final HttpSession httpSession = request.getSession();
	httpSession.setAttribute(SetUserViewFilter.USER_SESSION_ATTRIBUTE, user);

	for (final AuthenticationListner authenticationListner : AuthenticationListner.LOGIN_LISTNERS) {
	    callLoginListner(user, authenticationListner);
	}
    }

    public static void logout(final HttpServletRequest request) {
	final HttpSession httpSession = request.getSession();
	final UserView userView = (UserView) httpSession.getAttribute(SetUserViewFilter.USER_SESSION_ATTRIBUTE);

	if (userView != null) {
	    userView.getUser().setLastLogoutDateTime(new DateTime());
	}

	pt.ist.fenixWebFramework.security.UserView.setUser(null);
	httpSession.removeAttribute(SetUserViewFilter.USER_SESSION_ATTRIBUTE);
	httpSession.invalidate();

    }

    public final ActionForward login(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final String username = getAttribute(request, "username");
	final String password = getAttribute(request, "password");
	final Locale locale = Language.getLocale();
	try {
	    login(request, username, password, true);
	    return mapping.findForward("redirect");
	} catch (final DomainException dex) {
	    logout(request);
	    request.setAttribute("authentication.failed", username);
	    I18NFilter.setLocale(request, request.getSession(), locale);
	    return mapping.findForward("forward");
	}
    }

    public final ActionForward logout(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final String serverName = request.getServerName();
	logout(request);
	final CasConfig casConfig = FenixWebFramework.getConfig().getCasConfig(serverName);
	if (casConfig != null && casConfig.isCasEnabled()) {
	    final String url = casConfig.getCasLogoutUrl();
	    return new ActionForward(url, true);
	} else {
	    return mapping.findForward("redirect");
	}
    }

    public final ActionForward logoutEmptyPage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	logout(request);
	response.getOutputStream().close();
	return null;
    }

    private static void callLoginListner(final UserView userView, final AuthenticationListner authenticationListner) {
	final TransactionalThread thread = new TransactionalThread() {

	    final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();

	    @Override
	    public void transactionalRun() {
		try {
		    VirtualHost.setVirtualHostForThread(virtualHost);
		    authenticationListner.afterLogin(userView);
		} finally {
		    VirtualHost.releaseVirtualHostFromThread();
		}
	    }

	};
	thread.start();
    }

}
