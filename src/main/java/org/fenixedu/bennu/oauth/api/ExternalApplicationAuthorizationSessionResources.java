package org.fenixedu.bennu.oauth.api;

import static pt.ist.fenixframework.FenixFramework.atomic;

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
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;

@Path("/bennu-oauth/sessions/")
public class ExternalApplicationAuthorizationSessionResources extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{session}")
    public String authorizations(@PathParam("session") ApplicationUserAuthorization authorization) {
        User user = verifyAndGetRequestAuthor();
        if (authorization.getUser() == user || isManager(user)) {
            return view(authorization.getSessionSet());
        }
        return null;
    }

    @DELETE
    @Path("/{session}")
    public Response delete(@PathParam("session") ApplicationUserSession session) {
        User user = verifyAndGetRequestAuthor();
        if (session.getApplicationUserAuthorization().getUser() == user || isManager(user)) {
            atomic(() -> {
                session.delete();
            });
            return Response.ok().build();
        }
        return null;
    }

    private boolean isManager(User user) {
        return Group.parse("#managers").isMember(user);
    }
}
