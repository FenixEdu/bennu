package myorg.presentationTier.actions;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	return context.forward("/applicationConfiguration.jsp");
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
