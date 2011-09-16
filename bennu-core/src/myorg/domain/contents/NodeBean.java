package myorg.domain.contents;

import java.io.Serializable;

import myorg.domain.VirtualHost;
import myorg.domain.groups.PersistentGroup;
import pt.ist.bennu.vaadin.domain.contents.VaadinNode;

public class NodeBean implements Serializable {

    public enum NodeType {
	ACTION_NODE("ActionNode"), VAADIN_NODE("VaadinNode"), LINK_NODE("LinkNode");

	private final String name;

	private NodeType(String name) {
	    this.name = name;
	}

	public String getName() {
	    return this.name;
	}
    }

    private NodeType nodeType;
    private String path;
    private String method;
    private String linkBundle;
    private String linkKey;
    private String url;
    private String argument;
    private boolean useBennuLayout = true;
    private PersistentGroup persistentGroup;

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public String getMethod() {
	return method;
    }

    public void setMethod(String method) {
	this.method = method;
    }

    public String getLinkBundle() {
	return linkBundle;
    }

    public void setLinkBundle(String linkBundle) {
	this.linkBundle = linkBundle;
    }

    public String getLinkKey() {
	return linkKey;
    }

    public void setLinkKey(String linkKey) {
	this.linkKey = linkKey;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public String getArgument() {
	return argument;
    }

    public void setArgument(String argument) {
	this.argument = argument;
    }

    public NodeType getNodeType() {
	return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
	this.nodeType = nodeType;
    }

    public boolean isUseBennuLayout() {
	return useBennuLayout;
    }

    public void setUseBennuLayout(boolean useBennuLayout) {
	this.useBennuLayout = useBennuLayout;
    }

    public PersistentGroup getPersistentGroup() {
	return persistentGroup;
    }

    public void setPersistentGroup(PersistentGroup persistentGroup) {
	this.persistentGroup = persistentGroup;
    }

    public Node instantiateSpecifiedNode(VirtualHost virtualHost, Node parentNode) {
	switch (nodeType) {
	case ACTION_NODE:
	    return ActionNode.createActionNode(virtualHost, parentNode, path, method, linkBundle, linkKey, persistentGroup);
	case VAADIN_NODE:
	    return VaadinNode.createVaadinNode(virtualHost, parentNode, linkBundle, linkKey, argument, persistentGroup,
		    useBennuLayout);
	case LINK_NODE:
	    return LinkNode.createLinkNode(virtualHost, parentNode, url, linkBundle, linkKey, persistentGroup);
	}

	return null;
    }
}
