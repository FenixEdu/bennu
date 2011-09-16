package myorg.presentationTier.renderers.providers;

import java.util.Collections;
import java.util.Map;

import myorg.domain.contents.ActionNode;
import myorg.presentationTier.servlets.filters.FunctionalityFilter;
import myorg.presentationTier.servlets.filters.FunctionalityFilter.FunctionalityInfo;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class FunctionalitiesMethodProvider implements DataProvider {

    @Override
    public Converter getConverter() {
	return null;
    }

    @Override
    public Object provide(Object source, Object currentValue) {
	ActionNode node = (ActionNode) source;
	Map<String, FunctionalityInfo> result = FunctionalityFilter.retrieveFunctionalityMappings().get(node.getPath());
	return result == null ? Collections.EMPTY_SET : result.keySet();
    }

}
