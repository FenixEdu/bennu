/* 
* @(#)NodeProcessorAction.java 
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.bennu.core.domain.contents.INode;
import pt.ist.bennu.core.domain.contents.Node;
import pt.ist.bennu.core.presentationTier.Context;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/node")
/**
 * 
 * @author  Luis Cruz
 * 
 */
public class NodeProcessorAction extends ContextBaseAction {

    public final ActionForward viewElement(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        final Context context = getContext(request);
        if (context.getElements().isEmpty()) {
            final INode node = Node.getFirstTopLevelNode();
            context.push(node);
        }
        return context.forward("/page.jsp");
    }

}
