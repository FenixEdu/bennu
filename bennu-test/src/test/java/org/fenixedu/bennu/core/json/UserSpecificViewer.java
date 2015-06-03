package org.fenixedu.bennu.core.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UserSpecificViewer implements JsonViewer<User> {

    @Override
    public JsonElement view(User user, JsonBuilder ctx) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", user.getName());
        obj.addProperty("cenas", "isto sao cenas");
        return obj;
    }

}
