package pt.ist.bennu.core.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonImporter {
    private static JsonParser parser = new JsonParser();

    public static JsonElement importJson(String json) {
        return parser.parse(json);
    }
}
