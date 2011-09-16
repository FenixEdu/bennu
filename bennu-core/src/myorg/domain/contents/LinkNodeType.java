package myorg.domain.contents;

import java.io.Serializable;

import myorg.domain.VirtualHost;

public class LinkNodeType extends NodeType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public String getName() {
	return "LinkNode";
    }

	@Override
    public Node instantiateNode(VirtualHost virtualHost, Node parentNode, NodeBean nodeBean) {
	return LinkNode.createLinkNode(virtualHost, parentNode, nodeBean.getUrl(), nodeBean.getLinkBundle(),
		nodeBean.getLinkKey(), nodeBean.getPersistentGroup());
    }
}