package pt.ist.bennu.bennu.core.rest.mapper;

import java.lang.reflect.Type;

import org.joda.time.DateTime;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateTimeSerializer implements JsonSerializer<DateTime> {

    @Override
    public JsonElement serialize(DateTime dateTime, Type type, JsonSerializationContext context) {
        JsonPrimitive jsonPrimite = new JsonPrimitive(dateTime.toString());
        return jsonPrimite;
    }

}
