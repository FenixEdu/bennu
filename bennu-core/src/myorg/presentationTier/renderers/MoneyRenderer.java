package myorg.presentationTier.renderers;

import myorg.domain.util.Money;
import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

public class MoneyRenderer extends OutputRenderer {

    @Override
    protected Layout getLayout(Object object, Class type) {
	return new Layout() {

	    @Override
	    public HtmlComponent createComponent(Object object, Class type) {
		Money money = (Money) object;
		HtmlText text = new HtmlText(money.toFormatString());
		HtmlInlineContainer container = new HtmlInlineContainer();
		container.addChild(text);

		return container;
	    }

	};

    }

}
