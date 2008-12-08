package myorg.presentationTier;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import myorg.domain.contents.INode;
import myorg.domain.contents.Node;

public class Context {

    public static final String PATH_PART_SEPERATOR = ",";

    private Stack<INode> elements = new Stack<INode>();

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
		final INode node = Node.fromString(pathPart);
		elements.add(node);
	    }
	}	
    }

    public List<INode> getElements() {
        return elements;
    }

    public Collection<INode> getMenuElements() {
	if (elements.size() > 1) {
	    final INode parentNode = elements.get(elements.size() - 2);
	    return parentNode.getOrderedChildren();
	} else {
	    return Node.getOrderedTopLevelNodes();
	}
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

}
