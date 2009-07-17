package myorg.presentationTier.renderers;

import myorg.util.ClassNameResolver;
import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixframework.DomainObject;

public class ClassNameResolverRenderer extends OutputRenderer {

    @Override
    protected Layout getLayout(Object arg0, Class arg1) {
	return new Layout() {

	    @Override
	    public HtmlComponent createComponent(Object arg0, Class arg1) {
		return new HtmlText(ClassNameResolver.getNameFor((Class<? extends DomainObject>) arg0));
	    }

	};
    }

}
