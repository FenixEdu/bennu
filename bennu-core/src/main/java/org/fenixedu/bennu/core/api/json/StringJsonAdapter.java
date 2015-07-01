package org.fenixedu.bennu.core.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonCreator;
import org.fenixedu.bennu.core.json.JsonViewer;

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