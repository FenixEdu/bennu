package org.fenixedu.bennu.oauth.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(ExternalApplicationScope.class)
public class ExternalApplicationScopeAdapter implements JsonAdapter<ExternalApplicationScope> {

    @Override
    public ExternalApplicationScope create(JsonElement json, JsonBuilder ctx) {
        ExternalApplicationScope scope = new ExternalApplicationScope();
        JsonObject asJsonObject = json.getAsJsonObject();
        scope.setScopeKey(asJsonObject.get("scopeKey").getAsString());
        scope.setName(asJsonObject.get("name").getAsString());
        scope.setDescription(asJsonObject.get("description").getAsString());
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
        obj.setName(asJsonObject.get("name").getAsString());
        obj.setDescription(asJsonObject.get("description").getAsString());
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
        json.addProperty("name", obj.getName());
        json.addProperty("description", obj.getDescription());
        json.addProperty("service", obj.getService());
        return json;
    }
}
