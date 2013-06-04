package pt.ist.bennu.core.rest.json;

import java.util.Locale;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Locale.class)
public class LocaleStringViewer implements JsonViewer<Locale> {
    @Override
    public JsonElement view(Locale locale, JsonBuilder ctx) {
        JsonObject object = new JsonObject();
        object.addProperty("tag", locale.toLanguageTag());
        return object;
    }
}
