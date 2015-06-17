package org.fenixedu.bennu.scheduler.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.scheduler.custom.ClassBean;
import org.fenixedu.bennu.scheduler.log.CustomExecutionLogContext;
import org.fenixedu.bennu.scheduler.log.ExecutionLog;
import org.fenixedu.bennu.scheduler.log.ExecutionLogContext;

import com.google.gson.JsonObject;

@Path("/bennu-scheduler/custom")
public class CustomTaskResource extends ExecutionLogResource {

    private static final CustomExecutionLogContext context = new CustomExecutionLogContext();

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomTask(String jsonCode) {
        accessControl(Group.managers());
        getClassBean(jsonCode).run();
        return Response.ok().build();
    }

    private ClassBean getClassBean(String jsonCode) {
        JsonObject parse = parse(jsonCode).getAsJsonObject();
        return new ClassBean(parse.get("name").getAsString(), parse.get("code").getAsString());
    }

    @PUT
    @Path("compile")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response compileCustomTask(String jsonCode) {
        accessControl(Group.managers());
        return Response.ok(ExecutionLog.getGson().toJson(getClassBean(jsonCode).compile())).build();
    }

    @Override
    public ExecutionLogContext getContext() {
        return context;
    }
}
