package myorg.domain.contents;

import java.io.Serializable;

import myorg.domain.VirtualHost;

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