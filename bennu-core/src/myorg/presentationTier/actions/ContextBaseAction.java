package myorg.presentationTier.actions;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.presentationTier.Context;
import myorg.presentationTier.LayoutContext;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.utl.ist.fenix.tools.util.i18n.Language;

public abstract class ContextBaseAction extends BaseAction {

    public static final String CONTEXT_PATH = "_CONTEXT_PATH_";
    public static final String CONTEXT = "_CONTEXT_";

    public static class LocaleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Language language;

	public LocaleBean() {
	    language = Language.getLanguage();
	}

	public Language getLanguage() {
	    return language;
	}

	public void setLanguage(Language language) {
	    this.language = language;
	}

    }

    public Context createContext(final String contextPathString) {
	return new LayoutContext(contextPathString);
    }

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final String contextPathString = getAttribute(request, CONTEXT_PATH);
	final Context context = createContext(contextPathString);
	request.setAttribute(CONTEXT, context);

	final LocaleBean localeBean = new LocaleBean();
	request.setAttribute("localeBean", localeBean);

	return super.execute(mapping, form, request, response);
    }

    public static Context getContext(final HttpServletRequest request) {
	return (Context) request.getAttribute(CONTEXT);
    }

    public static ActionForward forward(final HttpServletRequest request, final String forward) {
	final Context context = getContext(request);
	return context.forward(forward);
    }

}
