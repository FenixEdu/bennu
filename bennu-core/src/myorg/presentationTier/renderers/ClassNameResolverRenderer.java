package myorg.presentationTier.renderers;

import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

public class ClassNameResolverRenderer extends OutputRenderer {

    @Override
    protected Layout getLayout(Object arg0, Class arg1) {
	return new Layout() {

	    @Override
	    public HtmlComponent createComponent(Object arg0, Class arg1) {
		return new HtmlText(BundleUtil.getLocalizedNamedFroClass((Class<?>) arg0));
	    }

	};
    }
}
