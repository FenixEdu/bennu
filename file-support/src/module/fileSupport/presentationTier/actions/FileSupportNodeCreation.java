/**
 * 
 */
package module.fileSupport.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.RoleType;
import myorg.domain.VirtualHost;
import myorg.domain.contents.ActionNode;
import myorg.domain.contents.Node;
import myorg.domain.groups.Role;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeAction;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

/**
 * @author Shezad Anavarali Date: Jul 21, 2009
 * 
 */
@Mapping(path = "/fileSupportNodeCreation")
public class FileSupportNodeCreation extends ContextBaseAction {

    @CreateNodeAction(bundle = "FILE_SUPPORT_RESOURCES", key = "add.node.fileSupport.management", groupKey = "label.module.fileSupport")
    public final ActionForward createWorkflowNode(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	final Node node = getDomainObject(request, "parentOfNodesToManageId");

//	final Node homeNode = ActionNode.createActionNode(virtualHost, node, "/fileStorageManagement", "prepare", "resources.FileSupportResources",
//		"link.sideBar.fileSupport.fileStorageManagement", Role.getRole(RoleType.MANAGER));
	ActionNode.createActionNode(virtualHost, node, "/fileStorageManagement", "prepare", "resources.FileSupportResources",
		"link.sideBar.fileSupport.fileStorageManagement", Role.getRole(RoleType.MANAGER));
	ActionNode.createActionNode(virtualHost, node, "/storageConfigurationManagement", "prepare",
		"resources.FileSupportResources", "link.sideBar.fileSupport.storageConfigurationManagement", Role
			.getRole(RoleType.MANAGER));

	return forwardToMuneConfiguration(request, virtualHost, node);
    }

}
