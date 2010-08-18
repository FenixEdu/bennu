package myorg.presentationTier.renderers;

import myorg.domain.util.Money;
import myorg.presentationTier.renderers.validator.MoneyValidator;
import myorg.util.BundleUtil;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.renderers.InputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;

public class MoneyInputRenderer extends InputRenderer {

    private String size;

    @Override
    protected Layout getLayout(Object object, Class type) {

	return new Layout() {

	    @Override
	    public HtmlComponent createComponent(Object object, Class type) {
		HtmlTextInput input = new HtmlTextInput();
		input.setSize(size);

		if (object != null) {
		    input.setValue(((Money) object).toFormatStringWithoutCurrency());
		}
		MetaSlotKey key = (MetaSlotKey) getInputContext().getMetaObject().getKey();
		input.setTargetSlot(key);
		input.setConverter(new MoneyInputConverter());
		HtmlChainValidator htmlChainValidator = new HtmlChainValidator(input);
		htmlChainValidator.addValidator(new MoneyValidator());

		HtmlContainer container = new HtmlInlineContainer();
		container.addChild(input);
		container.addChild(new HtmlText(BundleUtil.getFormattedStringFromResourceBundle("resources/MyorgResources",
			"label.decimal.seprator")));

		// return input;
		return container;
	    }
	};
    }

    public static class MoneyInputConverter extends Converter {

	@Override
	public Object convert(Class type, Object value) {
	    String moneyValue = (String) value;
	    if (!StringUtils.isEmpty(moneyValue)) {
		try {
		    if (moneyValue.indexOf('.') > 0 && moneyValue.indexOf(',') > 0) {
			// format 2.000,56 = 2k + 0.56
			return new Money(moneyValue.replace(".", "").replace(',', '.'));
		    }
		    if (moneyValue.indexOf('.') > 0 && moneyValue.indexOf(',') < 0) {
			// format 2.000 = 2k
			return new Money(moneyValue.replace(".", ""));
		    } else {
			// format 2,00 = 2
			return new Money(moneyValue.replace(",", "."));
		    }
		} catch (NumberFormatException e) {
		    throw new ConversionException("renderers.converter.bigdecimal", e, true, value);
		}
	    }
	    return null;
	}
    }

    public String getSize() {
	return size;
    }

    public void setSize(String size) {
	this.size = size;
    }

}
