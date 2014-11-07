package org.fenixedu.bennu.oauth.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(ApplicationUserSession.class)
public class ExternalApplicationAuthorizationSessionAdapter implements JsonAdapter<ApplicationUserSession> {

    @Override
    public JsonElement view(ApplicationUserSession obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("date", obj.getCreationDate().toString());
        json.addProperty("device", obj.getDeviceId());
        json.addProperty("refresh_token", obj.getRefreshToken());
        json.addProperty("access_token", obj.getAccessToken());
        return json;
    }

    @Override
    public ApplicationUserSession create(JsonElement json, JsonBuilder ctx) {
        return null;
    }

    @Override
    public ApplicationUserSession update(JsonElement json, ApplicationUserSession obj, JsonBuilder ctx) {
        return null;
    }
}
