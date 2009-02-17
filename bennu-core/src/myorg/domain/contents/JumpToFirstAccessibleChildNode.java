package myorg.domain.contents;

import myorg.domain.VirtualHost;
import myorg.presentationTier.Context;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class JumpToFirstAccessibleChildNode extends JumpToFirstAccessibleChildNode_Base {

    public JumpToFirstAccessibleChildNode(final VirtualHost virtualHost, final Node node) {
        super();
        init(virtualHost, node, null);
    }

    public Node getFirstAccessibleChildNode() {
	for (final INode node : getOrderedChildren()) {
	    final Node n = (Node) node;
	    if (n.isAccessible()) {
		return n;
	    }
	}
	return null;
    }

    @Override
    public Object getElement() {
	return null;
    }

    @Override
    public MultiLanguageString getLink() {
	final Node node = getFirstAccessibleChildNode();
	return node == null ? null : node.getLink();
    }

    @Override
    protected void appendUrlPrefix(final StringBuilder stringBuilder) {
	final Node node = getFirstAccessibleChildNode();
	if (node != null) {
	    node.appendUrlPrefix(stringBuilder);
	}
    }

    @Override
    protected void appendNodePath(final StringBuilder stringBuilder) {
	super.appendNodePath(stringBuilder);
	final Node node = getFirstAccessibleChildNode();
	if (node != null) {
	    stringBuilder.append(Context.PATH_PART_SEPERATOR);
	    stringBuilder.append(node.getOID());
	}
    }

    @Override
    public boolean isAccessible() {
	final Node node = getFirstAccessibleChildNode();
	return node != null && node.isAccessible();
    }

    @Service
    public static JumpToFirstAccessibleChildNode createJumpToFirstAccessibleChildNode(final VirtualHost virtualHost, final Node node) {
	return new JumpToFirstAccessibleChildNode(virtualHost, node);
    }

}
