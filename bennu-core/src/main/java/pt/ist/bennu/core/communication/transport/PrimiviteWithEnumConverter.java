/* 
* @(#)PrimiviteWithEnumConverter.java 
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
package pt.ist.bennu.core.communication.transport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import pt.ist.fenixframework.FenixFramework;
import dml.DomainModel;
import dml.ExternalizationElement;
import dml.ValueType;

/**
 * 
 * @author Paulo Abrantes
 * 
 */
public class PrimiviteWithEnumConverter extends PrimitiveConversion {

	private final static DomainModel domainModel = FenixFramework.getDomainModel();

	@Override
	protected boolean isAcceptedType(Class clazz) {
		return super.isAcceptedType(clazz) || clazz.isEnum() || isValueType(clazz);
	}

	private boolean isValueType(Class clazz) {
		return domainModel.findValueType(clazz.getName()) != null;
	}

	public String serializeToSend(ValueType valueType, Object value) {
		List<ExternalizationElement> externalizationElements = valueType.getExternalizationElements();
		int size = externalizationElements.size();
		Object[] valuesToSend = new Object[size + 2];
		valuesToSend[0] = valueType.getClass().getName();
		valuesToSend[1] = value.getClass().getName();

		for (int i = 0; i < size; i++) {
			ExternalizationElement externalizationElement = externalizationElements.get(i);
			try {
				Method method = value.getClass().getMethod(externalizationElement.getMethodName(), new Class[] {});
				valuesToSend[i + 2] = method.invoke(value, new Object[] {}).toString();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return serializeToSend(valuesToSend);
	}

	public <T extends Object> T readValueType(String representation) {
		Object[] objects = readMultipleObjects(representation);
		try {
			Class<?> clazz = Class.forName((String) objects[1]);
			ValueType findValueType = domainModel.findValueType(clazz.getSimpleName());
			String internalizationMethodName = findValueType.getInternalizationMethodName();
			if (internalizationMethodName == null) {
				Constructor<?> constructor = clazz.getConstructor(new Class[] { String.class });
				return (T) constructor.newInstance(objects[2]);
			} else {
				Method m = clazz.getMethod(internalizationMethodName, new Class[] { String.class });
				return (T) m.invoke(null, objects[2]);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String serializeToSend(Enum value) {
		Object[] valuesToSend = new Object[] { value.getClass().getName(), value.toString() };
		return serializeToSend(valuesToSend);
	}

	public <T extends Enum> T readEnum(String representation) {
		Object[] objects = readMultipleObjects(representation);
		try {
			Class<? extends Enum> clazz = (Class<? extends Enum>) Class.forName((String) objects[0]);
			Enum[] values = clazz.getEnumConstants();
			for (Enum value : values) {
				value.toString().equals(objects[1]);
				return (T) value;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String serializeToSend(Object object) {
		if (object != null && object.getClass().isEnum()) {
			return serializeToSend((Enum) object);
		}

		ValueType valueType = domainModel.findValueType(object.getClass().getSimpleName());
		if (valueType != null) {
			return serializeToSend(valueType, object);
		}
		return super.serializeToSend(object);
	}
}
