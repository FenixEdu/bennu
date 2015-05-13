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

import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ExternalApplicationForManagersAdapter extends ExternalApplicationAdapter {

    @Override
    public ExternalApplication update(JsonElement json, ExternalApplication obj, JsonBuilder ctx) {

        ExternalApplication updated = super.update(json, obj, ctx);
        JsonObject jObj = json.getAsJsonObject();
        updated.setAuthorName(jObj.get("author") == null ? "" : jObj.get("author").getAsString());
        return updated;

    }

}
