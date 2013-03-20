/*
 * @(#)FileSupportNodeCreation.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: Shezad Anavarali
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the File Support Module.
 *
 *   The File Support Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The File Support Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the File Support Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.fileSupport.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.contents.ActionNode;
import pt.ist.bennu.core.domain.contents.Node;
import pt.ist.bennu.core.domain.groups.Role;
import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;
import pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeAction;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

/**
 * 
 * @author Shezad Anavarali
 * @author Luis Cruz
 * 
 */
@Mapping(path = "/fileSupportNodeCreation")
public class FileSupportNodeCreation extends ContextBaseAction {

    @CreateNodeAction(bundle = "FILE_SUPPORT_RESOURCES", key = "add.node.fileSupport.management",
            groupKey = "label.module.fileSupport")
    public final ActionForward createWorkflowNode(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
        final Node node = getDomainObject(request, "parentOfNodesToManageId");

//	final Node homeNode = ActionNode.createActionNode(virtualHost, node, "/fileStorageManagement", "prepare", "resources.FileSupportResources",
//		"link.sideBar.fileSupport.fileStorageManagement", Role.getRole(RoleType.MANAGER));
        ActionNode.createActionNode(virtualHost, node, "/fileStorageManagement", "prepare", "resources.FileSupportResources",
                "link.sideBar.fileSupport.fileStorageManagement", Role.getRole(RoleType.MANAGER));
        ActionNode.createActionNode(virtualHost, node, "/storageConfigurationManagement", "prepare",
                "resources.FileSupportResources", "link.sideBar.fileSupport.storageConfigurationManagement",
                Role.getRole(RoleType.MANAGER));

        return forwardToMuneConfiguration(request, virtualHost, node);
    }

}
