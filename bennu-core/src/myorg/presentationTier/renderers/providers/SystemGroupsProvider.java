package myorg.presentationTier.renderers.providers;

import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class SystemGroupsProvider implements DataProvider {

    @Override
    public Converter getConverter() {
	return null;
    }

    @Override
    public Object provide(Object arg0, Object arg1) {
	return MyOrg.getInstance().getSystemGroups();
    }

}
