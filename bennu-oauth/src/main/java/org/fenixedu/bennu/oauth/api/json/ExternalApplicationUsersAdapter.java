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
            jobj.addProperty("name", applicationUserAuthorization.getUser().getDisplayName());
            jobj.addProperty("authorizations", applicationUserAuthorization.getSessionSet().size());
            jarr.add(jobj);
        }
        return jarr;
    }

}
