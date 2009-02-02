/*
 * @(#)AuthenticationAction.java
 *
 * Copyright 2009 Instituto Superior Tecnico, João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import myorg.applicationTier.Authenticate;
import myorg.applicationTier.Authenticate.UserView;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.servlets.filters.SetUserViewFilter;
import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping( path="/authenticationAction" )
@Forwards( { @Forward(name="forward", path="/home.do?method=firstPage", redirect=true) } )
public class AuthenticationAction extends ContextBaseAction {

    public static void login(final HttpServletRequest request, final String username, final String password) {
	final UserView user = Authenticate.authenticate(username, password);
	final HttpSession httpSession = request.getSession();
	httpSession.setAttribute(SetUserViewFilter.USER_SESSION_ATTRIBUTE, user);
    }

    public static void logout(final HttpServletRequest request) {
	pt.ist.fenixWebFramework.security.UserView.setUser(null);
	final HttpSession httpSession = request.getSession();
	httpSession.removeAttribute(SetUserViewFilter.USER_SESSION_ATTRIBUTE);
	httpSession.invalidate();
    }

    public final ActionForward login(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response)
    		throws Exception {
	final String username = getAttribute(request, "username");
	final String password = getAttribute(request, "password");
	login(request, username, password);	
	return mapping.findForward("forward");
    }

    public final ActionForward logout(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response)
		throws Exception {
	logout(request);
	return mapping.findForward("forward");
    }

    public final ActionForward logoutEmptyPage(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response)
	throws Exception {
	logout(request);
	response.getOutputStream().close();
	return null;
    }

}
