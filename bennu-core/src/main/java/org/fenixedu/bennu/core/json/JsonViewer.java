package org.fenixedu.bennu.core.json;

import com.google.gson.JsonElement;

public interface JsonViewer<T> {
    JsonElement view(T obj, JsonBuilder ctx);
}
