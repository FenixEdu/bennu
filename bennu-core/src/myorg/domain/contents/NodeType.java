package myorg.domain.contents;

import myorg.domain.VirtualHost;

public abstract class NodeType {
    public abstract String getName();

    public abstract Node instantiateNode(VirtualHost virtualHost, Node parentNode, NodeBean nodeBean);

    @Override
    public String toString() {
	return getName();
    }
}
