package org.fenixedu.bennuAdmin.util;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.ValueTypeSerializer;
import pt.ist.fenixframework.core.AbstractDomainObject;
import pt.ist.fenixframework.dml.Slot;
import pt.ist.fenixframework.dml.ValueType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DomainObjectUtils {
    public static Object getSlotValue(final DomainObject domainObject, final Slot slot) {
        final Method method = getMethod(domainObject, slot.getName());
        if (method != null) {
            try {
                Object value = method.invoke(domainObject);
                ValueType vt = slot.getSlotType();
                if (vt.isBuiltin() || vt.isEnum() || value == null) {
                    return value;
                }
                return getPrimitiveValueFor(vt, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                return "!!EXCEPTION!! " + e.getMessage();
            }
        }
        return null;
    }

    public static Object getPrimitiveValueFor(ValueType vt, Object value) {
        try {
            Method method =
                    ValueTypeSerializer.class.getDeclaredMethod("serialize$" + vt.getDomainName().replace('.', '$'),
                            Class.forName(vt.getFullname()));
            return method.invoke(null, value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Method getMethod(final DomainObject domainObject, final String slotName) {
        final String methodName = "get" + Character.toUpperCase(slotName.charAt(0)) + slotName.substring(1);
        if (domainObject != null && methodName != null && !methodName.isEmpty()) {
            Class<?> clazz = domainObject.getClass();
            while (clazz != AbstractDomainObject.class) {
                try {
                    Method method = clazz.getDeclaredMethod(methodName);
                    method.setAccessible(true);
                    return method;
                } catch (NoSuchMethodException | SecurityException e) {
                    // do nothing
                }
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
