package pt.ist.bennu.json.tests;

import org.apache.commons.codec.binary.Base64;

import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonCreator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserSpecificCreator implements JsonCreator<User> {

    private static final JsonParser JSON_PARSER = new JsonParser();

    @Override
    public User create(String jsonData, JsonBuilder ctx) {
        final JsonObject jsonObject = JSON_PARSER.parse(jsonData).getAsJsonObject();
        final String name = jsonObject.get("name").getAsString();
        final String password = Base64.encodeBase64String(jsonObject.get("password").getAsString().getBytes());
        final String number = jsonObject.get("number").getAsString();
        return new User(name, password, number);
    }
}
