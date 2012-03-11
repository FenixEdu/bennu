/* 
* @(#)PrimitiveConversion.java 
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
package myorg.communication.transport;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * 
 * @author  Paulo Abrantes
 * 
*/
public class PrimitiveConversion {

    public static Class[] ACCEPTED_CLASSES = new Class[] { String.class, Integer.class, Boolean.class, Short.class, Float.class,
	    Long.class, Double.class };

    protected boolean isAcceptedType(Class clazz) {
	for (int i = 0; i < ACCEPTED_CLASSES.length; i++) {
	    if (ACCEPTED_CLASSES[i] == clazz) {
		return true;
	    }
	}
	return false;
    }

    private static String NULL_OBJECT = "{}";

    protected String serializeToSend(Class clazz, String value) {
	if (!isAcceptedType(clazz)) {
	    throw new UnsupportedOperationException("Does not support sending class: " + clazz.getName());
	}
	return "{" + clazz.getName() + "|@|" + value + "}";
    }

    public String serializeToSend(Object object) {
	return object == null ? NULL_OBJECT : serializeToSend(object.getClass(), object.toString());
    }

    public String serializeToSend(Object... values) {
	StringBuilder builder = new StringBuilder();
	builder.append('[');
	int totalSize = values.length;

	for (int i = 0; i < totalSize; i++) {
	    Object object = values[i];
	    builder.append(serializeToSend(object.getClass(), object.toString()));
	    if (i + 1 < totalSize) {
		builder.append(',');
	    }
	}
	builder.append(']');
	return builder.toString();
    }

    public <T extends Object> T readObject(String objectRepresentation) {
	if (!objectRepresentation.equals(NULL_OBJECT)) {
	    String fullNameClass = getType(objectRepresentation);
	    String name = fullNameClass.substring(fullNameClass.lastIndexOf('.') + 1);
	    Class[] classes = new Class[] { String.class };
	    T o = null;
	    try {
		Method m = PrimitiveConversion.class.getDeclaredMethod("read" + name, classes);
		o = (T) m.invoke(this, objectRepresentation);
		return o;
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return null;
    }

    public Object[] readMultipleObjects(String representation) {
	return readMultipleObjects(Object.class, representation);
    }

    public <T extends Object> T[] readMultipleObjects(Class<T> classType, String representation) {
	String serieOfObjects = representation.substring(1, representation.length() - 1);
	if (serieOfObjects.length() == 0) {
	    return (T[]) Array.newInstance(classType, 0);
	}
	String[] objectsRepresentations = serieOfObjects.split(",");
	T[] objects = (T[]) Array.newInstance(classType, objectsRepresentations.length);
	for (int i = 0; i < objectsRepresentations.length; i++) {
	    String objectRepresentation = objectsRepresentations[i];
	    objects[i] = (T) readObject(objectRepresentation);
	}
	return objects;
    }

    private String getValue(String representation) {
	String[] split = representation.split("\\|@\\|");
	return split[1].substring(0, split[1].length() - 1);
    }

    private String getType(String representation) {
	String[] split = representation.split("\\|@\\|");
	return split[0].substring(1, split[0].length());
    }

    /*
     * Helpers called by reflection
     */

    private String readString(String representation) {
	return getValue(representation);
    }

    private Integer readInteger(String representation) {
	return Integer.valueOf(getValue(representation));
    }

    private Boolean readBoolean(String representation) {
	return Boolean.valueOf(getValue(representation));
    }

    private Short readShort(String representation) {
	return Short.valueOf(getValue(representation));
    }

    private Float readFloat(String representation) {
	return Float.valueOf(getValue(representation));
    }

    private Double readDouble(String representation) {
	return Double.valueOf(getValue(representation));
    }
}
