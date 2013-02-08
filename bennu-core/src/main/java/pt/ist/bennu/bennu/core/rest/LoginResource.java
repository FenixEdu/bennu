package pt.ist.bennu.bennu.core.rest;

import java.net.URISyntaxException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import pt.ist.bennu.bennu.core.rest.mapper.BennuRestError;
import pt.ist.bennu.bennu.core.rest.mapper.RestException;
import pt.ist.bennu.core.domain.User;

@Path("/login")
public class LoginResource extends BennuRestResource {

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getLoginStatus() {
        User author = verifyAndGetRequestAuthor();
        return Response.ok().entity(view(author)).build();
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public Response login(@Context UriInfo uriInfo, @FormParam("username") String username, @FormParam("password") String password)
            throws URISyntaxException {
        if (!isCasEnabled()) {
            User authenticatedUser = login(username, password, false);
            if (authenticatedUser == null) {
                throw new RestException(BennuRestError.UNAUTHORIZED);
            } else {
                return Response.seeOther(uriInfo.getBaseUriBuilder().replacePath("").build()).build();
            }
        } else {
            throw new RestException(BennuRestError.UNAUTHORIZED);
        }

    }

}
