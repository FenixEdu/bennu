package org.fenixedu.bennu.scheduler.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.scheduler.domain.TaskSchedule;
import org.fenixedu.bennu.scheduler.rest.json.TaskScheduleJsonAdapter;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/bennu-scheduler/schedule")
public class ScheduleResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSchedule() {
        accessControl(Group.managers());
        return view(SchedulerSystem.getInstance().getTaskScheduleSet(), "schedule");
    }

    @GET
    @Path("dump")
    public Response dump() {
        accessControl(Group.managers());
        final String filename =
                ScheduleResource.class.getSimpleName() + "_" + new DateTime().toString("MM-dd-yyyy-kk-mm-ss") + ".json";

        return Response.ok(view(SchedulerSystem.getInstance().getTaskScheduleSet(), "schedule"))
                .header("Content-Disposition", "attachment; filename=" + filename).build();
    }

    @DELETE
    public Response delete() {
        accessControl(Group.managers());
        clearAllSchedules();
        return Response.ok().build();
    }

    @POST
    @Deprecated
    @Path("dump")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response loadDump(@FormParam("data") String json) {
        accessControl(Group.managers());
        clearAllSchedules();
        createSchedulesFromDump(json);
        return Response.ok().build();
    }

    @POST
    @Path("load-dump")
    @Consumes(MediaType.APPLICATION_JSON)
    public String loadDumpNew(String json) {
        accessControl(Group.managers());
        clearAllSchedules();
        createSchedulesFromDump(json);
        return getSchedule();
    }

    @Atomic(mode = TxMode.WRITE)
    public void createSchedulesFromDump(String json) {
        TaskScheduleJsonAdapter taskScheduleJsonAdapter = new TaskScheduleJsonAdapter();
        final JsonObject dump = new JsonParser().parse(json).getAsJsonObject();
        for (JsonElement schedule : dump.get("schedule").getAsJsonArray()) {
            taskScheduleJsonAdapter.create(schedule, null);
        }
    }

    @Atomic(mode = TxMode.WRITE)
    public void clearAllSchedules() {
        for (TaskSchedule schedule : SchedulerSystem.getInstance().getTaskScheduleSet()) {
            schedule.delete();
        }
    }

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@PathParam("oid") String taskOid) {
        accessControl(Group.managers());
        return view(readDomainObject(taskOid));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addSchedule(String configJson) {
        accessControl(Group.managers());
        return view(create(configJson, TaskSchedule.class));
    }

    @PUT
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String changeSchedule(String taskScheduleJson, @PathParam("oid") String taskScheduleOid) {
        accessControl(Group.managers());
        return view(update(taskScheduleJson, readDomainObject(taskScheduleOid)));
    }

    @DELETE
    @Path("{oid}")
    public Response delete(@PathParam("oid") String taskOid) {
        accessControl(Group.managers());
        TaskSchedule schedule = readDomainObject(taskOid);
        schedule.delete();
        return Response.status(Status.OK).build();
    }

}
