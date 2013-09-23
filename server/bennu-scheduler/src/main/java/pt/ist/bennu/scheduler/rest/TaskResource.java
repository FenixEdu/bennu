package pt.ist.bennu.scheduler.rest;

import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.scheduler.annotation.Task;
import pt.ist.bennu.scheduler.domain.SchedulerSystem;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("tasks")
public class TaskResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTasks() {
        accessControl("#managers");
        final JsonObject objContainer = new JsonObject();
        final JsonArray tasks = new JsonArray();
        for (Entry<String, Task> taskEntry : SchedulerSystem.getTasks().entrySet()) {
            final JsonObject taskJson = new JsonObject();
            taskJson.addProperty("type", taskEntry.getKey());
            taskJson.addProperty("name", taskEntry.getValue().englishTitle());
            tasks.add(taskJson);
        }
        objContainer.add("tasks", tasks);
        return toJson(objContainer);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{name}")
    public Response runTaskNow(@PathParam("name") String name) {
        accessControl("#managers");
        try {
            SchedulerSystem.runNow(name);
        } catch (Exception e) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        return Response.status(Status.OK).build();
    }
}
