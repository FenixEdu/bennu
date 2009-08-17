/**
 * 
 */
package module.fileSupport.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.contents.domain.Page;
import module.contents.domain.Page.PageBean;
import myorg.domain.RoleType;
import myorg.domain.VirtualHost;
import myorg.domain.contents.ActionNode;
import myorg.domain.contents.Node;
import myorg.domain.groups.PersistentGroup;
import myorg.domain.groups.Role;
import myorg.presentationTier.actions.ContextBaseAction;
import myorg.util.BundleUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeAction;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

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

	final Node homeNode = createNodeForPage(virtualHost, node, "resources.FileSupportResources", "link.topBar.FileSupport",
		Role.getRole(RoleType.MANAGER));
	ActionNode.createActionNode(virtualHost, homeNode, "/fileStorageManagement", "prepare", "resources.FileSupportResources",
		"link.sideBar.fileSupport.fileStorageManagement", Role.getRole(RoleType.MANAGER));
	ActionNode.createActionNode(virtualHost, homeNode, "/storageConfigurationManagement", "prepare",
		"resources.FileSupportResources", "link.sideBar.fileSupport.storageConfigurationManagement", Role
			.getRole(RoleType.MANAGER));

	return forwardToMuneConfiguration(request, virtualHost, node);
    }

    protected Node createNodeForPage(final VirtualHost virtualHost, final Node node, final String bundle, final String key,
	    PersistentGroup userGroup) {
	final PageBean pageBean = new PageBean(virtualHost, node, userGroup);
	final MultiLanguageString statisticsLabel = BundleUtil.getMultilanguageString(bundle, key);
	pageBean.setLink(statisticsLabel);
	pageBean.setTitle(statisticsLabel);
	return (Node) Page.createNewPage(pageBean);
    }

}
