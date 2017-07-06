package org.fenixedu.bennu.portal.api.json;

import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.portal.domain.SupportConfiguration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SupportConfigurationViewer extends SupportConfigurationAdapter {
    @Override
    public JsonElement view(SupportConfiguration obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("title", obj.getTitle().getContent());
        return json;
    }
}
