package org.fenixedu.bennu.core.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.joda.time.DateTime;

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
