package org.fenixedu.bennu.core.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.rest.BennuRestResource;

@Path("/bennu-core/isAlive")
public class CheckIsAliveResource extends BennuRestResource {
    @GET
    public Response isAlive() {
        Bennu.getInstance();
        return Response.ok("ok").build();
    }
}
