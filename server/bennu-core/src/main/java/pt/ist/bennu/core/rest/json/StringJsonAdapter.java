package pt.ist.bennu.core.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonCreator;
import pt.ist.bennu.json.JsonViewer;

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