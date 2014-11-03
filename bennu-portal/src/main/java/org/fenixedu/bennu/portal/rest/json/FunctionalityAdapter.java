package org.fenixedu.bennu.portal.rest.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.portal.model.Functionality;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Functionality.class)
public class FunctionalityAdapter implements JsonViewer<Functionality> {

    @Override
    public JsonElement view(Functionality obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();

        json.addProperty("provider", obj.getProvider());
        json.addProperty("key", obj.getKey());
        json.addProperty("group", obj.getAccessGroup());
        json.addProperty("title", obj.getTitle().getContent());
        json.addProperty("description", obj.getDescription().getContent());

        return json;
    }

}
