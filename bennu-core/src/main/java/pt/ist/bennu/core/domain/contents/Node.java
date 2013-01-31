/* 
* @(#)Node.java 
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
package pt.ist.bennu.core.domain.contents;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.groups.PersistentGroup;
import pt.ist.bennu.core.domain.groups.Role;
import pt.ist.bennu.core.presentationTier.Context;
import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

/**
 * 
 * @author Pedro Santos
 * @author Nuno Diegues
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public abstract class Node extends Node_Base implements INode {

	public Node() {
		super();
		setMyOrg(MyOrg.getInstance());
		//setOjbConcreteClass(getClass().getName());
	}

	@Override
	public String getUrl() {
		final StringBuilder stringBuilder = new StringBuilder();
		appendUrlPrefix(stringBuilder);
		if (!hasFunctionality()) {
			stringBuilder.append('&');
			stringBuilder.append(ContextBaseAction.CONTEXT_PATH);
			stringBuilder.append('=');
			appendNodePath(stringBuilder);
		}
		return stringBuilder.toString();
	}

	public String getUrl(final String appContext) {
		final StringBuilder stringBuilder = new StringBuilder();
		appendUrlPrefix(stringBuilder);
		if (!hasFunctionality()) {
			stringBuilder.append('&');
			stringBuilder.append(ContextBaseAction.CONTEXT_PATH);
			stringBuilder.append('=');
			appendNodePath(stringBuilder);
			if (isRedirect()) {
				stringBuilder.append('&');
				stringBuilder.append(GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME);
				stringBuilder.append('=');
				stringBuilder.append(appendChecksum(stringBuilder.toString(), appContext));
			}
		}
		return stringBuilder.toString();
	}

	protected String appendChecksum(final String url, final String appContext) {
		return GenericChecksumRewriter.calculateChecksum(appContext + url);
	}

	protected void appendNodePath(final StringBuilder stringBuilder) {
		final Node parentNode = getParentNode();
		if (parentNode != null) {
			parentNode.appendNodePath(stringBuilder);
			stringBuilder.append(Context.PATH_PART_SEPERATOR);
		}
		stringBuilder.append(getExternalId());
	}

	public String getContextPath() {
		final StringBuilder stringBuilder = new StringBuilder();
		appendNodePath(stringBuilder);
		return stringBuilder.toString();
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

	public static Node getFirstAvailableTopLevelNode() {
		final Set<Node> nodes = VirtualHost.getVirtualHostForThread().getOrderedTopLevelNodes();
		for (final Node node : nodes) {
			if (node.isAccessible()) {
				return node;
			}
		}
		return null;
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

	@Override
	public Set<INode> getOrderedChildren() {
		final Set<INode> nodes = new TreeSet<INode>(COMPARATOR_BY_ORDER);
		nodes.addAll(getChildren());
		return nodes;
	}

	@Override
	public String asString() {
		return getExternalId();
	}

	public static INode fromString(final String string) {
		Node node = AbstractDomainObject.fromExternalId(string);
		return node;
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
		removeAccessibilityGroup();
		deleteDomainObject();
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

	@Override
	public boolean isAccessible() {
		final PersistentGroup persistentGroup = getAccessibilityGroup();
		final User user = UserView.getCurrentUser();
		return persistentGroup != null && persistentGroup.isMember(user);
	}

	@Override
	public boolean isManager() {
		final PersistentGroup persistentGroup = getAccessibilityGroup();
		if (persistentGroup instanceof Role) {
			Role role = (Role) persistentGroup;
			return role.isRole(RoleType.MANAGER);
		}
		return false;
	}

	public boolean isRedirect() {
		return false;
	}

	public abstract boolean isAcceptsFunctionality();

	@Override
	public boolean hasFunctionality() {
		return false;
	}

	public Node findMatchNode(String mapping, String method) {
		if (matchesSearch(mapping, method)) {
			return this;
		} else {
			for (Node child : getChildNodes()) {
				Node result = child.findMatchNode(mapping, method);
				if (result != null) {
					return result;
				}
			}
			return null;
		}
	}

	private boolean matchesSearch(String mapping, String method) {
		if (this instanceof ActionNode) {
			ActionNode actionNode = (ActionNode) this;
			return actionNode.getPath().equals(mapping) && actionNode.getMethod().equals(method);
		} else {
			return false;
		}
	}
}
