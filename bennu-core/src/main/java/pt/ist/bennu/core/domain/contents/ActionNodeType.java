/* 
* @(#)ActionNodeType.java 
* 
* Copyright 2011 Instituto Superior Tecnico 
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
package pt.ist.bennu.core.domain.contents;

import java.io.Serializable;

import pt.ist.bennu.core.domain.VirtualHost;

/**
 * 
 * @author Nuno Diegues
 * 
 */
public class ActionNodeType extends NodeType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public String getName() {
        return "ActionNode";
    }

    @Override
    public Node instantiateNode(VirtualHost virtualHost, Node parentNode, NodeBean nodeBean) {
        return ActionNode.createActionNode(virtualHost, parentNode, nodeBean.getPath(), nodeBean.getMethod(),
                nodeBean.getLinkBundle(), nodeBean.getLinkKey(), nodeBean.getPersistentGroup());
    }
}
