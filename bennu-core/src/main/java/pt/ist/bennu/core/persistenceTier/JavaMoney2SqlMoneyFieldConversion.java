/* 
* @(#)JavaMoney2SqlMoneyFieldConversion.java 
* 
* Copyright 2009 Instituto Superior Tecnico 
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
package pt.ist.bennu.core.persistenceTier;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

import pt.ist.bennu.core.domain.util.Money;

/**
 * 
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public class JavaMoney2SqlMoneyFieldConversion implements FieldConversion {

	@Override
	public Object javaToSql(Object source) {
		if (source instanceof Money) {
			final Money money = (Money) source;
			return money.serialize();
		}
		return source;
	}

	@Override
	public Object sqlToJava(Object source) {
		if (source instanceof String) {
			final String string = (String) source;
			return Money.deserialize(string);
		}
		return source;
	}

}
