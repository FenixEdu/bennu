package org.fenixedu.bennu.scheduler.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.JsonUtils;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.scheduler.domain.TaskSchedule;
import org.fenixedu.commons.stream.StreamUtils;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/bennu-scheduler/tasks")
public class TaskResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getTasks() {
        accessControl(Group.managers());
        final JsonArray tasks = SchedulerSystem.getTasks().entrySet().stream()
                .map(taskEntry -> JsonUtils.toJson(taskJson -> {
                    taskJson.addProperty("type", taskEntry.getKey());
                    taskJson.addProperty("name", taskEntry.getValue().englishTitle());
                }))
                .collect(StreamUtils.toJsonArray());
        return JsonUtils.toJson(objContainer -> objContainer.add("tasks", tasks));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{name}")
    public Response runTaskNow(final @PathParam("name") String name) {
        accessControl(Group.managers());
        try {
            createRunOnceTaskSchedule(name);
        } catch (final Exception e) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        return ok();
    }

    @Atomic(mode = TxMode.WRITE)
    private TaskSchedule createRunOnceTaskSchedule(final String name) {
        return new TaskSchedule(name, Boolean.TRUE);
    }

}
