package pt.ist.bennu.json;

import com.google.gson.JsonElement;

public interface JsonViewer<T> {
    JsonElement view(T obj, JsonBuilder ctx);
}
