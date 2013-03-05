package pt.ist.bennu.core.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.util.MultiLanguageString;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;

@DefaultJsonAdapter(MultiLanguageString.class)
public class MultiLanguageStringViewer implements JsonViewer<MultiLanguageString> {

    @Override
    public JsonElement view(MultiLanguageString obj, JsonBuilder ctx) {
        return obj.json();
    }

}
