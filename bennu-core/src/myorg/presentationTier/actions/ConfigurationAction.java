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

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.RoleType;
import myorg.domain.User;
import myorg.domain.VirtualHost;
import myorg.domain.VirtualHostBean;
import myorg.domain.contents.INode;
import myorg.domain.contents.Node;
import myorg.domain.contents.NodeBean;
import myorg.domain.groups.People;
import myorg.domain.groups.PersistentGroup;
import myorg.domain.util.ByteArray;
import myorg.presentationTier.Context;
import myorg.presentationTier.LayoutContext;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.utl.ist.fenix.tools.util.i18n.Language;

@Mapping(path = "/configuration")
public class ConfigurationAction extends ContextBaseAction {

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
	return context.forward("/myorg/applicationConfiguration.jsp");
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
	return basicApplicationConfiguration(mapping, form, request, response, "/myorg/basicApplicationConfiguration.jsp");
    }

    public ActionForward themeApplicationConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	return basicApplicationConfiguration(mapping, form, request, response, "/myorg/themeApplicationConfiguration.jsp");
    }

    public ActionForward languageApplicationConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	return basicApplicationConfiguration(mapping, form, request, response, "/myorg/languageApplicationConfiguration.jsp");
    }

    public ActionForward editBasicApplicationConfiguration(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHostBean bean = getRenderedObject("virtualHostToConfigure");
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostId");
	virtualHost.edit(bean);

	((LayoutContext) getContext(request)).setLayout(bean.getTheme());
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
	return context.forward("/myorg/themeApplicationConfiguration.jsp");
    }

    public ActionForward manageSystemGroups(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Set<PersistentGroup> persistentGroups = getMyOrg().getSystemGroupsSet();
	request.setAttribute("persistentGroups", persistentGroups);
	return context.forward("/myorg/manageGroups.jsp");
    }

    public ActionForward viewPersistentGroup(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final PersistentGroup persistentGroup = getDomainObject(request, "persistentGroupId");
	return viewPersistentGroup(request, persistentGroup);
    }

    private ActionForward viewPersistentGroup(final HttpServletRequest request, final PersistentGroup persistentGroup) {
	final Context context = getContext(request);
	request.setAttribute("persistentGroup", persistentGroup);
	return context.forward("/myorg/viewPersistentGroup.jsp");
    }

    public ActionForward prepareAddVirtualHost(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
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
	return context.forward("/myorg/editNode.jsp");
    }

    public final ActionForward prepareCreateNode(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	request.setAttribute("virtualHostToManage", virtualHost);

	final Node node = getDomainObject(request, "parentOfNodesToManageId");
	request.setAttribute("parentOfNodesToManage", node);

	request.setAttribute("nodeBean", new NodeBean());

	final Context context = getContext(request);
	return context.forward("/myorg/createNode.jsp");
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
	return context.forward("/myorg/createNode.jsp");
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
	return context.forward("/myorg/manageMenus.jsp");
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
	return context.forward("/myorg/editAvailability.jsp");
    }

    public ActionForward deleteVirtualHost(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	virtualHost.deleteService();
	return applicationConfiguration(mapping, form, request, response);
    }

    public ActionForward viewSystemConfig(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {
	return getContext(request).forward("/myorg/systemInfo.jsp");
    }

    public ActionForward removeUser(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final People people = getDomainObject(request, "persistentGroupId");
	final User user = getDomainObject(request, "userId");
	people.removeMember(user);
	return viewPersistentGroup(request, people);
    }

}
