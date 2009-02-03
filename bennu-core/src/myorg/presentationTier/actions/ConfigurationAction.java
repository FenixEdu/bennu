/*
 * @(#)ConfigurationAction.java
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

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.VirtualHost;
import myorg.domain.VirtualHostBean;
import myorg.domain.contents.INode;
import myorg.domain.contents.Node;
import myorg.domain.groups.PersistentGroup;
import myorg.presentationTier.Context;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping( path="/configuration" )
public class ConfigurationAction extends ContextBaseAction {

    public ActionForward applicationConfiguration(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	return context.forward("/myorg/applicationConfiguration.jsp");
    }

    public ActionForward basicApplicationConfiguration(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostId");
	if (virtualHost != null) {
	    request.setAttribute("virtualHostToConfigure", virtualHost);
	}
	return context.forward("/myorg/basicApplicationConfiguration.jsp");
    }

    public ActionForward manageSystemGroups(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Set<PersistentGroup> persistentGroups = getMyOrg().getSystemGroupsSet();
	request.setAttribute("persistentGroups", persistentGroups);
	return context.forward("/myorg/manageGroups.jsp");
    }

    public ActionForward viewPersistentGroup(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final PersistentGroup persistentGroup = getDomainObject(request, "persistentGroupId");
	request.setAttribute("persistentGroup", persistentGroup);
	return context.forward("/myorg/viewPersistentGroup.jsp");
    }

    public ActionForward prepareAddVirtualHost(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	VirtualHostBean virtualHostBean = getRenderedObject();
	if (virtualHostBean == null) {
	    virtualHostBean = new VirtualHostBean();
	}
	request.setAttribute("virtualHostBean", virtualHostBean);
	return context.forward("/myorg/addVirtualHost.jsp");
    }

    public ActionForward addVirtualHost(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHostBean virtualHostBean = getRenderedObject();
	VirtualHost.createVirtualHost(virtualHostBean);
	return applicationConfiguration(mapping, form, request, response);
    }

    public ActionForward manageMenus(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	request.setAttribute("virtualHostToManage", virtualHost);

	final Node node = getDomainObject(request, "parentOfNodesToManageId");
	request.setAttribute("parentOfNodesToManage", node);

	final Set<INode> nodes = node == null ? (Set) Node.getOrderedTopLevelNodes(virtualHost) : node.getOrderedChildren();
	request.setAttribute("nodesToManage", nodes);

	final Context context = getContext(request);
	return context.forward("/myorg/manageMenus.jsp");
    }

    public final ActionForward deleteNode(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Node node = getDomainObject(request, "nodeToDeleteId");
	node.deleteService();
	return manageMenus(mapping, form, request, response);
    }

}
