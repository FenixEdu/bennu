/* 
* @(#)LocalDate2SqlFieldConverter.java 
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

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.joda.time.LocalDate;

/**
 * 
 * @author  Luis Cruz
 * 
*/
public class LocalDate2SqlFieldConverter implements FieldConversion {

    public Object javaToSql(Object arg0) throws ConversionException {
	LocalDate localDate = (LocalDate) arg0;
	if (localDate != null) {
	    final String dateString = String.format("%d-%02d-%02d", localDate.getYear(), localDate.getMonthOfYear(), localDate
		    .getDayOfMonth());
	    return dateString.length() != 10 ? null : dateString;
	}
	return null;
    }

    public Object sqlToJava(Object arg0) throws ConversionException {
	String value = (String) arg0;
	if (!StringUtils.isEmpty(value)) {
	    int year = Integer.parseInt(value.substring(0, 4));
	    int month = Integer.parseInt(value.substring(5, 7));
	    int day = Integer.parseInt(value.substring(8, 10));
	    return year == 0 || month == 0 || day == 0 ? null : new LocalDate(year, month, day);
	}
	return null;
    }

}
