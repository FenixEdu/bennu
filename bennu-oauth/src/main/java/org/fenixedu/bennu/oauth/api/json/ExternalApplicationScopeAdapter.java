package org.fenixedu.bennu.oauth.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(ExternalApplicationScope.class)
public class ExternalApplicationScopeAdapter implements JsonAdapter<ExternalApplicationScope> {

    @Override
    public ExternalApplicationScope create(JsonElement json, JsonBuilder ctx) {
        ExternalApplicationScope scope = new ExternalApplicationScope();
        JsonObject asJsonObject = json.getAsJsonObject();
        scope.setScopeKey(asJsonObject.get("scopeKey").getAsString());
        scope.setName(LocalizedString.fromJson(asJsonObject.get("name")));
        scope.setDescription(LocalizedString.fromJson(asJsonObject.get("description")));
        if (asJsonObject.has("service")) {
            scope.setService(asJsonObject.get("service").getAsBoolean());
        } else {
            scope.setService(false);
        }
        return scope;
    }

    @Override
    public ExternalApplicationScope update(JsonElement json, ExternalApplicationScope obj, JsonBuilder ctx) {
        JsonObject asJsonObject = json.getAsJsonObject();
        obj.setName(LocalizedString.fromJson(asJsonObject.get("name")));
        obj.setDescription(LocalizedString.fromJson(asJsonObject.get("description")));
        if (asJsonObject.has("service")) {
            obj.setService(asJsonObject.get("service").getAsBoolean());
        } else {
            obj.setService(false);
        }
        return obj;
    }

    @Override
    public JsonElement view(ExternalApplicationScope obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("scopeKey", obj.getScopeKey());
        json.add("name", obj.getName().json());
        json.add("description", obj.getDescription().json());
        json.addProperty("service", obj.getService());
        return json;
    }
}
