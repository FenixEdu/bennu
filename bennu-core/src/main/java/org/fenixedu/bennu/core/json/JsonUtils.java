package org.fenixedu.bennu.core.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JsonUtils {
    public static String getString(JsonObject json, String property) {
        JsonPrimitive primitive = getPrimitive(json, property);
        return primitive != null ? primitive.getAsString() : null;
    }

    public static int getInt(JsonObject json, String property) {
        JsonPrimitive primitive = getPrimitive(json, property);
        return primitive != null ? primitive.getAsInt() : null;
    }

    public static LocalDate getLocalDate(JsonObject json, String property) {
        JsonPrimitive primitive = getPrimitive(json, property);
        return primitive != null ?  ISODateTimeFormat.date().parseDateTime(primitive.getAsString()).toLocalDate() : null;
    }

    public static JsonPrimitive getPrimitive(JsonObject json, String property) {
        if (!json.has(property)) {
            return null;
        }
        JsonElement value = json.get(property);
        if (!value.isJsonPrimitive()) {
            throw BennuCoreDomainException.wrongJsonFormat(value, "primitive");
        }
        return value.getAsJsonPrimitive();
    }

    public static <T extends Object> T get(JsonObject json, String property, JsonBuilder ctx, Class<T> type) {
        if (!json.has(property)) {
            return null;
        }
        return ctx.create(json.get(property), type);
    }

    public static void put(JsonObject json, String property, String value) {
        if (value != null) {
            json.addProperty(property, value);
        }
    }

    public static void put(JsonObject json, String property, JsonElement value) {
        if (value != null) {
            json.add(property, value);
        }
    }

    public static <T extends DomainObject> T toDomainObject(final JsonObject jo, final String slot) {
        final JsonElement je = jo.get(slot);
        return je == null || je.isJsonNull() || je.getAsString().isEmpty() ? null :
                FenixFramework.getDomainObject(je.getAsString());
    }

    public static String get(final JsonObject o, final String slot) {
        final JsonElement e = o.get(slot);
        return e == null || e.isJsonNull() ? null : e.getAsString();
    }

    public static <R, T> R collectElements(final JsonArray array, final Supplier<R> supplier,
                                           final BiConsumer<R, JsonElement> mapper) {
        final R result = supplier.get();
        for (final JsonElement element : array) {
            mapper.accept(result, element);
        }
        return result;
    }

    public static JsonObject toJson(final Consumer<JsonObject> consumer) {
        final JsonObject result = new JsonObject();
        consumer.accept(result);
        return result;
    }

    public static void addIf(final JsonObject result, final String key, final String value) {
        if (value != null) {
            result.addProperty(key, value);
        }
    }

    public static void addIf(final JsonObject result, final String key, final Number value) {
        if (value != null) {
            result.addProperty(key, value);
        }
    }

    public static void addIf(final JsonObject result, final String key, final Boolean value) {
        if (value != null) {
            result.addProperty(key, value);
        }
    }

    public static void addIf(final JsonObject result, final String key, final JsonElement value) {
        if (value != null) {
            result.add(key, value);
        }
    }

    public static DateTime toDateTime(final JsonObject jo, final String slot) {
        final JsonElement je = jo.get(slot);
        return je == null || je.isJsonNull() || je.getAsString().isEmpty() ? null : new DateTime(je.getAsString());
    }

    public static LocalDate toLocalDate(final JsonObject data, final String slot, final String pattern) {
        final JsonElement je = data.get(slot);
        return je == null || je.isJsonNull() || je.getAsString().isEmpty() ? null :
                DateTime.parse(je.getAsString(), DateTimeFormat.forPattern(pattern)).toLocalDate();
    }

    public static LocalDate toLocalDate(final JsonObject data, final String slot, final DateTimeFormatter formatter) {
        final JsonElement je = data.get(slot);
        return je == null || je.isJsonNull() || je.getAsString().isEmpty() ? null :
                DateTime.parse(je.getAsString(), formatter).toLocalDate();
    }

    public static Boolean toBoolean(final JsonObject data, final String slot) {
        final JsonElement je = data.get(slot);
        return je == null || je.isJsonNull() ? null : Boolean.valueOf(je.getAsBoolean());
    }

    public static String toString(final JsonObject data, final String slot) {
        final JsonElement je = data.get(slot);
        return je == null || je.isJsonNull() ? null : je.getAsString();
    }

    public static <T extends DomainObject> Set<T> toDomainObjects(final JsonArray array) {
        final Set<T> result = new HashSet<>();
        for (final JsonElement je : array) {
            final T t = je == null || je.isJsonNull() || je.getAsString().isEmpty() ? null :
                    FenixFramework.getDomainObject(je.getAsString());
            if (t != null) {
                result.add(t);
            }
        }
        return result;
    }

    public static JsonObject parse(final String string) {
        return new JsonParser().parse(string).getAsJsonObject();
    }

}
