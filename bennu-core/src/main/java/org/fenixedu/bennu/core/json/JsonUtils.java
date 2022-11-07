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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonUtils {
    public static String getString(final JsonObject json, final String property) {
        final JsonPrimitive primitive = getPrimitive(json, property);
        return primitive != null ? primitive.getAsString() : null;
    }

    public static int getInt(final JsonObject json, final String property) {
        final JsonPrimitive primitive = getPrimitive(json, property);
        return primitive != null ? primitive.getAsInt() : null;
    }

    public static LocalDate getLocalDate(final JsonObject json, final String property) {
        final JsonPrimitive primitive = getPrimitive(json, property);
        return primitive != null ?  ISODateTimeFormat.date().parseDateTime(primitive.getAsString()).toLocalDate() : null;
    }

    public static JsonPrimitive getPrimitive(final JsonObject json, final String property) {
        if (!json.has(property)) {
            return null;
        }
        final JsonElement value = json.get(property);
        if (!value.isJsonPrimitive()) {
            throw BennuCoreDomainException.wrongJsonFormat(value, "primitive");
        }
        return value.getAsJsonPrimitive();
    }

    public static <T extends Object> T get(final JsonObject json, final String property, final JsonBuilder ctx, final Class<T> type) {
        if (!json.has(property)) {
            return null;
        }
        return ctx.create(json.get(property), type);
    }

    public static void put(final JsonObject json, final String property, final String value) {
        if (value != null) {
            json.addProperty(property, value);
        }
    }

    public static void put(final JsonObject json, final String property, final JsonElement value) {
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

    public static JsonArray toJsonArray(final Consumer<JsonArray> consumer) {
        final JsonArray result = new JsonArray();
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

    public static <T extends DomainObject> Stream<T> toDomainObjectStream(final JsonArray array) {
        return array.asList().stream()
                .filter(e -> e != null && !e.isJsonNull())
                .map(JsonElement::getAsString)
                .filter(s -> !s.isEmpty())
                .map(FenixFramework::getDomainObject)
                .map(o -> (T) o);
    }

    public static <T extends DomainObject> Set<T> toDomainObjects(final JsonArray array) {
        return toDomainObjectStream(array).map(o -> (T) o).collect(Collectors.toSet());
    }

    public static JsonElement parseJsonElement(final String string) {
        return string != null ? JsonParser.parseString(string) : null;
    }

    public static JsonObject parse(final String string) {
        final JsonElement element = parseJsonElement(string);
        return element != null ? element.getAsJsonObject() : null;
    }

    public static JsonArray parseJsonArray(final String string) {
        final JsonElement element = parseJsonElement(string);
        return element != null ? element.getAsJsonArray() : null;
    }

}
