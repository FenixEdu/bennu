package org.fenixedu.bennu.core.json;

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
