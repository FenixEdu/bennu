package org.fenixedu.bennu.oauth.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(ApplicationUserAuthorization.class)
public class ExternalApplicationAuthorizationAdapter implements JsonAdapter<ApplicationUserAuthorization> {

    @Override
    public ApplicationUserAuthorization create(JsonElement json, JsonBuilder ctx) {
        return null;
    }

    @Override
    public ApplicationUserAuthorization update(JsonElement json, ApplicationUserAuthorization obj, JsonBuilder ctx) {
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
        json.addProperty("applicationAuthor", application.getAuthorApplicationName());

        JsonArray scopeArray = new JsonArray();
        for (ExternalApplicationScope externalApplicationScope : application.getScopesSet()) {
            scopeArray.add(ctx.view(externalApplicationScope));
        }
        json.add("applicationScopes", scopeArray);

        if (application.getLogo() != null && !Strings.isNullOrEmpty(application.getLogo().toString())) {
            String logoUrl =
                    CoreConfiguration.getConfiguration().applicationUrl() + "/api/bennu-oauth/applications/"
                            + application.getExternalId() + "/logo";
            json.addProperty("applicationLogoUrl", logoUrl);
        }
        return json;
    }
}
