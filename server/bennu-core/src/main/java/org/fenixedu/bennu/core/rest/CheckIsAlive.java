package org.fenixedu.bennu.core.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.Bennu;

@Path("isAlive")
public class CheckIsAlive extends BennuRestResource {
    @GET
    public Response isAlive() {
        Bennu.getInstance();
        return Response.ok("ok").build();
    }
}
