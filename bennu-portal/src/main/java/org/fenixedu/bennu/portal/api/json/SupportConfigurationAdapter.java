package org.fenixedu.bennu.portal.api.json;

import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonCreator;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.portal.domain.SupportConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SupportConfigurationAdapter
        implements JsonViewer<SupportConfiguration>, JsonCreator<SupportConfiguration> {
    @Override
    public SupportConfiguration create(JsonElement json, JsonBuilder ctx) {
        JsonObject jsonObj = json.getAsJsonObject();
        LocalizedString title = LocalizedString.fromJson(jsonObj.get("title"));
        String email = jsonObj.get("email").getAsString();
        SupportConfiguration supportConfiguration = new SupportConfiguration(title, email);
        return supportConfiguration;
    }


    @Override
    public JsonElement view(SupportConfiguration obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("title", obj.getTitle().getContent());
        json.addProperty("email", obj.getEmailAddress());
        return json;
    }
}
