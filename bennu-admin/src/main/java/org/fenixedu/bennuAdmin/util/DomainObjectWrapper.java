package org.fenixedu.bennuAdmin.util;

import com.google.gson.JsonElement;
import org.fenixedu.bennu.core.json.ImmutableJsonElement;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.Role;
import pt.ist.fenixframework.dml.Slot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DomainObjectWrapper {
  private final DomainObject domainObject;

  public DomainObjectWrapper(DomainObject domainObject) {
    this.domainObject = domainObject;
  }

  public void setSlotValue(Slot slot, JsonElement value)
      throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
    Method setMethod = DomainObjectUtils.getMethod("set", domainObject, slot.getName());

    if (setMethod == null) {
      throw new Error("Set method not found");
    }

    setMethod.setAccessible(true);
    String type = slot.getTypeName().substring(slot.getTypeName().lastIndexOf(".") + 1);

    switch (type) {
      case "LocalizedString" -> setMethod.invoke(domainObject, LocalizedString.fromJson(value));
      case "ImmutableJsonElement<com.google.gson.JsonObject>" ->
          setMethod.invoke(domainObject, ImmutableJsonElement.of(value.getAsJsonObject()));
      case "String" -> setMethod.invoke(domainObject, value.getAsString());
      case "Boolean", "boolean" -> setMethod.invoke(domainObject, value.getAsBoolean());
      case "DateTime" -> setMethod.invoke(domainObject, DateTime.parse(value.getAsString()));
      case "Integer" -> setMethod.invoke(domainObject, value.getAsBigInteger());
      default -> {
        Class<?> cls = Class.forName(slot.getSlotType().getFullname());
        setMethod.invoke(domainObject, cls.cast(value.getAsString()));
      }
    }
  }

  public void setRole(Role role, String objectId)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
    Method setMethod = DomainObjectUtils.getMethod("set", domainObject, role.getName());

    if (setMethod == null) {
      throw new Error("Set method not found");
    }

    setMethod.setAccessible(true);
    Class<?> cls = Class.forName(role.getType().getFullName());

    DomainObject object = FenixFramework.getDomainObject(objectId);

    if (!FenixFramework.isDomainObjectValid(object)) {
      throw new Error("Invalid domain object");
    }

    setMethod.invoke(domainObject, cls.cast(object));
  }
}
