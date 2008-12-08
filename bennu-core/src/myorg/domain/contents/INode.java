package myorg.domain.contents;

import java.util.Comparator;
import java.util.Set;

import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public interface INode {

    public static Comparator<INode> COMPARATOR_BY_ORDER = new Comparator<INode>() {

	@Override
	public int compare(final INode node1, final INode node2) {
	    return node1.getOrder().compareTo(node2.getOrder());
	}

    };

    public Integer getOrder();

    public INode getParent();

    public Set<INode> getChildren();

    public Set<INode> getOrderedChildren();

    public Object getElement();

    public String asString();

    public MultiLanguageString getLink();

}
