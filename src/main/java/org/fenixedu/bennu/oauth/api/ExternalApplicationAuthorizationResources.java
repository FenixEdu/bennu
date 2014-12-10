package org.fenixedu.bennu.oauth.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.oauth.domain.ApplicationUserAuthorization;

@Path("/bennu-oauth/authorizations")
public class ExternalApplicationAuthorizationResources extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String myAuthorizations() {
        return view(verifyAndGetRequestAuthor().getApplicationUserAuthorizationSet());
    }

    @DELETE
    @Path("/{authorization}")
    public Response delete(@PathParam("authorization") ApplicationUserAuthorization authorization) {
        User user = verifyAndGetRequestAuthor();
        if (authorization.getUser() == user || isManager(user)) {
            authorization.delete();
            return Response.ok().build();
        }
        return null;
    }

    private boolean isManager(User user) {
        return Group.parse("#managers").isMember(user);
    }

}
