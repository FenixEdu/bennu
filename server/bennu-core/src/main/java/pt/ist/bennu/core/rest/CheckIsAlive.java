package pt.ist.bennu.core.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import pt.ist.bennu.core.domain.Bennu;

import com.google.common.base.Strings;

@Path("isAlive")
public class CheckIsAlive extends BennuRestResource {

    @GET
    @Path("{timeout}")
    public Response isAlive(@PathParam("timeout") String timeout) {
        Bennu.getInstance();
        if (!Strings.isNullOrEmpty(timeout)) {
            long secs = Long.parseLong(timeout);
            try {
                Thread.sleep(secs * 1000);
            } catch (InterruptedException e) {
            }
        }
        return Response.ok("ok").build();
    }
}
