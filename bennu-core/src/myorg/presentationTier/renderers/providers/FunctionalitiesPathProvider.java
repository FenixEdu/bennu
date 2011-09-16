package myorg.presentationTier.renderers.providers;

import myorg.presentationTier.servlets.filters.FunctionalityFilter;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class FunctionalitiesPathProvider implements DataProvider {

    @Override
    public Converter getConverter() {
	return null;
    }

    @Override
    public Object provide(Object source, Object currentValue) {
	return FunctionalityFilter.retrieveFunctionalityMappings().keySet();
    }

}
