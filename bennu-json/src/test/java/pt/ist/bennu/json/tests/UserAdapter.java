package pt.ist.bennu.json.tests;

import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserAdapter implements JsonAdapter<User> {

    private static final JsonParser JSON_PARSER = new JsonParser();

    @Override
    public User create(String jsonData, JsonBuilder ctx) {
        final JsonObject jsonObject = JSON_PARSER.parse(jsonData).getAsJsonObject();
        final String name = jsonObject.get("name").getAsString();
        final String password = jsonObject.get("password").getAsString();
        final String number = jsonObject.get("number").getAsString();
        return new User(name, password, number);
    }

    @Override
    public User update(String jsonData, User obj, JsonBuilder ctx) {
        final JsonObject jsonObject = JSON_PARSER.parse(jsonData).getAsJsonObject();
        final String name = jsonObject.get("name").getAsString();
        final String password = jsonObject.get("password").getAsString();
        final String number = jsonObject.get("number").getAsString();
        obj.setName(name);
        obj.setPassword(password);
        obj.setNumber(number);
        return obj;
    }

    @Override
    public JsonElement view(User obj, JsonBuilder ctx) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", obj.getName());
        jsonObject.addProperty("password", obj.getPassword());
        jsonObject.addProperty("number", obj.getNumber());
        return jsonObject;
    }
}
