package org.fenixedu.bennu.scheduler.api;

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

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.scheduler.domain.TaskSchedule;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/bennu-scheduler/tasks")
public class TaskResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getTasks() {
        accessControl(Group.managers());
        final JsonObject objContainer = new JsonObject();
        final JsonArray tasks = new JsonArray();
        for (Entry<String, Task> taskEntry : SchedulerSystem.getTasks().entrySet()) {
            final JsonObject taskJson = new JsonObject();
            taskJson.addProperty("type", taskEntry.getKey());
            taskJson.addProperty("name", taskEntry.getValue().englishTitle());
            tasks.add(taskJson);
        }
        objContainer.add("tasks", tasks);
        return objContainer;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{name}")
    public Response runTaskNow(@PathParam("name") String name) {
        accessControl(Group.managers());
        try {
            createRunOnceTaskSchedule(name);
        } catch (Exception e) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        return ok();
    }

    @Atomic(mode = TxMode.WRITE)
    private TaskSchedule createRunOnceTaskSchedule(String name) {
        TaskSchedule taskSchedule = new TaskSchedule(name, Boolean.TRUE);
        return taskSchedule;
    }

}
