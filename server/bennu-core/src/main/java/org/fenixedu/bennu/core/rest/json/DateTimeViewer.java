package org.fenixedu.bennu.core.rest.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.commons.json.JsonBuilder;
import org.fenixedu.commons.json.JsonViewer;
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
