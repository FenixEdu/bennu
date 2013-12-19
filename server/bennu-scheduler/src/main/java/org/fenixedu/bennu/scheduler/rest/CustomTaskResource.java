package org.fenixedu.bennu.scheduler.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.scheduler.custom.ClassBean;
import org.fenixedu.bennu.scheduler.log.CustomExecutionLogContext;
import org.fenixedu.bennu.scheduler.log.ExecutionLog;
import org.fenixedu.bennu.scheduler.log.ExecutionLogContext;

@Path("custom")
public class CustomTaskResource extends ExecutionLogResource {

    private static final CustomExecutionLogContext context = new CustomExecutionLogContext();

    @POST
    public Response addCustomTask(@FormParam("name") String name, @FormParam("code") String code) {
        accessControl("#managers");
        final ClassBean classBean = new ClassBean(name, code);
        classBean.run();
        return Response.ok().build();
    }

    @POST
    @Path("compile")
    public Response compileCustomTask(@FormParam("name") String name, @FormParam("code") String code) {
        accessControl("#managers");
        return Response.ok(ExecutionLog.getGson().toJson(new ClassBean(name, code).compile())).build();
    }

    @Override
    public ExecutionLogContext getContext() {
        return context;
    }
}
