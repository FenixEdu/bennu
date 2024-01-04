package org.fenixedu.bennuAdmin.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.fenixedu.bennu.core.json.JsonUtils;
import org.fenixedu.bennuAdmin.util.DomainObjectUtils;
import org.fenixedu.commons.stream.StreamUtils;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.*;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;

public class Schema {
  public static <T> JsonObject toJson(final BiConsumer<JsonObject, T> consumer, final T object) {
    return JsonUtils.toJson(data -> consumer.accept(data, object));
  }

  public static <T> JsonArray toJson(
      final BiConsumer<JsonObject, T> consumer, final Collection<T> collection) {
    return StreamUtils.toJsonArray(consumer, collection.stream());
  }

  public static final BiConsumer<JsonObject, DomainObject> DOMAIN_OBJECT =
      (data, domainObject) -> {
        DomainClass domClass =
            FenixFramework.getDomainModel().findClass(domainObject.getClass().getName());
        data.addProperty("objectId", domainObject.getExternalId());
        data.addProperty("class", domainObject.getClass().getSimpleName());
        data.addProperty("type", domainObject.getClass().getTypeName());
        data.add("modifiers", modifiers(domClass));
      };

  public static final BiConsumer<JsonObject, DomainObject> DOMAIN_OBJECT_META =
          (data, domainObject) -> {
              try {
                  domainObject.getClass().getDeclaredMethod("delete");
                  data.addProperty("deletable", true);
              } catch (NoSuchMethodException err) {
                  data.addProperty("deletable", false);
              }
          };

  public static BiConsumer<JsonObject, Slot> DOMAIN_OBJECT_SLOT(DomainObject domainObject) {
    return (data, slot) -> {
      data.addProperty("name", slot.getName());
      data.addProperty("type", slot.getSlotType().getFullname());
      data.add("modifiers", modifiers(slot));
      data.addProperty("value", DomainObjectUtils.getSlotValueString(domainObject, slot));
    };
  }

  public static BiConsumer<JsonObject, Role> DOMAIN_OBJECT_ROLE(DomainObject domainObject) {
    return (data, role) -> {
      data.addProperty("objectId", DomainObjectUtils.getRelationSlot(domainObject, role));
      data.addProperty("name", role.getName());
      data.addProperty("type", role.getType().getFullName());
      data.add("modifiers", modifiers(role));
    };
  }

  public static final BiConsumer<JsonObject, Role> DOMAIN_OBJECT_ROLE_SET =
      (data, role) -> {
        data.addProperty("name", role.getName());
        data.addProperty("type", role.getType().getFullName());
        data.add("modifiers", modifiers(role));
      };

  private static JsonElement modifiers(ModifiableEntity entity) {
    JsonArray array = new JsonArray();
    for (Modifier modifier : entity.getModifiers()) {
      array.add(new JsonPrimitive(modifier.name()));
    }
    return array;
  }
}
