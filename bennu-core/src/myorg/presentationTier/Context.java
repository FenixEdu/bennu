package myorg.presentationTier;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import myorg.domain.content.Node;
import pt.ist.fenixframework.pstm.Transaction;

public class Context {

    public static final String PATH_PART_SEPERATOR = ",";

    private Stack<Node> elements = new Stack<Node>();

    public Context() {
    }

    public Context(final String path) {
	this();
	setElements(path);
    }

    public void setElements(final String path) {
	if (path == null || path.isEmpty()) {
	    elements.clear();
	} else {
	    for (final String pathPart : path.split(PATH_PART_SEPERATOR)) {
		final Long oid = Long.valueOf(pathPart);
		final Node node = (Node) Transaction.getObjectForOID(oid.longValue());
		elements.add(node);
	    }
	}	
    }

    public List<Node> getElements() {
        return elements;
    }

    public Collection<Node> getMenuElements() {
	return elements.size() > 1 ? null : Node.getOrderedTopLevelNodes();
    }

    public String getPath() {
	final StringBuilder stringBuilder = new StringBuilder();
	for (final Node node : elements) {
	    if (stringBuilder.length() > 0) {
		stringBuilder.append(PATH_PART_SEPERATOR);
	    }
	    stringBuilder.append(node.getOID());
	}
	return stringBuilder.toString();
    }

    public String getPrefixPath() {
	final StringBuilder stringBuilder = new StringBuilder();
	int i = 0;
	for (final Node node : elements) {
	    if (++i < elements.size()) {
		if (stringBuilder.length() > 0) {
		    stringBuilder.append(PATH_PART_SEPERATOR);
		}
		stringBuilder.append(node.getOID());
	    }
	}
	if (stringBuilder.length() > 0) {
	    stringBuilder.append(PATH_PART_SEPERATOR);
	}
	return stringBuilder.toString();
    }

    public void push(final Node node) {
	elements.push(node);
    }

    public void pop() {
	elements.pop();
    }

    public void pop(final Node node) {
	final int nodeIndex = elements.indexOf(node);
	if (nodeIndex >= 0) {
	    for (int i = elements.size() - 1; i >= nodeIndex; i--) {
		elements.pop();
	    }
	}
    }

    public Node getSelectedNode() {
	return elements.isEmpty() ? null : elements.peek();
    }

}
