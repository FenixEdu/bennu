/*
 * PropertiesViewer.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-core.
 *
 * bennu-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.rest.json;

import java.util.Map.Entry;
import java.util.Properties;

import org.fenixedu.commons.json.JsonBuilder;
import org.fenixedu.commons.json.JsonViewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class KeyValuePropertiesViewer implements JsonViewer<Properties> {
    @Override
    public JsonElement view(Properties properties, JsonBuilder ctx) {
        JsonArray json = new JsonArray();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            JsonObject object = new JsonObject();
            object.addProperty("key", (String) entry.getKey());
            object.addProperty("value", (String) entry.getValue());
            json.add(object);
        }
        return json;
    }
}
