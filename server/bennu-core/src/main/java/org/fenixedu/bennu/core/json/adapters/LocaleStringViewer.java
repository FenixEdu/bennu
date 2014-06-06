package org.fenixedu.bennu.core.json.adapters;

import java.util.Locale;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Locale.class)
public class LocaleStringViewer implements JsonViewer<Locale> {
    @Override
    public JsonElement view(Locale locale, JsonBuilder ctx) {
        JsonObject object = new JsonObject();
        object.addProperty("tag", locale.toLanguageTag());
        object.addProperty("displayName", locale.getDisplayName(locale));
        object.addProperty("lang", locale.getLanguage());
        return object;
    }
}
