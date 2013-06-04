package pt.ist.bennu.json.tests;

import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PersonViewer implements JsonViewer<Person> {

    @Override
    public JsonElement view(Person obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("name", obj.getName());
        if (!obj.getContacts().isEmpty()) {
            json.add("contacts", ctx.view(obj.getContacts()));
        }
        return json;
    }

}
