package pt.ist.bennu.core.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.util.MultiLanguageString;
import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;

import com.google.gson.JsonElement;

@DefaultJsonAdapter(MultiLanguageString.class)
public class MultiLanguageStringViewer implements JsonAdapter<MultiLanguageString> {
    @Override
    public MultiLanguageString create(JsonElement json, JsonBuilder ctx) {
        return MultiLanguageString.fromJson(json);
    }

    @Override
    public MultiLanguageString update(JsonElement json, MultiLanguageString mls, JsonBuilder ctx) {
        return mls.append(MultiLanguageString.fromJson(json));
    }

    @Override
    public JsonElement view(MultiLanguageString obj, JsonBuilder ctx) {
        return obj.json();
    }

}
