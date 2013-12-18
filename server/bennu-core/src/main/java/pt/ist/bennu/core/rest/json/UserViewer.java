package pt.ist.bennu.core.rest.json;

import org.fenixedu.commons.json.JsonBuilder;
import org.fenixedu.commons.json.JsonViewer;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.domain.User;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(User.class)
public class UserViewer implements JsonViewer<User> {

    @Override
    public JsonElement view(User user, JsonBuilder context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", user.getExternalId());
        jsonObject.addProperty("username", user.getUsername());
        jsonObject.addProperty("name", user.getPresentationName());
        return jsonObject;
    }

}
