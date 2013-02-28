package pt.ist.bennu.core.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.AuthorizationException;

@Path("/login")
public class LoginResource extends BennuRestResource {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {
        if (!isCasEnabled()) {
            User user = login(username, password, false);
            if (user == null) {
                throw AuthorizationException.authenticationFailed();
            }
            return Response.ok(view(user)).build();
        }
        throw AuthorizationException.authenticationFailed();
    }
}
