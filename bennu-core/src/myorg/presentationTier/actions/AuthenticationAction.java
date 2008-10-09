package myorg.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import myorg.applicationTier.Authenticate;
import myorg.applicationTier.Authenticate.UserView;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.servlets.filters.SetUserViewFilter;
import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping( path="/authenticationAction" )
@Forwards( { @Forward(name="forward", path="/home.do?method=firstPage", redirect=true) } )
public class AuthenticationAction extends BaseAction {

    public static void login(final HttpServletRequest request, final String username, final String password) {
	final UserView user = Authenticate.authenticate(username, password);
	final HttpSession httpSession = request.getSession();
	httpSession.setAttribute(SetUserViewFilter.USER_SESSION_ATTRIBUTE, user);
    }

    public static void logout(final HttpServletRequest request) {
	pt.ist.fenixWebFramework.security.UserView.setUser(null);
	final HttpSession httpSession = request.getSession();
	httpSession.removeAttribute(SetUserViewFilter.USER_SESSION_ATTRIBUTE);
	httpSession.invalidate();
    }

    public final ActionForward login(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response)
    		throws Exception {
	final String username = getAttribute(request, "username");
	final String password = getAttribute(request, "password");
	login(request, username, password);	
	return mapping.findForward("forward");
    }

    public final ActionForward logout(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response)
		throws Exception {
	logout(request);
	return mapping.findForward("forward");
    }

    public final ActionForward logoutEmptyPage(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response)
	throws Exception {
	logout(request);
	response.getOutputStream().close();
	return null;
    }

}
