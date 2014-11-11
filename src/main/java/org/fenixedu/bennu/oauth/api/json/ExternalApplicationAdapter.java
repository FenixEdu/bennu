package org.fenixedu.bennu.oauth.api.json;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;

import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(ExternalApplication.class)
public class ExternalApplicationAdapter implements JsonAdapter<ExternalApplication> {

    @Override
    public ExternalApplication create(JsonElement json, JsonBuilder ctx) {

        JsonObject jObj = json.getAsJsonObject();
        ExternalApplication app = new ExternalApplication();
        app.setName(jObj.get("name").getAsString());
        app.setDescription(jObj.get("description").getAsString());
        app.setSiteUrl(jObj.get("siteUrl").getAsString());
        app.setRedirectUrl(jObj.get("redirectUrl").getAsString());

        if (jObj.has("logo") && !jObj.get("logo").isJsonNull()) {
            app.setLogo(jObj.get("logo").getAsString().getBytes(Charset.forName("UTF-8")));
        }

        if (jObj.has("scopes") && !jObj.get("scopes").isJsonNull()) {

            JsonArray jArr = jObj.get("scopes").getAsJsonArray();

            for (int i = 0; i < jArr.size(); i++) {
                JsonObject scopeJsonObject = jArr.get(i).getAsJsonObject();
                if (scopeJsonObject.has("selected") && scopeJsonObject.get("selected").getAsBoolean()) {
                    String oid = scopeJsonObject.get("id").getAsString();
                    app.addScopes(FenixFramework.getDomainObject(oid));
                }
            }
        }
        return app;
    }

    @Override
    public ExternalApplication update(JsonElement json, ExternalApplication obj, JsonBuilder ctx) {

        JsonObject jObj = json.getAsJsonObject();
        obj.setName(jObj.get("name") == null ? "" : jObj.get("name").getAsString());
        obj.setDescription(jObj.get("description") == null ? "" : jObj.get("description").getAsString());
        obj.setSiteUrl(jObj.get("siteUrl") == null ? "" : jObj.get("siteUrl").getAsString());
        obj.setRedirectUrl(jObj.get("redirectUrl") == null ? "" : jObj.get("redirectUrl").getAsString());

        if (jObj.has("logo") && !jObj.get("logo").isJsonNull()) {
            obj.setLogo(jObj.get("logo").getAsString().getBytes(Charset.forName("UTF-8")));
        }

        if (jObj.has("scopes") && !jObj.get("scopes").isJsonNull()) {
            List<ExternalApplicationScope> newScopes = new ArrayList<ExternalApplicationScope>();

            JsonArray jArr = jObj.get("scopes").getAsJsonArray();
            for (int i = 0; i < jArr.size(); i++) {

                JsonObject scopeJsonObject = jArr.get(i).getAsJsonObject();
                if (scopeJsonObject.get("selected").getAsBoolean()) {
                    String oid = scopeJsonObject.get("id").getAsString();
                    newScopes.add(FenixFramework.getDomainObject(oid));
                }
            }
            obj.setScopeList(newScopes);
        } else {
            obj.setScopeList(new ArrayList<ExternalApplicationScope>());
        }

        return obj;
    }

    @Override
    public JsonElement view(ExternalApplication obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("name", obj.getName());
        json.addProperty("description", obj.getDescription());
        json.addProperty("siteUrl", obj.getSiteUrl());
        json.addProperty("active", obj.isActive());
        json.addProperty("state", obj.getState().getName());
        json.addProperty("secret", obj.getSecret());
        json.addProperty("redirectUrl", obj.getRedirectUrl());
        json.addProperty("author", obj.getAuthorNameForUserDialog());
        json.addProperty("authorizations", obj.getApplicationUserAuthorizationSet().size());

        JsonArray scopeArray = new JsonArray();
        List<ExternalApplicationScope> appScopes = obj.getScopeList();

        for (ExternalApplicationScope externalApplicationScope : Bennu.getInstance().getScopesSet()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", externalApplicationScope.getExternalId());
            jsonObject.addProperty("name", externalApplicationScope.getName());

            if (appScopes.contains(externalApplicationScope)) {
                jsonObject.addProperty("selected", true);
            } else {
                jsonObject.addProperty("selected", false);
            }
            scopeArray.add(jsonObject);
        }

        json.add("scopes", scopeArray);

        if (obj.getLogo() != null && !Strings.isNullOrEmpty(obj.getLogo().toString())) {
            String logoUrl =
                    CoreConfiguration.getConfiguration().applicationUrl() + "/api/bennu-oauth/applications/"
                            + obj.getExternalId() + "/logo";

            json.addProperty("logoUrl", logoUrl);
        }

        return json;
    }
}
