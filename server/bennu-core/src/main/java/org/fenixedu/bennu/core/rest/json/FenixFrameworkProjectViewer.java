/*
 * FenixFrameworkProjectViewer.java
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

import java.io.IOException;
import java.util.jar.JarFile;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.commons.json.JsonBuilder;
import org.fenixedu.commons.json.JsonViewer;

import pt.ist.fenixframework.core.Project;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Project.class)
public class FenixFrameworkProjectViewer implements JsonViewer<Project> {
    @Override
    public JsonElement view(Project project, JsonBuilder ctx) {
        String projectResource = "/" + project.getName() + "/project.properties";
        String url = FenixFrameworkProjectViewer.class.getResource(projectResource).toExternalForm();
        if (url.startsWith("jar")) {
            String jar = url.substring("jar:file:".length(), url.length() - projectResource.length() - 1);
            try (JarFile jarFile = new JarFile(jar)) {
                return ctx.view(jarFile);
            } catch (IOException e) {
                throw new Error(e);
            }
        }
        // webapp case
        JsonObject json = new JsonObject();
        json.addProperty("name", project.getName());
        return json;
    }
}