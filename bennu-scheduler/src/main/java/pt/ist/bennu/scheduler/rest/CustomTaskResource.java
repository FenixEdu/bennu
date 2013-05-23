package pt.ist.bennu.scheduler.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import pt.ist.bennu.scheduler.custom.ClassBean;
import pt.ist.bennu.scheduler.log.CustomExecutionLogContext;
import pt.ist.bennu.scheduler.log.ExecutionLogContext;

@Path("custom")
public class CustomTaskResource extends ExecutionLogResource {

    private static final ExecutionLogContext context = new CustomExecutionLogContext();

    @POST
    public Response addCustomTask(@FormParam("name") String name, @FormParam("code") String code) {
        final ClassBean classBean = new ClassBean(name, code);
        classBean.run();
        return Response.ok().build();
    }

    @POST
    @Path("compile")
    public Response compileCustomTask(@FormParam("name") String name, @FormParam("code") String code) {
        return Response.ok(new ClassBean(name, code).compile()).build();
    }

    @Override
    public ExecutionLogContext getContext() {
        return context;
    }
}
