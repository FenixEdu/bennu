package myorg.presentationTier.actions;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.RoleType;
import myorg.domain.contents.ActionNode;
import myorg.domain.groups.PersistentGroup;
import myorg.domain.groups.Role;
import myorg.presentationTier.Context;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeAction;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping( path="/configuration" )
public class ConfigurationAction extends ContextBaseAction {

    @CreateNodeAction( bundle="MYORG_RESOURCES", key="add.node.application.configuration", groupKey="label.module.myorg.configuration" )
    public final ActionForward createApplicationConfigureationInterface(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	ActionNode.createActionNode("/configuration", "applicationConfiguration", "resources.MyorgResources", "label.configuration.link", Role.getRole(RoleType.MANAGER));
	return applicationConfiguration(mapping, form, request, response);
    }

    public ActionForward applicationConfiguration(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	return context.forward("/applicationConfiguration.jsp");
    }

    @CreateNodeAction( bundle="MYORG_RESOURCES", key="add.node.application.system.groups", groupKey="label.module.myorg.configuration" )
    public final ActionForward createSystemGroupsInterface(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	ActionNode.createActionNode("/configuration", "manageSystemGroups", "resources.MyorgResources", "label.configuration.manage.system.groups", Role.getRole(RoleType.MANAGER));
	return manageSystemGroups(mapping, form, request, response);
    }

    public ActionForward manageSystemGroups(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Set<PersistentGroup> persistentGroups = getMyOrg().getSystemGroupsSet();
	request.setAttribute("persistentGroups", persistentGroups);
	return context.forward("/manageGroups.jsp");
    }

    public ActionForward viewPersistentGroup(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final PersistentGroup persistentGroup = getDomainObject(request, "persistentGroupId");
	request.setAttribute("persistentGroup", persistentGroup);
	return context.forward("/viewPersistentGroup.jsp");
    }

}
