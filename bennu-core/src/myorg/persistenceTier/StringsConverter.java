/*
 * @(#)StringsConverter.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.persistenceTier;

import java.util.ArrayList;
import java.util.List;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

import pt.utl.ist.fenix.tools.util.Strings;

public class StringsConverter implements FieldConversion {

    public Object javaToSql(Object arg0) throws ConversionException {
	if (arg0 == null) {
	    return null;
	}
	Strings strings = (Strings) arg0;
	StringBuilder buffer = new StringBuilder("");
	for (String string : strings) {
	    buffer.append(string.length());
	    buffer.append(":");
	    buffer.append(string);
	}
	return buffer.toString();
    }

    public Object sqlToJava(Object arg0) throws ConversionException {
	if (arg0 == null) {
	    return null;
	}
	List<String> strings = new ArrayList<String>();
	String sqlString = (String) arg0;

	int beginIndex = 0;
	int endIndex = find(beginIndex, ':', sqlString);

	while (beginIndex >= 0 && endIndex > beginIndex) {
	    int size = Integer.valueOf(sqlString.substring(beginIndex, endIndex++));
	    strings.add(sqlString.substring(endIndex, endIndex + size));
	    beginIndex = endIndex + size;
	    endIndex = beginIndex + find(beginIndex, ':', sqlString);
	}
	return new Strings(strings);
    }

    private int find(int index, char c, String string) {
	return string.substring(index).indexOf(c);
    }

}
