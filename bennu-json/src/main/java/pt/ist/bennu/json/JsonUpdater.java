package pt.ist.bennu.json;

import com.google.gson.JsonObject;

public interface JsonUpdater<T> {
    T update(JsonObject json, T obj, JsonBuilder jsonRegistry);
}
