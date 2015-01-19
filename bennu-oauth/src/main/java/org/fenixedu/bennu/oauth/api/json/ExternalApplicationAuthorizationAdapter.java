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

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ExternalApplicationScope;

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

        String logoUrl =
                CoreConfiguration.getConfiguration().applicationUrl() + "/api/bennu-oauth/applications/"
                        + application.getExternalId() + "/logo";
        json.addProperty("applicationLogoUrl", logoUrl);
        return json;
    }
}
