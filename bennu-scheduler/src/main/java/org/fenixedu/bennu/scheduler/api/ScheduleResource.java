package org.fenixedu.bennu.scheduler.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.scheduler.api.json.TaskScheduleJsonAdapter;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.scheduler.domain.TaskSchedule;
import org.joda.time.DateTime;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

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

@Path("/bennu-scheduler/schedule")
public class ScheduleResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement getSchedule() {
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
        return ok();
    }

    @POST
    @Deprecated
    @Path("dump")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response loadDump(final @FormParam("data") JsonObject json) {
        accessControl(Group.managers());
        clearAllSchedules();
        createSchedulesFromDump(json);
        return ok();
    }

    @POST
    @Path("load-dump")
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonElement loadDumpNew(final JsonObject json) {
        accessControl(Group.managers());
        clearAllSchedules();
        createSchedulesFromDump(json);
        return getSchedule();
    }

    @Atomic(mode = TxMode.WRITE)
    public void createSchedulesFromDump(final JsonObject json) {
        final TaskScheduleJsonAdapter taskScheduleJsonAdapter = new TaskScheduleJsonAdapter();
        json.get("schedule").getAsJsonArray().forEach(schedule -> taskScheduleJsonAdapter.create(schedule, null));
    }

    @Atomic(mode = TxMode.WRITE)
    public void clearAllSchedules() {
        SchedulerSystem.getInstance().getTaskScheduleSet().stream().forEach(TaskSchedule::delete);
    }

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement get(final @PathParam("oid") String taskOid) {
        accessControl(Group.managers());
        return view(readDomainObject(taskOid));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement addSchedule(final JsonElement configJson) {
        accessControl(Group.managers());
        return view(create(configJson, TaskSchedule.class));
    }

    @PUT
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement changeSchedule(final JsonElement taskScheduleJson, final @PathParam("oid") String taskScheduleOid) {
        accessControl(Group.managers());
        return view(update(taskScheduleJson, readDomainObject(taskScheduleOid)));
    }

    @DELETE
    @Path("{oid}")
    public Response delete(final @PathParam("oid") String taskOid) {
        accessControl(Group.managers());
        final TaskSchedule schedule = readDomainObject(taskOid);
        schedule.delete();
        return ok();
    }

}
