package myorg.presentationTier.renderers.providers;

import java.util.ArrayList;
import java.util.List;

import myorg.domain.contents.NodeBean.NodeType;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class NodeTypeProvider implements DataProvider {

    @Override
    public Converter getConverter() {
	return null;
    }

    @Override
    public Object provide(Object source, Object currentValue) {
	List<NodeType> result = new ArrayList<NodeType>();
	for (NodeType type : NodeType.values()) {
	    result.add(type);
	}
	return result;
    }

}
