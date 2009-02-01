package myorg.presentationTier.actions;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.VirtualHost;
import myorg.domain.VirtualHostBean;
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

}
