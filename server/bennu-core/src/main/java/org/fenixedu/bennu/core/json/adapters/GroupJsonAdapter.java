package org.fenixedu.bennu.core.json.adapters;

import java.util.Comparator;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.security.Authenticate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@DefaultJsonAdapter(Group.class)
public class GroupJsonAdapter implements JsonAdapter<Group> {
    @Override
    public Group create(JsonElement json, JsonBuilder ctx) {
        return Group.parse(json.getAsJsonObject().get("expression").getAsString());
    }

    @Override
    public Group update(JsonElement json, Group obj, JsonBuilder ctx) {
        return Group.parse(json.getAsJsonObject().get("expression").getAsString());
    }

    @Override
    public JsonElement view(Group group, JsonBuilder ctx) {
        JsonObject object = new JsonObject();
        object.addProperty("expression", group.getExpression());
        object.addProperty("name", group.getPresentationName());
        object.addProperty("accessible", group.isMember(Authenticate.getUser()));
        return object;
    }

    public static class FullGroupJsonAdapter extends GroupJsonAdapter {

        @Override
        public JsonObject view(Group group, JsonBuilder ctx) {
            JsonObject object = super.view(group, ctx).getAsJsonObject();
            JsonArray users = new JsonArray();
            object.addProperty("userCount", group.getMembers().size());
            group.getMembers().stream().sorted(Comparator.comparing(User::getName))
                    .forEach(user -> users.add(new JsonPrimitive(user.getName() + " (" + user.getUsername() + ")")));
            object.add("users", users);
            return object;
        }
    }
}
