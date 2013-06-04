package pt.ist.bennu.core.rest.json;

import org.joda.time.DateTime;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

@DefaultJsonAdapter(DateTime.class)
public class DateTimeViewer implements JsonViewer<DateTime> {

    @Override
    public JsonElement view(DateTime dateTime, JsonBuilder context) {
        JsonPrimitive jsonPrimite = new JsonPrimitive(dateTime.toString());
        return jsonPrimite;
    }

}
