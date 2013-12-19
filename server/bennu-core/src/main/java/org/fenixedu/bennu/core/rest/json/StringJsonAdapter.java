package org.fenixedu.bennu.core.rest.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.commons.json.JsonBuilder;
import org.fenixedu.commons.json.JsonCreator;
import org.fenixedu.commons.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

@DefaultJsonAdapter(String.class)
public class StringJsonAdapter implements JsonViewer<String>, JsonCreator<String> {
    @Override
    public String create(JsonElement json, JsonBuilder ctx) {
        return json.getAsString();
    }

    @Override
    public JsonElement view(String obj, JsonBuilder ctx) {
        return new JsonPrimitive(obj);
    }
}