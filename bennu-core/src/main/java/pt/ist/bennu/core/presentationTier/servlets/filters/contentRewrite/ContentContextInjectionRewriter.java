/* 
* @(#)ContentContextInjectionRewriter.java 
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
package pt.ist.bennu.core.presentationTier.servlets.filters.contentRewrite;

import javax.servlet.http.HttpServletRequest;

import pt.ist.bennu.core.presentationTier.Context;
import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter;

/**
 * 
 * @author Nuno Diegues
 * @author Anil Kassamali
 * @author Luis Cruz
 * 
 */
public class ContentContextInjectionRewriter extends RequestRewriter {

    public static final String CONTEXT_ATTRIBUTE_NAME = ContextBaseAction.CONTEXT_PATH;

    public ContentContextInjectionRewriter(final HttpServletRequest httpServletRequest) {
        super(httpServletRequest);
    }

    @Override
    protected String getContextPath(final HttpServletRequest httpServletRequest) {
        final Context context = ContextBaseAction.getContext(httpServletRequest);
        return context == null ? null : context.getPath();
    }

    @Override
    protected String getContextAttributeName() {
        return CONTEXT_ATTRIBUTE_NAME;
    }
}
