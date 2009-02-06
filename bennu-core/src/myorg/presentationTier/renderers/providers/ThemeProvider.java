package myorg.presentationTier.renderers.providers;

import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyConverter;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class ThemeProvider implements DataProvider {

    @Override
    public Converter getConverter() {
	return new DomainObjectKeyConverter();
    }

    @Override
    public Object provide(Object arg0, Object arg1) {
	return MyOrg.getInstance().getThemes();
    }

}
