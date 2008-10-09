package myorg.domain.content;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import myorg.domain.MyOrg;

public class Node extends Node_Base {
    
    public static Comparator<Node> COMPARATOR_BY_ORDER = new Comparator<Node>() {

	@Override
	public int compare(final Node node1, final Node node2) {
	    return node1.getOrder().compareTo(node2.getOrder());
	}

    };

    public Node() {
        super();
        setMyOrg(MyOrg.getInstance());
    }

    public static Node getFirstTopLevelNode() {
	final Set<Node> nodes = MyOrg.getInstance().getTopLevelNodesSet();
	return nodes.isEmpty() ? null : Collections.min(nodes, COMPARATOR_BY_ORDER);
    }
    
}
