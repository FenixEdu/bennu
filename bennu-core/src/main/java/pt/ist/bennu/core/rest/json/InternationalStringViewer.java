package pt.ist.bennu.core.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.i18n.InternationalString;
import pt.ist.bennu.json.JsonAdapter;
import pt.ist.bennu.json.JsonBuilder;

import com.google.gson.JsonElement;

@DefaultJsonAdapter(InternationalString.class)
public class InternationalStringViewer implements JsonAdapter<InternationalString> {
    @Override
    public InternationalString create(JsonElement json, JsonBuilder ctx) {
        return InternationalString.fromJson(json);
    }

    @Override
    public InternationalString update(JsonElement json, InternationalString i18NString, JsonBuilder ctx) {
        return i18NString.append(InternationalString.fromJson(json));
    }

    @Override
    public JsonElement view(InternationalString obj, JsonBuilder ctx) {
        return obj.json();
    }

}
