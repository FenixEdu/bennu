package org.fenixedu.bennu.core.rest;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/bennu-core/users")
public class UserResource extends BennuRestResource {

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
    public Response findUser(@QueryParam("query") String query, @QueryParam("maxHits") Integer maxHits) {
        Set<User> matches = new HashSet<>();
        User user = User.findByUsername(query);
        if (user != null) {
            matches.add(user);
        }
        matches.addAll(UserProfile.searchByName(query, maxHits - matches.size()).map(UserProfile::getUser)
                .filter(Objects::nonNull).collect(Collectors.toSet()));
        return Response.ok(view(matches, "users")).build();
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
}