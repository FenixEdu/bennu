package pt.ist.bennu.json.tests;

import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ContactViewer implements JsonViewer<Contact> {

    @Override
    public JsonElement view(Contact obj, JsonBuilder context) {
        JsonObject json = new JsonObject();
        json.addProperty("value", obj.getValue());
        json.addProperty("type", obj.getType().name());
        return json;
    }
}
