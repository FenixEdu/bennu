package myorg.presentationTier.renderers.providers;

import myorg.domain.contents.NodeBean;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class NodeTypeProvider implements DataProvider {

    @Override
    public Converter getConverter() {
	return null;
    }

    @Override
    public Object provide(Object source, Object currentValue) {
	return NodeBean.nodeTypes;
    }

}
