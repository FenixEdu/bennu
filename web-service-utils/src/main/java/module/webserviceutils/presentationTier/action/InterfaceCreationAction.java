/*
 * @(#)InterfaceCreationAction.java
 *
 * Copyright 2010 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Fenix Remote Domain Module.
 *
 *   The Fenix Remote Domain Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Remote Domain Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Remote Domain Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.webserviceutils.presentationTier.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.contents.Node;
import pt.ist.bennu.core.domain.groups.Role;
import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;
import pt.ist.bennu.vaadin.domain.contents.VaadinNode;
import pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeAction;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/webServiceUtilsInterfaceCreation")
/**
 * 
 * @author Luis Cruz
 * 
 */
public class InterfaceCreationAction extends ContextBaseAction {

    @CreateNodeAction(bundle = "WEB_SERVICE_UTILS_RESOURCES", key = "add.node.wsu.interface", groupKey = "label.module.webserviceutils")
    public final ActionForward createAnnouncmentNodes(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	final Node node = getDomainObject(request, "parentOfNodesToManageId");

	final String WEBSERVICEUTILS_RESOURCES = "resources.WebServiceUtilsResources";

	final VaadinNode hostManagementNode = VaadinNode.createVaadinNode(virtualHost, node, WEBSERVICEUTILS_RESOURCES,
		"label.link.wsu.interface", "ClientHostManagement", Role.getRole(RoleType.MANAGER));

	VaadinNode.createVaadinNode(virtualHost, hostManagementNode, WEBSERVICEUTILS_RESOURCES,
		"label.link.host.management.client", "ClientHostManagement", Role.getRole(RoleType.MANAGER));

	VaadinNode.createVaadinNode(virtualHost, hostManagementNode, WEBSERVICEUTILS_RESOURCES,
		"label.link.host.management.server", "ServerHostManagement", Role.getRole(RoleType.MANAGER));

	return forwardToMuneConfiguration(request, virtualHost, node);
    }
}
