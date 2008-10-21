package myorg.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.presentationTier.Context;
import myorg.presentationTier.actions.ContentAction.LocaleBean;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public abstract class ContextBaseAction extends BaseAction {

    public static final String CONTEXT_PATH = "_CONTEXT_PATH_";
    public static final String CONTEXT = "_CONTEXT_";

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final String contextPathString = getAttribute(request, CONTEXT_PATH);
	final Context context = new Context(contextPathString);
	request.setAttribute(CONTEXT, context);

	final LocaleBean localeBean = new LocaleBean();
	request.setAttribute("localeBean", localeBean);

	return super.execute(mapping, form, request, response);
    }

    public static Context getContext(final HttpServletRequest request) {
	return (Context) request.getAttribute(CONTEXT);
    }

}
