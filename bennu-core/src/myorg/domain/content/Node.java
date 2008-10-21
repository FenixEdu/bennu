package myorg.domain.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;

public class Node extends Node_Base {
    
    public static Comparator<Node> COMPARATOR_BY_ORDER = new Comparator<Node>() {

	@Override
	public int compare(final Node node1, final Node node2) {
	    return node1.getNodeOrder().compareTo(node2.getNodeOrder());
	}

    };

    public Node() {
        super();
        setMyOrg(MyOrg.getInstance());
    }

    public Node(final Page parentPage, final Page childPage, final Integer order) {
        this();
        if (parentPage == null) {
            setNodeOrder(getMyOrg().getTopLevelNodesSet(), order);
            setMyOrgFromTopLevelNode(getMyOrg());
        } else {
            setNodeOrder(parentPage.getChildNodesSet(), order);
            setParentPage(parentPage);
        }
        setChildPage(childPage);        
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
	final Set<Node> nodes = MyOrg.getInstance().getTopLevelNodesSet();
	return nodes.isEmpty() ? null : Collections.min(nodes, COMPARATOR_BY_ORDER);
    }

    @Service
    public void delete() {
	final Page parentPage = getParentPage();
	if (parentPage == null) {
	    descNodeOrders(getMyOrg().getTopLevelNodesSet());
	} else {
	    removeParentPage();
	    descNodeOrders(parentPage.getChildNodesSet());
	}
	final Page childPage = getChildPage();
	removeChildPage();
	childPage.deleteIfDisconnected();
	removeMyOrgFromTopLevelNode();
	removeMyOrg();
	Transaction.deleteObject(this);
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

    @Service
    public static void reorderNodes(final ArrayList<Node> nodes) {
	final MyOrg myOrg = MyOrg.getInstance();
	for (final Node node : myOrg.getTopLevelNodesSet()) {
	    if (!nodes.contains(node)) {
		throw new Error("Nodes changed!");
	    }
	}

	int i = 0;
	for (final Node node : nodes) {
	    if (!myOrg.hasTopLevelNodes(node)) {
		throw new Error("Nodes changed!");
	    }
	    node.setNodeOrder(Integer.valueOf(i++));
	}
    }

    public static SortedSet<Node> getOrderedTopLevelNodes() {
	final SortedSet<Node> nodes = new TreeSet<Node>(Node.COMPARATOR_BY_ORDER);
	final MyOrg myOrg = MyOrg.getInstance();
	nodes.addAll(myOrg.getTopLevelNodesSet());
	return nodes;
    }

}
