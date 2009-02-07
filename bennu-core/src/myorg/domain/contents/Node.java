/*
 * @(#)Node.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.domain.contents;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.MyOrg;
import myorg.domain.User;
import myorg.domain.VirtualHost;
import myorg.domain.groups.PersistentGroup;
import myorg.presentationTier.Context;
import myorg.presentationTier.actions.ContextBaseAction;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;

public abstract class Node extends Node_Base implements INode {

    public Node() {
	super();
	setMyOrg(MyOrg.getInstance());
	setOjbConcreteClass(getClass().getName());
    }

    public String getUrl() {
	final StringBuilder stringBuilder = new StringBuilder();
	appendUrlPrefix(stringBuilder);
	stringBuilder.append("&amp;");
	stringBuilder.append(ContextBaseAction.CONTEXT_PATH);
	stringBuilder.append('=');
	appendNodePath(stringBuilder);
	return stringBuilder.toString();
    }

    protected void appendNodePath(final StringBuilder stringBuilder) {
	final Node parentNode = getParentNode();
	if (parentNode != null) {
	    parentNode.appendNodePath(stringBuilder);
	    stringBuilder.append(Context.PATH_PART_SEPERATOR);
	}
	stringBuilder.append(getOID());
    }

    protected abstract void appendUrlPrefix(final StringBuilder stringBuilder);

    @Override
    public Set<INode> getChildren() {
	return (Set) getChildNodesSet();
    }

    @Override
    public Integer getOrder() {
	return getNodeOrder();
    }

    @Override
    public INode getParent() {
	return getParentNode();
    }

    public void init(final VirtualHost virtualHost, final Node parentNode, final Integer order) {
	if (parentNode == null) {
	    setNodeOrder(virtualHost.getTopLevelNodesSet(), order);
	    setVirtualHost(virtualHost);
	} else {
	    setNodeOrder(parentNode.getChildNodesSet(), order);
	    setParentNode(parentNode);
	}
    }

    private void setNodeOrder(final Set<Node> siblings, final Integer order) {
	if (order == null) {
	    setNodeOrder(siblings.size() + 1);
	} else {
	    final int newNodeOrder = order.intValue();
	    for (final Node node : siblings) {
		final int currentNodeOrder = node.getNodeOrder().intValue();
		if (currentNodeOrder >= newNodeOrder) {
		    node.setNodeOrder(Integer.valueOf(currentNodeOrder + 1));
		}
	    }
	}
    }

    public static Node getFirstTopLevelNode() {
	final Set<Node> nodes = VirtualHost.getVirtualHostForThread().getTopLevelNodesSet();
	return nodes.isEmpty() ? null : Collections.min(nodes, COMPARATOR_BY_ORDER);
    }

    protected void descNodeOrders(final Set<Node> nodes) {
	final int threshold = getNodeOrder().intValue();
	for (final Node node : nodes) {
	    final int currentNodeOrder = node.getNodeOrder().intValue();
	    if (currentNodeOrder >= threshold) {
		node.setNodeOrder(Integer.valueOf(currentNodeOrder - 1));
	    }
	}
    }

    public static SortedSet<Node> getOrderedTopLevelNodes() {
	final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
	return virtualHost.getOrderedTopLevelNodes();
    }

    public Set<INode> getOrderedChildren() {
	final Set<INode> nodes = new TreeSet<INode>(COMPARATOR_BY_ORDER);
	nodes.addAll(getChildren());
	return nodes;
    }

    @Override
    public String asString() {
	return Long.toString(getOID());
    }

    public static INode fromString(final String string) {
	final long oid = Long.parseLong(string);
	return (INode) Transaction.getObjectForOID(oid);
    }

    @Service
    public void deleteService() {
	delete();
    }

    public void delete() {
	final Node parentNode = getParentNode();
	if (parentNode == null) {
	    final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
	    descNodeOrders(virtualHost.getTopLevelNodesSet());
	} else {
	    removeParentNode();
	    descNodeOrders(parentNode.getChildNodesSet());
	}
	for (final Node childNode : getChildNodesSet()) {
	    childNode.delete();
	}
	removeVirtualHost();
	removeMyOrg();
	Transaction.deleteObject(this);
    }

    @Service
    public static void reorderTopLevelNodes(final List<Node> nodes) throws Error {
	final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
	for (final Node node : virtualHost.getTopLevelNodesSet()) {
	    if (!nodes.contains(node)) {
		throwError();
	    }
	}

	int i = 0;
	for (final Node node : nodes) {
	    if (!virtualHost.hasTopLevelNodes(node)) {
		throwError();
	    }
	    node.setNodeOrder(Integer.valueOf(i++));
	}
    }

    @Service
    public void reorderNodes(final List<Node> nodes) {
	for (final Node node : getChildNodesSet()) {
	    if (!nodes.contains(node)) {
		throwError();
	    }
	}

	int i = 0;
	for (final Node node : nodes) {
	    if (!hasChildNodes(node)) {
		throwError();
	    }
	    node.setNodeOrder(Integer.valueOf(i++));
	}
    }

    private static void throwError() {
	throw new Error("Nodes changed!");
    }

    public boolean isAccessible() {
	final PersistentGroup persistentGroup = getAccessibilityGroup();
	final User user = UserView.getCurrentUser();
	return persistentGroup != null && persistentGroup.isMember(user);
    }

}
