package org.fenixedu.bennu.scheduler.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;

@Path("/bennu-scheduler/log")
public class ExecutionLogResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String latest() {
        accessControl(Group.managers());
        return view(SchedulerSystem.getLogRepository().latest());
    }

    @GET
    @Path("{taskName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String executionsFor(@PathParam("taskName") String taskName, @QueryParam("count") @DefaultValue("20") int max) {
        accessControl(Group.managers());
        return view(SchedulerSystem.getLogRepository().executionsFor(taskName, max));
    }

    @GET
    @Path("{taskName}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response executionLog(@PathParam("taskName") String taskName, @PathParam("id") String id) {
        accessControl(Group.managers());
        return SchedulerSystem.getLogRepository().getLog(taskName, id).map(log -> Response.ok(view(log)).build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @GET
    @Path("cat/{taskName}/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response taskLog(@PathParam("taskName") String taskName, @PathParam("id") String id) {
        accessControl(Group.managers());
        return SchedulerSystem.getLogRepository().getTaskLog(taskName, id).map(log -> Response.ok(log).build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @GET
    @Path("{taskName}/{id}/{fileName}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("taskName") String taskName, @PathParam("id") String id,
            @PathParam("fileName") String fileName) {
        accessControl(Group.managers());
        return SchedulerSystem.getLogRepository().getFile(taskName, id, fileName).map(file -> Response.ok(file).build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

}