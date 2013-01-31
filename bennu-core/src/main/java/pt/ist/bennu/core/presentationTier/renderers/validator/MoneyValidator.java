/* 
* @(#)MoneyValidator.java 
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
package pt.ist.bennu.core.presentationTier.renderers.validator;

import pt.ist.bennu.core.domain.util.Money;
import pt.ist.bennu.core.presentationTier.renderers.MoneyInputRenderer;
import pt.ist.fenixWebFramework.renderers.validators.HtmlChainValidator;
import pt.ist.fenixWebFramework.renderers.validators.HtmlValidator;

/**
 * 
 * @author Susana Fernandes
 * 
 */
public class MoneyValidator extends HtmlValidator {

	public MoneyValidator(HtmlChainValidator htmlChainValidator) {
		super(htmlChainValidator);

		setBundle("MYORG_RESOURCES");
		setMessage("messages.exception.validation.invalidMoney");
	}

	public MoneyValidator() {
		super();
		setBundle("MYORG_RESOURCES");
		setMessage("messages.exception.validation.invalidMoney");
	}

	@Override
	public void performValidation() {
		setValid(true);
		try {
			new MoneyInputRenderer.MoneyInputConverter().convert(Money.class, getComponent().getValue());
		} catch (Exception e) {
			setValid(false);
		}
	}

	@Override
	public boolean hasJavascriptSupport() {
		return true;
	}

	@Override
	protected String getSpecificValidatorScript() {
		return "function(element) { var text = $(element).attr('value');"
				+ "return text.length == 0 || text.search(/^[0-9]+(\\.[0-9]+)*(,[0-9]+)?$/) == 0; }";
	}
}
