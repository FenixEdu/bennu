package pt.ist.bennu.scheduler.rest;

import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
}
