package org.fenixedu.bennuAdmin.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.twilio.rest.api.v2010.account.sip.Domain;
import jvstm.util.Pair;
import org.fenixedu.bennu.core.json.JsonUtils;
import org.fenixedu.bennuAdmin.util.DomainObjectUtils;
import org.fenixedu.commons.stream.StreamUtils;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.*;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        data.addProperty("id", domainObject.getExternalId());
        data.addProperty("class", domainObject.getClass().getSimpleName());
        data.add("modifiers", modifiers(domClass));
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
      data.addProperty("name", role.getName());
    };
  }

  private static JsonElement modifiers(ModifiableEntity entity) {
    JsonArray array = new JsonArray();
    for (Modifier modifier : entity.getModifiers()) {
      array.add(new JsonPrimitive(modifier.name()));
    }
    return array;
  }
}
