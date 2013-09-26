package pt.ist.bennu.json.tests;

import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UserAdapter implements JsonAdapter<User> {

    @Override
    public User create(JsonElement jsonElement, JsonBuilder ctx) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        final String name = jsonObject.get("name").getAsString();
        final String password = jsonObject.get("password").getAsString();
        final String number = jsonObject.get("number").getAsString();
        return new User(name, password, number);
    }

    @Override
    public User update(JsonElement jsonElement, User obj, JsonBuilder ctx) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
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
