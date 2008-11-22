package myorg.presentationTier.renderers.organization;

import java.util.ArrayList;

import myorg.domain.MyOrg;
import myorg.domain.organization.PartyType;
import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyConverter;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class PartyTypesProvider implements DataProvider {

    @Override
    public Converter getConverter() {
	return new DomainObjectKeyConverter();
    }

    @Override
    public Object provide(Object source, Object currentValue) {
	return new ArrayList<PartyType>(MyOrg.getInstance().getPartyTypes());
    }

}
