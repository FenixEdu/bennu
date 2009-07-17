package myorg.presentationTier.renderers.providers;

import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixframework.DomainObject;
import dml.DomainClass;
import dml.DomainModel;

public abstract class AbstractDomainClassProvider implements DataProvider {

    @Override
    public Converter getConverter() {
	return null;
    }

    @Override
    public Object provide(Object arg0, Object arg1) {
	final DomainModel domainModel = FenixWebFramework.getDomainModel();
	List<Class<? extends DomainObject>> classNames = new ArrayList<Class<? extends DomainObject>>();
	for (DomainClass domainClass : domainModel.getDomainClasses()) {
	    if (isClassInstance(domainClass)) {
		try {
		    classNames.add((Class<? extends DomainObject>) Class.forName(domainClass.getFullName()));
		} catch (ClassNotFoundException e) {
		    // ignore error
		    e.printStackTrace();
		}
	    }
	}

	return classNames;
    }

    private boolean isClass(final DomainClass domainClass) {
	return domainClass != null && domainClass.getFullName().equals(getSuperClass().getName());
    }

    private boolean isClassInstance(final DomainClass domainClass) {
	if (domainClass == null) {
	    return false;
	}
	if (isClass(domainClass)) {
	    return shouldContainSuperClass();
	}
	final DomainClass superclass = (DomainClass) domainClass.getSuperclass();
	return isClass(superclass) || isClassInstance(superclass);
    }

    protected abstract Class getSuperClass();

    protected boolean shouldContainSuperClass() {
	return false;
    }
}
