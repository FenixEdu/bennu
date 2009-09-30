/*
 * @(#)AddressConverter.java
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

import myorg.domain.util.Address;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

public class AddressConverter implements FieldConversion {

    final String CHARACTER = ":";
    final String SEPARATOR = CHARACTER + CHARACTER;

    public Object javaToSql(Object source) throws ConversionException {
	if (source instanceof Address) {
	    final Address address = (Address) source;
	    final StringBuilder builder = new StringBuilder();
	    builder.append(escapeFieldSeparator(address.getLine1()));
	    builder.append(SEPARATOR);
	    builder.append(escapeFieldSeparator(address.getLine2()));
	    builder.append(SEPARATOR);
	    builder.append(escapeFieldSeparator(address.getPostalCode()));
	    builder.append(SEPARATOR);
	    builder.append(escapeFieldSeparator(address.getLocation()));
	    builder.append(SEPARATOR);
	    builder.append(escapeFieldSeparator(address.getCountry()));
	    return builder.toString();
	}

	return source;
    }

    private String escapeFieldSeparator(String field) {
	return field != null ? field.replaceAll(CHARACTER, "\\\\" + CHARACTER + "\\\\") : "";
    }

    public Object sqlToJava(Object source) throws ConversionException {
	if (source instanceof String) {
	    final String stringAddress = (String) source;
	    String[] split = stringAddress.split(SEPARATOR);
	    return new Address(removeEscapeFieldSeparator(split[0]), removeEscapeFieldSeparator(split[1]),
		    removeEscapeFieldSeparator(split[2]), removeEscapeFieldSeparator(split[3]),
		    removeEscapeFieldSeparator(split[4]));
	}
	return source;
    }

    private String removeEscapeFieldSeparator(String field) {
	return field != null ? field.replaceAll("\\\\" + CHARACTER + "\\\\", CHARACTER) : null;
    }
}
