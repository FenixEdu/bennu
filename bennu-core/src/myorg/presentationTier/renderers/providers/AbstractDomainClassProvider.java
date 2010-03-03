package myorg.presentationTier.renderers.providers;

import pt.ist.fenixframework.DomainModelUtil;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public abstract class AbstractDomainClassProvider implements DataProvider {

    @Override
    public Converter getConverter() {
	return null;
    }

    @Override
    public Object provide(Object arg0, Object arg1) {
	return DomainModelUtil.getDomainClassHierarchy(getSuperClass(), shouldContainSuperClass());
    }

    protected abstract Class getSuperClass();

    protected boolean shouldContainSuperClass() {
	return false;
    }
}
