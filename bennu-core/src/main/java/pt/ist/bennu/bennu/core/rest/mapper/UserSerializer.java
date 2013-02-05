package pt.ist.bennu.bennu.core.rest.mapper;

import java.lang.reflect.Type;

import pt.ist.bennu.core.domain.User;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class UserSerializer implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext ctx) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", user.getOid());
        jsonObject.addProperty("username", user.getUsername());
        return jsonObject;
    }

}
