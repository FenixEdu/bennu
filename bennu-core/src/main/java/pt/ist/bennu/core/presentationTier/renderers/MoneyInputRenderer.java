/* 
* @(#)MoneyInputRenderer.java 
* 
* Copyright 2010 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package pt.ist.bennu.core.presentationTier.renderers;

import pt.ist.bennu.core.domain.util.Money;
import pt.ist.bennu.core.presentationTier.renderers.validator.MoneyValidator;
import pt.ist.bennu.core.util.BundleUtil;

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

/**
 * 
 * @author  Susana Fernandes
 * 
*/
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
