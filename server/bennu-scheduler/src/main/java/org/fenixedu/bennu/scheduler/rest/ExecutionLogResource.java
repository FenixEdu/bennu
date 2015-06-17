package org.fenixedu.bennu.scheduler.rest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.log.ExecutionLog;
import org.fenixedu.bennu.scheduler.log.ExecutionLogContext;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Path("/bennu-scheduler/log")
public class ExecutionLogResource extends BennuRestResource {

    private static final ExecutionLogContext context = new ExecutionLogContext();

    public ExecutionLogContext getContext() {
        return context;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String view() {
        accessControl(Group.managers());
        final JsonObject view = new JsonObject();
        final JsonArray logs = new JsonArray();
        if (!getContext().isEmpty()) {
            for (JsonObject obj : getContext().last()) {
                logs.add(obj);
            }
        }
        view.add("logs", logs);
        return ExecutionLog.getGson().toJson(view);
    }

    @GET
    @Path("{taskname}")
    @Produces(MediaType.APPLICATION_JSON)
    public String view(@PathParam("taskname") String taskname) {
        accessControl(Group.managers());
        final JsonObject view = new JsonObject();
        final JsonArray logs = new JsonArray();

        if (!getContext().isEmpty()) {
            for (JsonObject obj : getContext().get(taskname)) {
                logs.add(obj);
            }
        }
        view.add("logs", logs);
        return ExecutionLog.getGson().toJson(view);
    }

    @GET
    @Path("{taskname}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String view(@PathParam("taskname") String taskname, @PathParam("id") String id) {
        accessControl(Group.managers());
        final JsonObject jsonLog = getContext().get(taskname, id);
        if (jsonLog != null) {
            return ExecutionLog.getGson().toJson(jsonLog);
        }
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("cat/{taskname}/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response logging(@PathParam("taskname") String taskname, @PathParam("id") String id) {
        accessControl(Group.managers());
        final JsonObject jsonLog = getContext().get(taskname, id);
        if (jsonLog != null) {
            try {
                final File file = getFile(jsonLog, "log");
                if (file.exists()) {
                    return Response.ok(Files.toString(file, Charset.defaultCharset())).build();
                }
                return Response.status(Status.NO_CONTENT).build();
            } catch (IOException e) {
                return Response.status(Status.NO_CONTENT).build();
            }
        }
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("{taskname}/{id}/{filename}")
    public Response downloadFile(@PathParam("taskname") String taskname, @PathParam("id") String id,
            @PathParam("filename") String filename) {
        accessControl(Group.managers());
        final JsonObject jsonLog = getContext().get(taskname, id);
        if (jsonLog != null && hasFile(jsonLog, filename)) {
            return Response.ok(getFile(jsonLog, filename)).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    private static boolean hasFile(final JsonObject jsonLog, String filename) {
        if (!jsonLog.has("files")) {
            return false;
        }
        for (JsonElement jsonFileEl : jsonLog.get("files").getAsJsonArray()) {
            if (jsonFileEl.getAsString().equals(filename)) {
                return true;
            }
        }

        return false;
    }

    public File getFile(final JsonObject jsonLog, String filename) {
        return new File(
                CronTask.getAbsolutePath(filename, jsonLog.get("taskName").getAsString(), jsonLog.get("id").getAsString()));
    }

}
