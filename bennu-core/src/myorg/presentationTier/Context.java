/* 
* @(#)Context.java 
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
package myorg.presentationTier;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import myorg.domain.VirtualHost;
import myorg.domain.contents.INode;
import myorg.domain.contents.Node;

import org.apache.struts.action.ActionForward;

/**
 * 
 * @author  Paulo Abrantes
 * @author  Luis Cruz
 * 
*/
public abstract class Context {

    public static final String PATH_PART_SEPERATOR = ",";

    private Stack<INode> elements = new Stack<INode>();

    public Context() {
    }

    public Context(final String path) {
	this();
	setElements(path);
    }

    public boolean contains(Node node) {
	for (INode nodeInStack : elements) {
	    if (nodeInStack == node) {
		return true;
	    }
	}
	return false;
    }

    public void setElements(final String path) {
	if (path == null || path.isEmpty()) {
	    elements.clear();
	} else {
	    for (final String pathPart : path.split(PATH_PART_SEPERATOR)) {
		final INode node = Node.fromString(pathPart);
		elements.add(node);
	    }
	}
    }

    public List<INode> getElements() {
	return elements;
    }

    public Collection<INode> getMenuElements() {
	final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
	return (Collection) virtualHost.getOrderedTopLevelNodes();
    }

    public String getPath() {
	final StringBuilder stringBuilder = new StringBuilder();
	for (final INode node : elements) {
	    if (node != null) {
		if (stringBuilder.length() > 0) {
		    stringBuilder.append(PATH_PART_SEPERATOR);
		}
		stringBuilder.append(node.asString());
	    }
	}
	return stringBuilder.toString();
    }

    public String getPrefixPath() {
	final StringBuilder stringBuilder = new StringBuilder();
	int i = 0;
	for (final INode node : elements) {
	    if (++i < elements.size()) {
		if (stringBuilder.length() > 0) {
		    stringBuilder.append(PATH_PART_SEPERATOR);
		}
		stringBuilder.append(node.asString());
	    }
	}
	if (stringBuilder.length() > 0) {
	    stringBuilder.append(PATH_PART_SEPERATOR);
	}
	return stringBuilder.toString();
    }

    public void push(final INode node) {
	elements.push(node);
    }

    public void pop() {
	if (!elements.isEmpty()) {
	    elements.pop();
	}
    }

    public void pop(final INode node) {
	final int nodeIndex = elements.indexOf(node);
	if (nodeIndex >= 0) {
	    for (int i = elements.size() - 1; i >= nodeIndex; i--) {
		elements.pop();
	    }
	}
    }

    public INode getSelectedNode() {
	return elements.isEmpty() ? null : elements.peek();
    }

    public INode getParentNode() {
	final int s = elements.size();
	return s > 1 ? elements.get(s - 2) : null;
    }

    public abstract ActionForward forward(final String forward);

}
