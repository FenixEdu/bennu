/* 
* @(#)ContextBaseAction.java 
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

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.ist.bennu.core.presentationTier.Context;
import pt.ist.bennu.core.presentationTier.LayoutContext;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.utl.ist.fenix.tools.util.i18n.Language;

/**
 * 
 * @author  Paulo Abrantes
 * @author  Luis Cruz
 * 
*/
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

    public Context createContext(final String contextPaString) {
	return createContext(contextPaString, null);
    }

    public Context createContext(final String contextPathString, HttpServletRequest request) {
	return new LayoutContext(contextPathString);
    }

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final String contextPathString = getAttribute(request, CONTEXT_PATH);
	final Context context = createContext(contextPathString, request);
	request.setAttribute(CONTEXT, context);

	final LocaleBean localeBean = new LocaleBean();
	request.setAttribute("localeBean", localeBean);

	return super.execute(mapping, form, request, response);
    }

    public static Context getContext(final HttpServletRequest request) {
	return (Context) request.getAttribute(CONTEXT);
    }

    public static void setContext(final HttpServletRequest request, final Context context) {
	request.setAttribute(CONTEXT, context);
    }

    public static ActionForward forward(final HttpServletRequest request, final String forward) {
	final Context context = getContext(request);
	return context.forward(forward);
    }

}
