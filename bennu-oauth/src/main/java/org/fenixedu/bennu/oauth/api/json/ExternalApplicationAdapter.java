/**
 * Copyright © 2015 Instituto Superior Técnico
 *
 * This file is part of Bennu OAuth.
 *
 * Bennu OAuth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu OAuth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu OAuth.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.oauth.api.json;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;

import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(ExternalApplication.class)
public class ExternalApplicationAdapter implements JsonAdapter<ExternalApplication> {

    protected boolean isManager(User user) {
        return Group.managers().isMember(user);
    }

    protected ExternalApplication create(JsonElement json) {
        ExternalApplication app = new ExternalApplication();
        app.setAuthor(Authenticate.getUser());
        return app;
    }

    @Override
    public ExternalApplication create(JsonElement json, JsonBuilder ctx) {

        JsonObject jObj = json.getAsJsonObject();
        ExternalApplication app = create(json);

        app.setName(getRequiredValue(jObj, "name"));
        app.setDescription(getRequiredValue(jObj, "description"));
        app.setRedirectUrl(getRedirectUrl(jObj));
        app.setSiteUrl(getDefaultValue(jObj, "siteUrl", ""));

        if (jObj.has("logo") && !jObj.get("logo").isJsonNull()) {
            app.setLogo(jObj.get("logo").getAsString().getBytes(Charset.forName("UTF-8")));
        }
        if (jObj.has("scopes") && !jObj.get("scopes").isJsonNull()) {

            JsonArray jArr = jObj.get("scopes").getAsJsonArray();

            for (int i = 0; i < jArr.size(); i++) {
                JsonObject scopeJsonObject = jArr.get(i).getAsJsonObject();
                String oid = scopeJsonObject.get("id").getAsString();
                ExternalApplicationScope scope = FenixFramework.getDomainObject(oid);

                if (scope.getService()) {
                    if (isManager(Authenticate.getUser())) {
                        app.addScopes(scope);
                    }
                } else {
                    app.addScopes(scope);
                }
            }
        }
        return app;
    }

    protected String getRedirectUrl(JsonObject jObj) {
        return getRequiredValue(jObj, "redirectUrl");
    }

    @Override
    public ExternalApplication update(JsonElement json, ExternalApplication app, JsonBuilder ctx) {

        JsonObject jObj = json.getAsJsonObject();
        app.setName(getRequiredValue(jObj, "name"));
        app.setDescription(getRequiredValue(jObj, "description"));
        app.setRedirectUrl(getRedirectUrl(jObj));

        app.setSiteUrl(getDefaultValue(jObj, "siteUrl", ""));

        if (jObj.has("logo") && !jObj.get("logo").isJsonNull()) {
            app.setLogo(jObj.get("logo").getAsString().getBytes(Charset.forName("UTF-8")));
        }

        if (jObj.has("scopes") && !jObj.get("scopes").isJsonNull()) {
            List<ExternalApplicationScope> newScopes = new ArrayList<ExternalApplicationScope>();

            JsonArray jArr = jObj.get("scopes").getAsJsonArray();
            for (int i = 0; i < jArr.size(); i++) {

                JsonObject scopeJsonObject = jArr.get(i).getAsJsonObject();
                String oid = scopeJsonObject.get("id").getAsString();
                ExternalApplicationScope scope = FenixFramework.getDomainObject(oid);

                if (scope.getService()) {
                    if (isManager(Authenticate.getUser())) {
                        newScopes.add(scope);
                    }
                } else {
                    newScopes.add(scope);
                }
            }
            app.setScopeList(newScopes);
        } else {
            app.setScopeList(new ArrayList<ExternalApplicationScope>());
        }
        return app;
    }

    @Override
    public JsonElement view(ExternalApplication obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getClientId());
        json.addProperty("name", obj.getName());
        json.addProperty("description", obj.getDescription());
        json.addProperty("siteUrl", obj.getSiteUrl());
        json.addProperty("active", obj.isActive());
        json.addProperty("state", obj.getState().getName());
        json.addProperty("secret", obj.getSecret());
        json.addProperty("redirectUrl", obj.getRedirectUrl());
        json.addProperty("author", obj.getAuthorApplicationName());
        json.addProperty("authorizations", obj.getApplicationUserAuthorizationSet().size());

        json.add("scopes", ctx.view(obj.getScopesSet()));

        String logoUrl =
                CoreConfiguration.getConfiguration().applicationUrl() + "/api/bennu-oauth/applications/" + obj.getExternalId()
                        + "/logo";

        json.addProperty("logoUrl", logoUrl);

        return json;
    }

    protected String getRequiredValue(JsonObject obj, String property) {
        if (obj.has(property)) {
            return obj.get(property).getAsString();
        }
        throw BennuCoreDomainException.cannotCreateEntity();
    }

    protected String getDefaultValue(JsonObject obj, String property, String defaultValue) {
        if (obj.has(property)) {
            return obj.get(property).getAsString();
        }
        return defaultValue;
    }
}
