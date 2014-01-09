package org.fenixedu.bennu.core.json;

import com.google.common.io.BaseEncoding;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UserSpecificCreator implements JsonCreator<User> {

    @Override
    public User create(JsonElement jsonElement, JsonBuilder ctx) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        final String name = jsonObject.get("name").getAsString();
        final String password = BaseEncoding.base64().encode(jsonObject.get("password").getAsString().getBytes());
        final String number = jsonObject.get("number").getAsString();
        return new User(name, password, number);
    }
}
