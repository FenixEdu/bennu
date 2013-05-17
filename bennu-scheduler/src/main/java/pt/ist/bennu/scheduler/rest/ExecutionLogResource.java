package pt.ist.bennu.scheduler.rest;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.scheduler.CronTask;
import pt.ist.bennu.scheduler.log.ExecutionLog;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

@Path("log")
public class ExecutionLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionLogResource.class);
    private static ConcurrentHashMap<String, JsonObject> logsMap = new ConcurrentHashMap<>();
    private static Long lastModified;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String view() {
        updateLogMap();
        final JsonObject view = new JsonObject();
        final JsonArray logs = new JsonArray();
        if (!logsMap.isEmpty()) {
            for (JsonObject obj : logsMap.values()) {
                logs.add(obj);
            }
        }
        view.add("logs", logs);
        return ExecutionLog.getGson().toJson(view);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String view(@PathParam("id") String id) {
        final JsonObject jsonLog = logsMap.get(id);
        if (jsonLog != null) {
            return ExecutionLog.getGson().toJson(jsonLog);
        }
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("cat/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String logging(@PathParam("id") String id) {
        final JsonObject jsonLog = logsMap.get(id);
        if (jsonLog != null && jsonLog.has("files")) {
            try {
                return Files.toString(getFile(jsonLog, "log"), Charset.defaultCharset());
            } catch (IOException e) {
                throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
            }
        }
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("{id}/{filename}")
    public Response downloadFile(@PathParam("id") String id, @PathParam("filename") String filename) {
        final JsonObject jsonLog = logsMap.get(id);
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

    private static synchronized boolean hasChanged(File file) {
        final long modified = file.lastModified();
        if (lastModified == null) {
            lastModified = modified;
            return true;
        }
        final boolean result = modified > lastModified;
        LOG.info("File has changed ? : {}", result);
        if (result) {
            lastModified = modified;
            return true;
        }
        return false;
    }

    private synchronized static void updateLogMap() {
        final File file = new File(ExecutionLog.LOG_JSON_PATH);
        if (file.exists() && hasChanged(file)) {
            try (FileReader fileReader = new FileReader(file)) {
                JsonStreamParser parser = new JsonStreamParser(fileReader);
                while (parser.hasNext()) {
                    final JsonObject jsonLog = parser.next().getAsJsonObject();
                    final String jsonLogId = jsonLog.get("id").getAsString();
                    final JsonObject jsonLogFromMap = logsMap.get(jsonLogId);
                    if (jsonLogFromMap != null) {
                        update(jsonLog, jsonLogFromMap);
                    } else {
                        logsMap.put(jsonLogId, jsonLog);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void update(JsonObject source, JsonObject target) {
        if (source.equals(target)) {
            return;
        }
        for (final Entry<String, JsonElement> entry : source.entrySet()) {
            target.add(entry.getKey(), entry.getValue());
        }
    }

}
