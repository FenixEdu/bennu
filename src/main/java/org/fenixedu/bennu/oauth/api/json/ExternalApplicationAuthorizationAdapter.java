package org.fenixedu.bennu.oauth.api.json;

import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(ApplicationUserAuthorization.class)
public class ExternalApplicationAuthorizationAdapter implements JsonAdapter<ApplicationUserAuthorization> {

    @Override
    public ApplicationUserAuthorization create(JsonElement json, JsonBuilder ctx) {
        /*
        ExternalApplicationScope scope = new ExternalApplicationScope();
        JsonObject asJsonObject = json.getAsJsonObject();
        scope.setScopeKey(asJsonObject.get("scopeKey").getAsString());
        scope.setName(asJsonObject.get("name").getAsString());
        scope.setDescription(asJsonObject.get("description").getAsString());
        */
        return null;
    }

    @Override
    public ApplicationUserAuthorization update(JsonElement json, ApplicationUserAuthorization obj, JsonBuilder ctx) {
        /*JsonObject asJsonObject = json.getAsJsonObject();
        obj.setScopeKey(asJsonObject.get("scopeKey").getAsString());
        obj.setName(asJsonObject.get("name").getAsString());
        obj.setDescription(asJsonObject.get("description").getAsString());
        */
        return null;
    }

    @Override
    public JsonElement view(ApplicationUserAuthorization obj, JsonBuilder ctx) {

        JsonObject json = new JsonObject();
        ExternalApplication application = obj.getApplication();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("applicationName", application.getName());
        json.addProperty("applicationDescription", application.getDescription());
        json.addProperty("applicationSiteUrl", application.getSiteUrl());
        json.addProperty("applicationAuthor", application.getAuthor().getUsername());

        json.addProperty("applicationScopes",
                application.getScopesSet().stream().map(ExternalApplicationScope::getName).collect(Collectors.joining(", ")));

        if (application.getLogo() != null && !Strings.isNullOrEmpty(application.getLogo().toString())) {
            try {
                json.addProperty("applicationLogo", new String(application.getLogo(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return json;
    }
}
