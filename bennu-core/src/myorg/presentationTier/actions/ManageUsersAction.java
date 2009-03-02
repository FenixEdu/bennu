/*
 * @(#)ManageUsersAction.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
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

import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.SearchUsers;
import myorg.domain.User;
import myorg.domain.VirtualHost;
import myorg.domain.contents.ActionNode;
import myorg.domain.contents.Node;
import myorg.domain.groups.UserGroup;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeAction;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/manageUsers")
public class ManageUsersAction extends ContextBaseAction {

    @CreateNodeAction( bundle="MYORG_RESOURCES", key="label.application.users.create.interface", groupKey="label.application" )
    public final ActionForward createManageCoffeeOrdersNode(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	final Node node = getDomainObject(request, "parentOfNodesToManageId");
	ActionNode.createActionNode(virtualHost, node, "/manageUsers", "searchUsers", "resources.MyorgResources", "label.application.users", UserGroup.getInstance());
	return forwardToMuneConfiguration(request, virtualHost, node);
    }

    public ActionForward searchUsers(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws ClassNotFoundException {
	SearchUsers searchUsers = getRenderedObject("searchUsers");
	if (searchUsers == null) {
	    searchUsers = new SearchUsers();
	    final User user = UserView.getCurrentUser();
	    if (user != null) {
		searchUsers.setUser(user);
		searchUsers.setUsername(user.getUsername());
	    }
	}
	request.setAttribute("searchUsers", searchUsers);
	return forward(request, "/myorg/searchUsers.jsp");
    }

    public ActionForward generatePassword(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws ClassNotFoundException {
	final User user = getDomainObject(request, "userId");
	user.generatePassword();
	request.setAttribute("password", user.getPassword());
	final SearchUsers searchUsers = new SearchUsers();
	searchUsers.setUser(user);
	searchUsers.setUsername(user.getUsername());
	request.setAttribute("searchUsers", searchUsers);
	return forward(request, "/myorg/searchUsers.jsp");
    }

    public ActionForward prepareCreateNewUser(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws ClassNotFoundException {
	final SearchUsers searchUsers = new SearchUsers();
	request.setAttribute("searchUsers", searchUsers);
	return forward(request, "/myorg/createNewUser.jsp");
    }

    public ActionForward createNewUser(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws ClassNotFoundException {
	final SearchUsers searchUsers = getRenderedObject("searchUsers");
	final User user = User.createNewUser(searchUsers.getUsername());
	searchUsers.setUser(user);
	return searchUsers(mapping, form, request, response);
    }

}
