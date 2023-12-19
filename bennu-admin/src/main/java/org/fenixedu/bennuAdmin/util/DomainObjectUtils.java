package org.fenixedu.bennuAdmin.util;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.ValueTypeSerializer;
import pt.ist.fenixframework.core.AbstractDomainObject;
import pt.ist.fenixframework.dml.DomainClass;
import pt.ist.fenixframework.dml.Role;
import pt.ist.fenixframework.dml.Slot;
import pt.ist.fenixframework.dml.ValueType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

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

  public static String getSlotValueString(final DomainObject domainObject, final Slot slot) {
    Object value = getSlotValue(domainObject, slot);
    if (value instanceof byte[]) {
      return Base64.getEncoder().encodeToString((byte[]) value);
    }
    return null;
  }

  public static Object getPrimitiveValueFor(ValueType vt, Object value) {
    try {
      Method method =
          ValueTypeSerializer.class.getDeclaredMethod(
              "serialize$" + vt.getDomainName().replace('.', '$'), Class.forName(vt.getFullname()));
      return method.invoke(null, value);
    } catch (Exception e) {
      return null;
    }
  }

  public static Method getMethod(final DomainObject domainObject, final String slotName) {
    final String methodName =
        "get" + Character.toUpperCase(slotName.charAt(0)) + slotName.substring(1);
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

  public static Set<Slot> getDomainObjectSlots(DomainObject domainObject) {
    DomainClass domClass =
        FenixFramework.getDomainModel().findClass(domainObject.getClass().getName());
    Set<Slot> slots = new HashSet<>();
    for (DomainClass dc = domClass; dc != null; dc = (DomainClass) dc.getSuperclass()) {
      slots.addAll(dc.getSlotsList());
    }
    return slots;
  }

  public static Set<Role> getRoles(final DomainClass domainClass, boolean one) {
    final Set<Role> result = new HashSet<Role>();
    for (DomainClass dc = domainClass; dc != null; dc = (DomainClass) dc.getSuperclass()) {
      for (final Role role : dc.getRoleSlotsList()) {
        // Roles without name are unidirectional and should be skipped
        if (role.getName() == null) {
          continue;
        }
        if (one ? (role.getMultiplicityUpper() == 1) : (role.getMultiplicityUpper() != 1)) {
          result.add(role);
        }
      }
    }
    return result;
  }

  public static String getRelationSlot(final DomainObject domainObject, final Role role) {
    final Method method = getMethod(domainObject, role.getName());
    if (method != null) {
      try {
        DomainObject obj = (DomainObject) method.invoke(domainObject);
        return obj == null ? null : obj.getExternalId();
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        return "!!EXCEPTION!! " + e.getMessage();
      }
    }
    return null;
  }

  public static Set<DomainObject> getRelationSet(final DomainObject domainObject, final Role role) {
    final Method method = getMethod(domainObject, role.getName() + "Set");
    if (method != null) {
      try {
        return (Set<DomainObject>) method.invoke(domainObject);
      } catch (final Exception e) {
        return null;
      }
    }
    return null;
  }
}
