package org.fenixedu.bennu.core.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.stream.StreamUtils;

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
    public JsonElement grantUserMembershipGroup(@QueryParam("groupExpression") String groupExpression,
            @QueryParam("username") String username) {
        verifyAndGetRequestAuthor();
        User user = User.findByUsername(username);
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return view(Group.parse(groupExpression).grant(user));
    }

    @GET
    @Path("/revoke")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement revokeUserFromGroup(@QueryParam("groupExpression") String groupExpression,
            @QueryParam("username") String username) {
        verifyAndGetRequestAuthor();
        User user = User.findByUsername(username);
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return view(Group.parse(groupExpression).revoke(user));
    }

    @GET
    @Path("/dynamic")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement listDynamicGroups() {
        accessControl(Group.managers());
        return view(BennuGroupIndex.allDynamicGroups(), DynamicGroupJsonAdapter.class);
    }

    @POST
    @Path("/dynamic")
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonElement changeDynamicGroupName(JsonObject obj) {
        accessControl(Group.managers());
        DynamicGroup group = DynamicGroup.get(obj.get("group").getAsString());
        LocalizedString name = obj.has("name") ? LocalizedString.fromJson(obj.get("name")) : null;
        group.mutator().setPresentationName(name);
        return view(group, DynamicGroupJsonAdapter.class);
    }

    @POST
    @Path("/dynamic/grant")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement addUserToDynamicGroup(JsonObject obj) {
        accessControl(Group.managers());
        DynamicGroup group = DynamicGroup.get(obj.get("group").getAsString());
        User user = User.findByUsername(obj.get("user").getAsString());
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return view(group.mutator().changeGroup(group.underlyingGroup().grant(user)), DynamicGroupJsonAdapter.class);
    }

    @POST
    @Path("/dynamic/revoke")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement removeUserFromDynamicGroup(JsonObject obj) {
        accessControl(Group.managers());
        DynamicGroup group = DynamicGroup.get(obj.get("group").getAsString());
        User user = User.findByUsername(obj.get("user").getAsString());
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return view(group.mutator().changeGroup(group.underlyingGroup().revoke(user)), DynamicGroupJsonAdapter.class);
    }

    public static class DynamicGroupJsonAdapter implements JsonViewer<DynamicGroup> {

        @Override
        public JsonElement view(DynamicGroup obj, JsonBuilder ctx) {
            JsonObject json = new JsonObject();
            json.addProperty("name", obj.getName());
            LocalizedString customPresentationName = obj.toPersistentGroup().getCustomPresentationName();
            json.addProperty("displayName", customPresentationName == null ? obj.getName() : customPresentationName.getContent());
            if (customPresentationName != null) {
                json.add("customPresentationName", customPresentationName.json());
            }
            JsonArray users = obj.getMembers().map(user -> {
                JsonObject userJson = new JsonObject();
                userJson.addProperty("username", user.getUsername());
                userJson.addProperty("name", user.getProfile().getDisplayName());
                userJson.addProperty("avatar", user.getProfile().getAvatarUrl());
                return userJson;
            }).collect(StreamUtils.toJsonArray());

            json.add("members", users);
            return json;
        }

    }

}
