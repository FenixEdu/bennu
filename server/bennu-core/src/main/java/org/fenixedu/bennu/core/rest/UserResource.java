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
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.adapters.GroupJsonAdapter.FullGroupJsonAdapter;
import org.fenixedu.bennu.core.security.Authenticate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/bennu-core/users")
public class UserResource extends BennuRestResource {

    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public Response getAllUsers() {
        verifyAndGetRequestAuthor();
        JsonObject users = new JsonObject();
        users.add("users", new JsonArray());
        return Response.ok(users.toString()).build();
    }

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
                        .limit(maxHits);
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
        accessControl("#managers");
        return Response.ok(view(create(json, User.class))).build();
    }

    @PUT
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(String json, @PathParam("username") String username) {
        accessControl("#managers");
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
        accessControl("#managers");
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
        accessControl("#managers");
        JsonObject json = new JsonObject();

        json.addProperty("userCount", Bennu.getInstance().getUserSet().size());
        try {
            Object sessions =
                    getPlatformMBeanServer().getAttribute(
                            new ObjectName("Tomcat:type=Manager,context="
                                    + (request.getContextPath().isEmpty() ? "/" : request.getContextPath()) + ",host=localhost"),
                            "activeSessions");
            json.addProperty("activeSessions", sessions.toString());
        } catch (Exception e) {
            // Ignore, not on Tomcat
        }
        json.add("managers", getBuilder().view(DynamicGroup.get("managers").underlyingGroup(), FullGroupJsonAdapter.class));

        return Response.ok(json.toString()).build();
    }

    @POST
    @Path("/managers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeManagers(String expression) {
        accessControl("#managers");
        logger.warn("User '{}' changed the managers group to {}", Authenticate.getUser().getUsername(), expression);
        Group group = Group.parse(expression);
        return Response.ok(getBuilder().view(DynamicGroup.get("managers").changeGroup(group).underlyingGroup())).build();
    }
}