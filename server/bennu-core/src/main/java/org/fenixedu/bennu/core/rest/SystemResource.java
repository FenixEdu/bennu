/*
 * SystemResource.java
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
package org.fenixedu.bennu.core.rest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.JarFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.json.adapters.KeyValuePropertiesViewer;
import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;

import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("system")
public class SystemResource extends BennuRestResource {
    @GET
    @Path("info")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response info(@Context HttpServletRequest request) {
        accessControl("#managers");
        JsonObject json = new JsonObject();

        // fenix-framework projects
        json.add("projects", getBuilder().view(FenixFramework.getProject().getProjects()));

        // libraries
        try {
            List<JarFile> libraries = new ArrayList<>();
            String libPath = request.getServletContext().getRealPath("/WEB-INF/lib");
            // in jetty this is not possible.
            if (libPath != null) {
                for (File file : new File(libPath).listFiles()) {
                    if (file.getName().endsWith(".jar")) {
                        libraries.add(new JarFile(file));
                    }
                }
            }
            json.add("libraries", getBuilder().view(libraries));
        } catch (IOException e) {
            throw new Error(e);
        }

        // system properties
        json.add("sys", getBuilder().view(System.getProperties(), Properties.class, KeyValuePropertiesViewer.class));

        // environment properties
        JsonArray env = new JsonArray();
        for (Entry<String, String> entry : System.getenv().entrySet()) {
            JsonObject object = new JsonObject();
            object.addProperty("key", entry.getKey());
            object.addProperty("value", entry.getValue());
            env.add(object);
        }
        json.add("env", env);

        JsonArray headers = new JsonArray();
        for (final Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
            String header = e.nextElement();
            JsonObject object = new JsonObject();
            object.addProperty("key", header);
            object.addProperty("value", request.getHeader(header));
            headers.add(object);
        }
        json.add("http", headers);

        json.add(
                "conf",
                getBuilder().view(ConfigurationInvocationHandler.rawProperties(), Properties.class,
                        KeyValuePropertiesViewer.class));

        return Response.ok(toJson(json)).build();
    }

    @GET
    @Path("error")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response error() {
        throw new Error("oi?");
    }

}
