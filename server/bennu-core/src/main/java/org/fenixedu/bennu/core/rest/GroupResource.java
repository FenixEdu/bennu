package org.fenixedu.bennu.core.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    @GET
    @Path("/dynamic")
    @Produces(MediaType.APPLICATION_JSON)
    public String listDynamicGroups() {
        accessControl("#managers");
        return view(BennuGroupIndex.allDynamicGroups(), DynamicGroupJsonAdapter.class);
    }

    @POST
    @Path("/dynamic/grant")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserToDynamicGroup(String json) {
        accessControl("#managers");
        JsonObject obj = parse(json).getAsJsonObject();
        DynamicGroup group = DynamicGroup.get(obj.get("group").getAsString());
        User user = User.findByUsername(obj.get("user").getAsString());
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(view(group.changeGroup(group.underlyingGroup().grant(user)), DynamicGroupJsonAdapter.class)).build();
    }

    @POST
    @Path("/dynamic/revoke")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUserFromDynamicGroup(String json) {
        accessControl("#managers");
        JsonObject obj = parse(json).getAsJsonObject();
        DynamicGroup group = DynamicGroup.get(obj.get("group").getAsString());
        User user = User.findByUsername(obj.get("user").getAsString());
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(view(group.changeGroup(group.underlyingGroup().revoke(user)), DynamicGroupJsonAdapter.class)).build();
    }

    public static class DynamicGroupJsonAdapter implements JsonViewer<DynamicGroup> {

        @Override
        public JsonElement view(DynamicGroup obj, JsonBuilder ctx) {
            JsonObject json = new JsonObject();
            json.addProperty("name", obj.getName());
            json.addProperty("displayName", obj.getName());
            JsonArray users = new JsonArray();

            for (User user : obj.getMembers()) {
                JsonObject userJson = new JsonObject();

                userJson.addProperty("username", user.getUsername());
                userJson.addProperty("name", user.getName());
                if (user.getProfile() != null) {
                    userJson.addProperty("avatar", user.getProfile().getAvatarUrl());
                }

                users.add(userJson);
            }

            json.add("members", users);
            return json;
        }

    }

}