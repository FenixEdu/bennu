package pt.ist.bennu.json;

import com.google.gson.JsonObject;

public interface JsonCreator<T> {

    T create(JsonObject json, JsonBuilder jsonRegistry);
}
