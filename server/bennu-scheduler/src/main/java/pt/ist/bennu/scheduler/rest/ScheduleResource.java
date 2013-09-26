package pt.ist.bennu.scheduler.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.scheduler.domain.SchedulerSystem;
import pt.ist.bennu.scheduler.domain.TaskSchedule;

@Path("/schedule")
public class ScheduleResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSchedule() {
        accessControl("#managers");
        return view(SchedulerSystem.getInstance().getTaskScheduleSet(), "schedule");
    }

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@PathParam("oid") String taskOid) {
        accessControl("#managers");
        return view(readDomainObject(taskOid));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addSchedule(String configJson) {
        accessControl("#managers");
        return view(create(configJson, TaskSchedule.class));
    }

    @PUT
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String changeSchedule(String taskScheduleJson, @PathParam("oid") String taskScheduleOid) {
        accessControl("#managers");
        return view(update(taskScheduleJson, readDomainObject(taskScheduleOid)));
    }

    @DELETE
    @Path("{oid}")
    public Response delete(@PathParam("oid") String taskOid) {
        accessControl("#managers");
        clear((TaskSchedule) readDomainObject(taskOid));
        return Response.status(Status.OK).build();
    }

    private void clear(TaskSchedule task) {
        task.unscheduleAndDelete();
    }

}
