package myorg.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.MyOrg;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping( path="/configuration" )
@Forwards( { @Forward(name="application.configuration", path="/applicationConfiguration.jsp", redirect=true) } )
public class ConfigurationAction extends BaseAction {

    public ActionForward applicationConfiguration(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	return mapping.findForward("application.configuration");
    }

}
