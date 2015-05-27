package org.fenixedu.bennu.core.rest;

import static java.lang.management.ManagementFactory.getPlatformMBeanServer;

import java.util.Objects;
import java.util.stream.Stream;

import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.groups.Group;

import com.google.gson.JsonObject;

@Path("/bennu-core/users")
public class UserResource extends BennuRestResource {

    @POST
    @Path("find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUser(@QueryParam("query") String query, @QueryParam("maxHits") @DefaultValue("20") Integer maxHits) {
        if (query == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        Stream<User> results =
                Stream.concat(Stream.of(User.findByUsername(query)),
                        UserProfile.searchByName(query, Integer.MAX_VALUE).map(UserProfile::getUser)).filter(Objects::nonNull)
                        .distinct().limit(maxHits);
        return Response.ok(view(results, "users")).build();
    }

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("oid") String externalId) {
        return Response.ok(view(readDomainObject(externalId))).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(String json) {
        accessControl(Group.managers());
        return Response.ok(view(create(json, User.class))).build();
    }

    @PUT
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(String json, @PathParam("username") String username) {
        accessControl(Group.managers());
        User user = User.findByUsername(username);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(view(update(json, user))).build();
        }
    }

    @GET
    @Path("/username/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response byUsername(@PathParam("username") String username) {
        accessControl(Group.managers());
        User user = User.findByUsername(username);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(view(user)).build();
        }
    }

    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response userData(@Context HttpServletRequest request) {
        accessControl(Group.managers());
        JsonObject json = new JsonObject();

        json.addProperty("userCount", Bennu.getInstance().getUserSet().size());
        try {
            Object sessions =
                    getPlatformMBeanServer().getAttribute(
                            new ObjectName("Catalina:type=Manager,context="
                                    + (request.getContextPath().isEmpty() ? "/" : request.getContextPath()) + ",host=localhost"),
                            "activeSessions");
            json.addProperty("activeSessions", sessions.toString());
        } catch (Exception e) {
            // Ignore, not on Tomcat
        }

        return Response.ok(json.toString()).build();
    }

}