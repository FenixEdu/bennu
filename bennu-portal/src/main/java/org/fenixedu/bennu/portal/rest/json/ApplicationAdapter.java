package org.fenixedu.bennu.portal.rest.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.portal.model.Application;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Application.class)
public class ApplicationAdapter implements JsonViewer<Application> {

    @Override
    public JsonElement view(Application obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();

        json.addProperty("key", obj.getKey());
        json.addProperty("group", obj.getGroup());
        json.addProperty("title", obj.getTitle().getContent());
        json.addProperty("description", obj.getDescription().getContent());
        return json;
    }
}
