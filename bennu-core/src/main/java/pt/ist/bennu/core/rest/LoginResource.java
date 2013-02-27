package pt.ist.bennu.core.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.AuthorizationException;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource extends BennuRestResource {

    @GET
    public Response getLoginStatus() {
        User author = verifyAndGetRequestAuthor();
        return Response.ok().entity(view(author)).build();
    }

    @POST
    public Response login(@Context UriInfo uriInfo, @FormParam("username") String username, @FormParam("password") String password) {
        if (!isCasEnabled()) {
            User authenticatedUser = login(username, password, false);
            if (authenticatedUser == null) {
                throw AuthorizationException.unauthorized();
            }
            return Response.seeOther(uriInfo.getBaseUriBuilder().replacePath("").build()).build();
        }
        throw AuthorizationException.unauthorized();
    }
}
