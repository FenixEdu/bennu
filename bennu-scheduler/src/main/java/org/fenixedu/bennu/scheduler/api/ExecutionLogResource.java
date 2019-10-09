package org.fenixedu.bennu.scheduler.api;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.scheduler.api.json.SimpleExecutionLogJsonAdapter;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;

import com.google.gson.JsonElement;

@Path("/bennu-scheduler/log")
public class ExecutionLogResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement latest() {
        accessControl(Group.managers());
        return view(SchedulerSystem.getLogRepository().latest(), SimpleExecutionLogJsonAdapter.class);
    }

    @GET
    @Path("{taskName}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement executionsFor(@PathParam("taskName") String taskName, @QueryParam("count") @DefaultValue("20") int max,
            @QueryParam("start") String start) {
        accessControl(Group.managers());
        return view(SchedulerSystem.getLogRepository().executionsFor(taskName, Optional.ofNullable(start), max),
                SimpleExecutionLogJsonAdapter.class);
    }

    @GET
    @Path("{taskName}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement executionLog(@PathParam("taskName") String taskName, @PathParam("id") String id) {
        accessControl(Group.managers());
        return SchedulerSystem.getLogRepository().getLog(taskName, id).map(this::view)
                .orElseThrow(() -> new WebApplicationException(Status.NOT_FOUND));
    }

    @GET
    @Path("cat/{taskName}/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String taskLog(@PathParam("taskName") String taskName, @PathParam("id") String id) {
        accessControl(Group.managers());
        return SchedulerSystem.getLogRepository().getTaskLog(taskName, id).orElse("");
    }

    @GET
    @Path("{taskName}/{id}/{fileName}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] downloadFile(@PathParam("taskName") String taskName, @PathParam("id") String id,
            @PathParam("fileName") String fileName) {
        accessControl(Group.managers());
        return SchedulerSystem.getLogRepository().getFile(taskName, id, fileName)
                .orElseThrow(() -> new WebApplicationException(Status.NOT_FOUND));
    }

    @GET
    @Path("kill/{taskName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response killTask(@PathParam("taskName") String taskName) {
        accessControl(Group.managers());
        Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> thread.getName().contains(taskName))
                .forEach(Thread::stop);
        return ok();
    }

    @GET
    @Path("thread/{taskName}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject threadTask(@PathParam("taskName") String taskName) {
        accessControl(Group.managers());
        JsonObject json = new JsonObject();
        for (Map.Entry<Thread, StackTraceElement[]> entry :  Thread.getAllStackTraces().entrySet()) {
            if (isCustomTaskThread(taskName, entry.getKey()) || isCronTaskThread(taskName, entry.getKey(), entry.getValue())) {
                json.addProperty("id", entry.getKey().getId());
                json.addProperty("name", entry.getKey().toString());
                json.addProperty("stacktrace",
                        Arrays.stream(entry.getValue()).map(StackTraceElement::toString).collect(Collectors.joining("\n")));
                return json;
            }
        }
        return json;
    }

    private boolean isCustomTaskThread(String taskName, Thread thread) {
        return thread.getName().contains(taskName);
    }

    private boolean isCronTaskThread(String taskName, Thread thread, StackTraceElement... stackTraceElements) {
        return thread.getName().contains(SchedulerSystem.SCHEDULER_CONSUMER) &&
                Arrays.stream(stackTraceElements).anyMatch(stackTraceElement -> stackTraceElement.toString().contains(taskName));
    }
}