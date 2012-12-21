/* 
* @(#)ConfigurationAction.java 
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
*   3 of the License, or (at your option) any later version. 
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
package pt.ist.bennu.core.presentationTier.actions;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.ist.bennu.core._development.PropertiesManager;
import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.VirtualHostBean;
import pt.ist.bennu.core.domain.contents.ActionNode;
import pt.ist.bennu.core.domain.contents.INode;
import pt.ist.bennu.core.domain.contents.Node;
import pt.ist.bennu.core.domain.contents.NodeBean;
import pt.ist.bennu.core.domain.groups.People;
import pt.ist.bennu.core.domain.groups.PersistentGroup;
import pt.ist.bennu.core.domain.groups.Role;
import pt.utl.ist.fenix.tools.util.ByteArray;
import pt.ist.bennu.core.presentationTier.Context;
import pt.ist.bennu.core.presentationTier.LayoutContext;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeAction;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixframework.artifact.FenixFrameworkArtifact;
import pt.ist.fenixframework.project.exception.FenixFrameworkProjectException;
import pt.utl.ist.fenix.tools.util.i18n.Language;

@Mapping(path = "/configuration")
/**
 * 
 * @author  Pedro Santos
 * @author  Nuno Diegues
 * @author  Paulo Abrantes
 * @author  Luis Cruz
 * 
*/
public class ConfigurationAction extends ContextBaseAction {

    @CreateNodeAction(bundle = "MYORG_RESOURCES", key = "add.node.application.configuration", groupKey = "label.application")
    public final ActionForward createManagementNode(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");

	final Node parentOfNodes = getDomainObject(request, "parentOfNodesToManageId");

	ActionNode systemSettings = ActionNode.createActionNode(virtualHost, parentOfNodes, "/configuration",
		"applicationConfiguration", "resources.MyorgResources", "label.application.configuration",
		Role.getRole(RoleType.MANAGER));

	ActionNode.createActionNode(virtualHost, systemSettings, "/configuration", "manageSystemGroups",
		"resources.MyorgResources", "label.configuration.manage.system.groups", Role.getRole(RoleType.MANAGER));

	ActionNode.createActionNode(virtualHost, systemSettings, "/scheduler", "viewScheduler", "resources.MyorgResources",
		"label.configuration.tasks.scheduleing", Role.getRole(RoleType.MANAGER));

	ActionNode.createActionNode(virtualHost, systemSettings, "/configuration", "viewSystemConfig",
		"resources.MyorgResources", "label.configuration.viewProperties", Role.getRole(RoleType.MANAGER));

	return forwardToMuneConfiguration(request, virtualHost, parentOfNodes);
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception {
	final User user = UserView.getCurrentUser();
	if (user == null || !user.hasRoleType(RoleType.MANAGER)) {
	    throw new Error("unauthorized.access");
	}
	return super.execute(mapping, form, request, response);
    }

    public ActionForward applicationConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	return context.forward("/bennu-core/applicationConfiguration.jsp");
    }

    public ActionForward basicApplicationConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response, final String forward) throws Exception {
	final Context context = getContext(request);
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostId");
	if (virtualHost != null) {
	    request.setAttribute("virtualHost", virtualHost);
	    request.setAttribute("virtualHostToConfigure", new VirtualHostBean(virtualHost));
	}
	return context.forward(forward);
    }

    public ActionForward basicApplicationConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	return basicApplicationConfiguration(mapping, form, request, response, "/bennu-core/basicApplicationConfiguration.jsp");
    }

    public ActionForward themeApplicationConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	return basicApplicationConfiguration(mapping, form, request, response, "/bennu-core/themeApplicationConfiguration.jsp");
    }

    public ActionForward languageApplicationConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	return basicApplicationConfiguration(mapping, form, request, response, "/bennu-core/languageApplicationConfiguration.jsp");
    }

    public ActionForward editBasicApplicationConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHostBean bean = getRenderedObject("virtualHostToConfigure");
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostId");
	virtualHost.edit(bean);

	((LayoutContext) getContext(request)).switchLayoutAndTheme(bean.getLayout().getName(), bean.getTheme().getName());
	return applicationConfiguration(mapping, form, request, response);
    }

    public ActionForward editBasicApplicationConfigurationLogo(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHostBean bean = getRenderedObject("virtualHostToConfigureLogo");
	final byte[] logo = consumeInputStream(bean.getLogoInputStream());
	if (logo != null) {
	    final VirtualHost virtualHost = getDomainObject(request, "virtualHostId");
	    virtualHost.setLogo(new ByteArray(logo));
	}
	return applicationConfiguration(mapping, form, request, response);
    }

    public ActionForward editBasicApplicationConfigurationFavico(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHostBean bean = getRenderedObject("virtualHostToConfigureFavico");
	final byte[] favico = consumeInputStream(bean.getFaviconInputStream());
	if (favico != null) {
	    final VirtualHost virtualHost = getDomainObject(request, "virtualHostId");
	    virtualHost.setFavicon(new ByteArray(favico));
	}
	return applicationConfiguration(mapping, form, request, response);
    }

    public ActionForward editLanguageApplicationConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostId");

	final Set<Language> languages = new HashSet<Language>();
	for (final Language language : Language.values()) {
	    if (isLanguageChecked(request, language)) {
		languages.add(language);
	    }
	}

	virtualHost.setLanguages(languages);

	request.setAttribute("virtualHost", virtualHost);
	request.setAttribute("virtualHostToConfigure", new VirtualHostBean(virtualHost));

	return applicationConfiguration(mapping, form, request, response);
    }

    private boolean isLanguageChecked(final HttpServletRequest request, final Language language) {
	String attribute = request.getParameter(language.name());
	return attribute != null && !attribute.isEmpty() && "on".equalsIgnoreCase(attribute);
    }

    public ActionForward postbackBasicApplicationConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	VirtualHostBean bean = getRenderedObject("virtualHostToConfigure");
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostId");
	RenderUtils.invalidateViewState();
	request.setAttribute("virtualHost", virtualHost);
	request.setAttribute("virtualHostToConfigure", bean);
	return context.forward("/bennu-core/themeApplicationConfiguration.jsp");
    }

    public ActionForward manageSystemGroups(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Set<PersistentGroup> persistentGroups = getMyOrg().getSystemGroupsSet();
	request.setAttribute("persistentGroups", persistentGroups);
	return context.forward("/bennu-core/manageGroups.jsp");
    }

    public ActionForward viewPersistentGroup(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final PersistentGroup persistentGroup = getDomainObject(request, "persistentGroupId");
	return viewPersistentGroup(request, persistentGroup);
    }

    private ActionForward viewPersistentGroup(final HttpServletRequest request, final PersistentGroup persistentGroup) {
	final Context context = getContext(request);
	request.setAttribute("persistentGroup", persistentGroup);
	return context.forward("/bennu-core/viewPersistentGroup.jsp");
    }

    public ActionForward prepareAddVirtualHost(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	VirtualHostBean virtualHostBean = getRenderedObject();
	if (virtualHostBean == null) {
	    virtualHostBean = new VirtualHostBean();
	}
	request.setAttribute("virtualHostBean", virtualHostBean);
	return context.forward("/bennu-core/addVirtualHost.jsp");
    }

    public ActionForward addVirtualHost(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHostBean virtualHostBean = getRenderedObject();
	VirtualHost.createVirtualHost(virtualHostBean);
	return applicationConfiguration(mapping, form, request, response);
    }

    public ActionForward editNode(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	request.setAttribute("virtualHostToManage", virtualHost);

	final Node node = getDomainObject(request, "nodeId");
	request.setAttribute("node", node);

	final String editSemantic = request.getParameter("editSemantic");
	if (editSemantic != null) {
	    request.setAttribute("editSemantic", editSemantic.equals("true") ? "false" : "true");
	} else {
	    request.setAttribute("editSemantic", "true");
	}

	final Context context = getContext(request);
	return context.forward("/bennu-core/editNode.jsp");
    }

    public final ActionForward prepareCreateNode(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	request.setAttribute("virtualHostToManage", virtualHost);

	final Node node = getDomainObject(request, "parentOfNodesToManageId");
	request.setAttribute("parentOfNodesToManage", node);

	request.setAttribute("nodeBean", new NodeBean());

	final Context context = getContext(request);
	return context.forward("/bennu-core/createNode.jsp");
    }

    public final ActionForward chooseTypePostBack(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	request.setAttribute("virtualHostToManage", virtualHost);

	final Node node = getDomainObject(request, "parentOfNodesToManageId");
	request.setAttribute("parentOfNodesToManage", node);

	NodeBean nodeBean = getRenderedObject();
	request.setAttribute("nodeTypeToCreate", nodeBean.getNodeType());
	request.setAttribute("nodeBean", nodeBean);

	final Context context = getContext(request);
	return context.forward("/bennu-core/createNode.jsp");
    }

    public final ActionForward createNode(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	request.setAttribute("virtualHostToManage", virtualHost);

	final Node parentNode = getDomainObject(request, "parentOfNodesToManageId");
	request.setAttribute("parentOfNodesToManage", parentNode);

	NodeBean nodeBean = getRenderedObject();
	nodeBean.getNodeType().instantiateNode(virtualHost, parentNode, nodeBean);

	return manageMenus(mapping, form, request, response);
    }

    public ActionForward manageMenus(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	request.setAttribute("virtualHostToManage", virtualHost);

	final Node node = getDomainObject(request, "parentOfNodesToManageId");
	request.setAttribute("parentOfNodesToManage", node);

	final Set<INode> nodes = node == null ? (Set) virtualHost.getOrderedTopLevelNodes() : node.getOrderedChildren();
	request.setAttribute("nodesToManage", nodes);

	final Context context = getContext(request);
	return context.forward("/bennu-core/manageMenus.jsp");
    }

    public final ActionForward deleteNode(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Node node = getDomainObject(request, "nodeToDeleteId");
	node.deleteService();
	return manageMenus(mapping, form, request, response);
    }

    public final ActionForward editNodeAvailability(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Node node = getDomainObject(request, "nodeId");
	request.setAttribute("node", node);
	final Context context = getContext(request);
	return context.forward("/bennu-core/editAvailability.jsp");
    }

    public ActionForward deleteVirtualHost(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	virtualHost.deleteService();
	return applicationConfiguration(mapping, form, request, response);
    }

    public ActionForward viewSystemConfig(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws IOException, FenixFrameworkProjectException {
    	String propertiesLocation = "/" + PropertiesManager.getProperty("app.name") + "/project.properties";
    	
    	ArrayList<String> modulesList = new ArrayList<String>();
    	for (FenixFrameworkArtifact artifact : FenixFrameworkArtifact.fromName(PropertiesManager.getProperty("app.name")) 
	    .getArtifacts()) {
    		modulesList.add(artifact.getName());
    	}
//    	Properties projectProperties = new Properties();
//    	projectProperties.load(getClass().getResourceAsStream(propertiesLocation));
    	
//    	final String[] modules = projectProperties.getProperty("depends").split(",");
    	final String[] modules = (String[]) modulesList.toArray(new String[0]);
    	
    	File[] listModulesFiles = new File(request.getServletContext().getRealPath("/WEB-INF/lib")).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("jar") && StringUtils.indexOfAny(name, modules) >= 0;
			}
		});
    	
    	List<File> modulesFileList = new ArrayList<File>(Arrays.asList(listModulesFiles));
    			
    	File[] listFiles = new File(request.getServletContext().getRealPath("/WEB-INF/lib")).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("jar");
			}
		});
    	
    	List<File> listFilesList = new ArrayList<File>(Arrays.asList(listFiles));
    	listFilesList.removeAll(modulesFileList);
    	
    	Collections.sort(modulesFileList);
    	Collections.sort(listFilesList);
    	
    	setAttribute(request, "AllOtherJars", listFilesList);
    	setAttribute(request, "AllModuleJars", modulesFileList);
	return getContext(request).forward("/bennu-core/systemInfo.jsp");
    }

    public ActionForward removeUser(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final People people = getDomainObject(request, "persistentGroupId");
	final User user = getDomainObject(request, "userId");
	people.removeMember(user);
	return viewPersistentGroup(request, people);
    }

}
