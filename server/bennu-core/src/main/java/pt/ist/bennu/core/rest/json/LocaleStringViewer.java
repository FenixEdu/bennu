package pt.ist.bennu.core.rest.json;

import java.util.Locale;

import org.fenixedu.commons.json.JsonBuilder;
import org.fenixedu.commons.json.JsonViewer;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;

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
