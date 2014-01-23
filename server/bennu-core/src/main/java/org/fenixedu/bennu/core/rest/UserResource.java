package org.fenixedu.bennu.core.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.Bennu;

@Path("/bennu-core/users")
public class UserResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        verifyAndGetRequestAuthor();
        return Response.ok(view(Bennu.getInstance().getUserSet(), "users")).build();
    }

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("oid") String externalId) {
        return Response.ok(view(readDomainObject(externalId))).build();
    }
}