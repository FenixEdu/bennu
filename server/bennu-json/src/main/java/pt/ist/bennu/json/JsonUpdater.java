package pt.ist.bennu.json;

import com.google.gson.JsonElement;


public interface JsonUpdater<T> {
    T update(JsonElement json, T obj, JsonBuilder ctx);
}
