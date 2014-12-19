package org.fenixedu.bennu.oauth.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(ExternalApplicationUsersAdapter.class)
public class ExternalApplicationUsersAdapter implements JsonAdapter<ExternalApplication> {

    @Override
    public ExternalApplication create(JsonElement json, JsonBuilder ctx) {
        return null;
    }

    @Override
    public ExternalApplication update(JsonElement json, ExternalApplication app, JsonBuilder ctx) {
        return null;
    }

    @Override
    public JsonElement view(ExternalApplication obj, JsonBuilder ctx) {
        JsonArray jarr = new JsonArray();
        for (ApplicationUserAuthorization applicationUserAuthorization : obj.getApplicationUserAuthorizationSet()) {
            JsonObject jobj = new JsonObject();
            jobj.addProperty("id", applicationUserAuthorization.getExternalId());
            jobj.addProperty("name", applicationUserAuthorization.getUser().getProfile().getDisplayName());
            jobj.addProperty("authorizations", applicationUserAuthorization.getSessionSet().size());
            jarr.add(jobj);
        }
        return jarr;
    }

}
