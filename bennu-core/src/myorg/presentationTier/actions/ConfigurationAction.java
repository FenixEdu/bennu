package myorg.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

}
