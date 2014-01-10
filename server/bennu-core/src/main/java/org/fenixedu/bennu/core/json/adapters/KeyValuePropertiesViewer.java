package org.fenixedu.bennu.core.json.adapters;

import java.util.Map.Entry;
import java.util.Properties;

import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class KeyValuePropertiesViewer implements JsonViewer<Properties> {
    @Override
    public JsonElement view(Properties properties, JsonBuilder ctx) {
        JsonArray json = new JsonArray();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            JsonObject object = new JsonObject();
            object.addProperty("key", (String) entry.getKey());
            object.addProperty("value", (String) entry.getValue());
            json.add(object);
        }
        return json;
    }
}
