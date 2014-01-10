package org.fenixedu.bennu.core.json.adapters;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;

@DefaultJsonAdapter(LocalizedString.class)
public class LocalizedStringViewer implements JsonAdapter<LocalizedString> {
    @Override
    public LocalizedString create(JsonElement json, JsonBuilder ctx) {
        return LocalizedString.fromJson(json);
    }

    @Override
    public LocalizedString update(JsonElement json, LocalizedString i18NString, JsonBuilder ctx) {
        return i18NString.append(LocalizedString.fromJson(json));
    }

    @Override
    public JsonElement view(LocalizedString obj, JsonBuilder ctx) {
        return obj.json();
    }

}
