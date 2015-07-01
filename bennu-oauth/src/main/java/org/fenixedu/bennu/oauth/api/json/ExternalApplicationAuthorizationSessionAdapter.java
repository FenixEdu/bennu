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
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;
import org.fenixedu.bennu.oauth.util.OAuthUtils;

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
        json.addProperty(OAuthUtils.REFRESH_TOKEN, obj.getRefreshToken());
        json.addProperty(OAuthUtils.ACCESS_TOKEN, obj.getAccessToken());
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
