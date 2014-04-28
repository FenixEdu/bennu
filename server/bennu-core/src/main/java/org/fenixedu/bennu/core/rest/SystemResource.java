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
import java.lang.Thread.State;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
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
import pt.ist.fenixframework.core.SharedIdentityMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/bennu-core/system")
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
                File[] files = new File(libPath).listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith(".jar")) {
                            libraries.add(new JarFile(file));
                        }
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

        JsonObject metrics = new JsonObject();

        metrics.addProperty("cacheSize", SharedIdentityMap.getCache().size());
        metrics.addProperty("project", FenixFramework.getProject().getName());
        metrics.addProperty("version", FenixFramework.getProject().getVersion());

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        metrics.addProperty("jvm.memory.total.used", memoryBean.getHeapMemoryUsage().getUsed()
                + memoryBean.getNonHeapMemoryUsage().getUsed());
        metrics.addProperty("jvm.memory.total.max", memoryBean.getHeapMemoryUsage().getMax()
                + memoryBean.getNonHeapMemoryUsage().getMax());

        metrics.addProperty("jvm.memory.heap.used", memoryBean.getHeapMemoryUsage().getUsed());
        metrics.addProperty("jvm.memory.heap.max", memoryBean.getHeapMemoryUsage().getMax());

        metrics.addProperty("jvm.memory.non-heap.used", memoryBean.getNonHeapMemoryUsage().getUsed());
        metrics.addProperty("jvm.memory.non-heap.max", memoryBean.getNonHeapMemoryUsage().getMax());

        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        metrics.addProperty("jvm.threads.count", threads.getThreadCount());

        ClassLoadingMXBean classLoading = ManagementFactory.getClassLoadingMXBean();
        metrics.addProperty("jvm.classloading.loaded.total", classLoading.getTotalLoadedClassCount());
        metrics.addProperty("jvm.classloading.loaded", classLoading.getLoadedClassCount());
        metrics.addProperty("jvm.classloading.unloaded", classLoading.getUnloadedClassCount());

        json.add("metrics", metrics);

        return Response.ok(toJson(json)).build();
    }

    @GET
    @Path("thread-dump")
    @Produces(MediaType.APPLICATION_JSON)
    public Response threadDump() {
        accessControl("#managers");
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();

        int total = 0;

        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();

        for (State state : State.values()) {
            json.addProperty(state.name(), 0);
        }

        for (Entry<Thread, StackTraceElement[]> entry : threads.entrySet()) {
            JsonObject thread = new JsonObject();
            String state = entry.getKey().getState().name();
            thread.addProperty("name", entry.getKey().toString());
            thread.addProperty("state", state);
            StringBuilder builder = new StringBuilder();
            for (StackTraceElement element : entry.getValue()) {
                builder.append(element);
                builder.append("\n");
            }
            thread.addProperty("id", entry.getKey().getId());
            thread.addProperty("stacktrace", builder.toString());
            array.add(thread);

            json.addProperty(state, json.get(state).getAsInt() + 1);
            total++;
        }

        json.addProperty("totalThreads", total);
        json.add("threads", array);
        return Response.ok(toJson(json)).build();
    }

    @GET
    @Path("healthcheck")
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthChecks() {
        accessControl("#managers");
        JsonArray json = new JsonArray();

        for (Healthcheck check : healthchecks) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", check.getName());
            obj.add("result", check.execute().toJson());
            json.add(obj);
        }

        return Response.ok(toJson(json)).build();
    }

    private static final Collection<Healthcheck> healthchecks = new ConcurrentLinkedQueue<>();

    public static void registerHealthcheck(Healthcheck healthcheck) {
        healthchecks.add(Objects.requireNonNull(healthcheck));
    }

}
