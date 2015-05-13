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
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(ServiceApplication.class)
public class ServiceApplicationAdapter extends ExternalApplicationAdapter {

    @Override
    protected ExternalApplication create(JsonElement json) {
        ServiceApplication serviceApplication = new ServiceApplication();
        serviceApplication.setAuthorName(Authenticate.getUser().getProfile().getDisplayName());
        JsonElement ipAddresses = json.getAsJsonObject().get("ipAddresses");
        if (ipAddresses != null && ipAddresses.isJsonArray()) {
            serviceApplication.setIpAddresses(ipAddresses);
        }
        return serviceApplication;
    }

    @Override
    protected String getRedirectUrl(JsonObject jObj) {
        return getDefaultValue(jObj, "redirectUrl", "");
    }

    @Override
    public JsonElement view(ExternalApplication obj, JsonBuilder ctx) {
        JsonElement view = super.view(obj, ctx);
        JsonObject jsonObject = view.getAsJsonObject();
        JsonElement ipAddresses = ((ServiceApplication) obj).getIpAddresses();
        if (ipAddresses == null || ipAddresses.isJsonNull()) {
            ipAddresses = new JsonArray();
        }
        jsonObject.add("ipAddresses", ipAddresses);
        return jsonObject;
    }

    @Override
    public ExternalApplication update(JsonElement json, ExternalApplication app, JsonBuilder ctx) {
        ServiceApplication serviceApplication = (ServiceApplication) super.update(json, app, ctx);
        serviceApplication.setIpAddresses(json.getAsJsonObject().get("ipAddresses"));
        return serviceApplication;
    }

}