package pt.ist.bennu.json;

import com.google.gson.JsonElement;

public interface JsonCreator<T> {

    T create(JsonElement jsonElement, JsonBuilder jsonRegistry);
}
