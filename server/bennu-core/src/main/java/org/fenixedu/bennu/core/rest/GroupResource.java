package org.fenixedu.bennu.core.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;

@Path("/bennu-core/groups")
public class GroupResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response grantUserMembershipGroup(@QueryParam("groupExpression") String groupExpression) {
        verifyAndGetRequestAuthor();
        return Response.ok(view(Group.parse(groupExpression))).build();
    }

    @GET
    @Path("/grant")
    @Produces(MediaType.APPLICATION_JSON)
    public Response grantUserMembershipGroup(@QueryParam("groupExpression") String groupExpression,
            @QueryParam("username") String username) {
        verifyAndGetRequestAuthor();
        User user = User.findByUsername(username);
        return Response.ok(view(Group.parse(groupExpression).grant(user))).build();
    }

    @GET
    @Path("/revoke")
    @Produces(MediaType.APPLICATION_JSON)
    public Response revokeUserFromGroup(@QueryParam("groupExpression") String groupExpression,
            @QueryParam("username") String username) {
        verifyAndGetRequestAuthor();
        User user = User.findByUsername(username);
        return Response.ok(view(Group.parse(groupExpression).revoke(user))).build();
    }
}