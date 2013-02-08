package pt.ist.bennu.bennu.core.rest.mapper;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UserViewer implements JsonViewer<User> {

    @Override
    public JsonElement view(User user, JsonBuilder context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", user.getExternalId());
        jsonObject.addProperty("username", user.getUsername());
        return jsonObject;
    }

}
