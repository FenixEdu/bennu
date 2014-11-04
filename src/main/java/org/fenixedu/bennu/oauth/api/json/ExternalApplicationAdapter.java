package org.fenixedu.bennu.oauth.api.json;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
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
                String oid = jArr.get(i).getAsString();
                app.addScopes(FenixFramework.getDomainObject(oid));
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
                String oid = jArr.get(i).getAsString();
                newScopes.add(FenixFramework.getDomainObject(oid));
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
        json.addProperty("secret", obj.getSecret());
        json.addProperty("redirectUrl", obj.getRedirectUrl());
        json.addProperty("author", obj.getAuthor().getUsername());

        json.addProperty(
                "scopes",
                obj.getScopesSet().stream().sorted((a1, a2) -> a1.getName().compareTo(a2.getName()))
                        .map(ExternalApplicationScope::getName).collect(Collectors.joining(", ")));

        json.addProperty("scopesId",
                obj.getScopesSet().stream().map(ExternalApplicationScope::getExternalId).collect(Collectors.joining(", ")));

        if (obj.getLogo() != null && !Strings.isNullOrEmpty(obj.getLogo().toString())) {
            try {
                json.addProperty("logo", new String(obj.getLogo(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return json;
    }
}
