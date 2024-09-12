package org.fenixedu.bennu.core.api;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.security.Authenticate;

@Path("/bennu-core/users")
public class UserResource extends BennuRestResource {

    @POST
    @Path("find")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement findUser(@QueryParam("query") String query,
            @QueryParam("includeInactive") @DefaultValue("false") Boolean includeInactive,
            @QueryParam("maxHits") @DefaultValue("20") Integer maxHits) {
        if (query == null || Authenticate.getUser() == null) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        Stream<User> results =
                Stream.concat(Stream.of(User.findByUsername(query)),
                        UserProfile.searchByName(query, Integer.MAX_VALUE).map(UserProfile::getUser)).filter(Objects::nonNull)
                        .distinct();
        if (!includeInactive) {
            results = results.filter(u -> !u.isLoginExpired());
        }
        results = results.limit(maxHits);
        return view(results, "users");
    }

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement getUser(@PathParam("oid") String externalId) {
        accessControl(Group.managers());
        return view(readDomainObject(externalId));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement create(JsonElement json) {
        accessControl(Group.managers());
        return view(create(json, User.class));
    }

    @PUT
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement update(JsonElement json, @PathParam("username") String username) {
        accessControl(Group.managers());
        User user = User.findByUsername(username);
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        } else {
            return view(update(json, user));
        }
    }

    @GET
    @Path("/username/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement byUsername(@PathParam("username") String username) {
        accessControl(Group.managers());
        User user = User.findByUsername(username);
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return view(user);
    }

    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement userData(@Context HttpServletRequest request) {
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

        return json;
    }

}