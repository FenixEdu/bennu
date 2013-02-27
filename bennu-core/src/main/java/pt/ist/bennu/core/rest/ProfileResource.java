package pt.ist.bennu.core.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.ist.bennu.core.domain.User;

@Path("/profile")
public class ProfileResource extends BennuRestResource {

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getProfile() {
        User author = verifyAndGetRequestAuthor();
        return Response.ok().entity(view(author)).build();
    }
}