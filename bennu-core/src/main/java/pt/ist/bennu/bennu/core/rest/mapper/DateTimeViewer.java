package pt.ist.bennu.bennu.core.rest.mapper;

import org.joda.time.DateTime;

import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class DateTimeViewer implements JsonViewer<DateTime> {

    @Override
    public JsonElement view(DateTime dateTime, JsonBuilder context) {
        JsonPrimitive jsonPrimite = new JsonPrimitive(dateTime.toString());
        return jsonPrimite;
    }

}
